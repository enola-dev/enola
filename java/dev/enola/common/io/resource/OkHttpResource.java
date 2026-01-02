/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.enola.common.io.resource;

import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import dev.enola.common.FreedesktopDirectories;

import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.time.Duration;
import java.util.function.Supplier;

/**
 * Resource implemented with <a href="https://square.github.io/okhttp/">OkHttp</a>.
 *
 * <p>Prefer this over {@link UrlResource} (in general).
 */
public class OkHttpResource extends BaseResource implements ReadableResource {

    // TODO Fix potential resource leaks! See details in #byteSource().

    // TODO Re-design to open 1 instead 2 separate connections for mediaType & byteSource - how?!

    // TODO Better cache failed URLs instead of keep retrying! (If it is? Test...)

    // TODO java.net.http <https://openjdk.org/groups/net/httpclient/intro.html> alternative!

    // TODO https://kong.github.io/unirest-java/ (which uses JDK HttpClient itself?) alternative?

    // TODO https://github.com/mizosoft/methanol as alternative?

    private static final Logger LOG = LoggerFactory.getLogger(OkHttpResource.class);

    // This must be increased if there are test failures on slow CI servers :(
    private static final Duration t = Duration.ofMillis(7500);

    // https://square.github.io/okhttp/features/caching/
    private static final File cacheDir =
            new File(FreedesktopDirectories.CACHE_FILE, OkHttpResource.class.getSimpleName());
    private static final Cache cache = new Cache(cacheDir, 50L * 1024L * 1024L /* 50 MiB */);
    private static final HttpLoggingInterceptor httpLog = new HttpLoggingInterceptor();
    private static final OkHttpClient client =
            new OkHttpClient.Builder()
                    .cache(cache)
                    .addInterceptor(httpLog)
                    .callTimeout(t)
                    .connectTimeout(t)
                    .readTimeout(t)
                    .writeTimeout(t)
                    .build();

    static {
        httpLog.redactHeader("Authorization");
        httpLog.redactHeader("Cookie");
        httpLog.setLevel(HttpLoggingInterceptor.Level.BASIC);

        try {
            Files.createDirectories(cacheDir.toPath());
        } catch (IOException e) {
            LOG.warn("Failed to create cache directory: {}", cacheDir.getAbsolutePath(), e);
        }
    }

    private static final MediaTypeDetector mtd = new MediaTypeDetector();

    public static class Provider implements ResourceProvider {
        @Override
        public @Nullable Resource getResource(URI uri) {
            if (uri.getScheme().startsWith("http")) {
                return new ReadableButNotWritableDelegatingResource(new OkHttpResource(uri));
            } else return null;
        }
    }

    public OkHttpResource(String url) {
        super(URI.create(url), () -> mediaType(url));
    }

    public OkHttpResource(URI uri) {
        super(uri, () -> mediaType(uri.toString()));
    }

    private static Request newRequest(String url) {
        return new Request.Builder()
                .url(url)
                // It's polite to announce who we are...
                .addHeader("User-Agent", "enola.dev")
                // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept
                .addHeader("Accept", "*/*")
                .build();
    }

    // See also UrlResource#mediaType(URL url)
    private static MediaType mediaType(String url) {
        Request request = newRequest(url);
        try (var response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IllegalArgumentException(unsuccessfulMessage(url, response));
            var mt = response.body().contentType();
            if (mt != null) {
                return mtd.overwrite(URI.create(url), okToGuavaMediaType(mt));
            } else {
                throw new IllegalStateException("Success, but no Content-Type header: " + url);
            }

        } catch (IOException e) {
            throw new UncheckedIOException("IOException on " + url, e);
        }
    }

    private static MediaType okToGuavaMediaType(okhttp3.MediaType okMediaType) {
        // TODO Optimize?
        return MediaType.parse(okMediaType.toString());
    }

    @Override
    // TODO Re-design to fix resource leak... as-is, if you call this but then never call
    // the returned ByteSource's openStream(), then the OkHttp Response will never be closed! :(
    // This could be fixed by postponing actually opening the connection "down" into openStream().
    public ByteSource byteSource() {
        String url = uri().toString();
        Request request = newRequest(url);
        try {
            // Intentional not try-with-resource (but that's leaky & NOK; see above)
            var response = client.newCall(request).execute();

            // TODO How can this propagate connection errors and timeouts more clearly?
            if (response.isSuccessful())
                return new InputStreamByteSource(
                        () -> response.body().byteStream(), response::close);
            else return new ErrorByteSource(new IOException(unsuccessfulMessage(url, response)));
        } catch (IOException e) {
            return new ErrorByteSource(e);
        }
    }

    private static String unsuccessfulMessage(String url, Response response) {
        return response.code() + " " + url + " : " + response.message();
    }

    private static class InputStreamByteSource extends ByteSource {
        private final Supplier<InputStream> inputStreamSupplier;
        private final Runnable closer;

        InputStreamByteSource(Supplier<InputStream> inputStreamSupplier, Runnable closer) {
            this.inputStreamSupplier = inputStreamSupplier;
            this.closer = closer;
        }

        @Override
        public InputStream openStream() throws IOException {
            return new DelegatingClosingInputStream(inputStreamSupplier.get(), closer);
        }
    }

    private static class DelegatingClosingInputStream extends DelegatingInputStream {
        private final Runnable closer;

        protected DelegatingClosingInputStream(InputStream delegate, Runnable closer) {
            super(delegate);
            this.closer = closer;
        }

        @Override
        public void close() throws IOException {
            super.close();
            closer.run();
        }
    }

    private static class DelegatingInputStream extends InputStream {

        private final InputStream delegate;

        protected DelegatingInputStream(InputStream delegate) {
            this.delegate = delegate;
        }

        @Override
        public int available() throws IOException {
            return delegate.available();
        }

        @Override
        public void close() throws IOException {
            delegate.close();
        }

        @Override
        public void mark(int readlimit) {
            delegate.mark(readlimit);
        }

        @Override
        public boolean markSupported() {
            return delegate.markSupported();
        }

        public static InputStream nullInputStream() {
            return InputStream.nullInputStream();
        }

        public int read() throws IOException {
            return delegate.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return delegate.read(b);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return delegate.read(b, off, len);
        }

        @Override
        public byte[] readAllBytes() throws IOException {
            return delegate.readAllBytes();
        }

        @Override
        public int readNBytes(byte[] b, int off, int len) throws IOException {
            return delegate.readNBytes(b, off, len);
        }

        @Override
        public byte[] readNBytes(int len) throws IOException {
            return delegate.readNBytes(len);
        }

        @Override
        public void reset() throws IOException {
            delegate.reset();
        }

        @Override
        public long skip(long n) throws IOException {
            return delegate.skip(n);
        }

        @Override
        public void skipNBytes(long n) throws IOException {
            delegate.skipNBytes(n);
        }

        @Override
        public long transferTo(OutputStream out) throws IOException {
            return delegate.transferTo(out);
        }

        @Override
        public String toString() {
            return "DelegatingInputStream{" + "delegate=" + delegate + '}';
        }
    }
}

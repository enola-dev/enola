/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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

import dev.enola.common.io.mediatype.MediaTypeDetector;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.function.Supplier;

public class OkHttpResource extends BaseResource implements ReadableResource {

    // TODO Enable Caching to/from Disk!
    // https://square.github.io/okhttp/features/caching/
    // https://square.github.io/okhttp/5.x/okhttp/okhttp3/-ok-http-client/index.html

    public static final OkHttpClient client = new OkHttpClient();

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
        this(URI.create(url));
    }

    public OkHttpResource(URI uri) {
        super(uri, mediaType(uri));
    }

    private static MediaType mediaType(URI uri) {
        // TODO Improve this... the problem is close() !!
        String contentTypeFromServer = null;
        String encodingFromServer = null;
        return mtd.detect(contentTypeFromServer, encodingFromServer, uri);
    }

    @Override
    public ByteSource byteSource() {
        var request = new Request.Builder().url(uri().toString()).build();
        try {
            var response = client.newCall(request).execute();
            return new InputStreamByteSource(() -> response.body().byteStream(), response::close);
        } catch (IOException e) {
            return new ErrorByteSource(e);
        }
    }

    private static class InputStreamByteSource extends ByteSource {
        private final Supplier<InputStream> inputStreamSupplier;
        private final Runnable closer;

        public InputStreamByteSource(Supplier<InputStream> inputStreamSupplier, Runnable closer) {
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

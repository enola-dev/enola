/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2024 The Enola <https://enola.dev> Authors
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

import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import com.google.common.net.MediaType;

import dev.enola.common.io.iri.URIs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

/**
 * Resource implemented with {@link URL#openStream()}.
 *
 * <p>Consider using {@link OkHttpResource} instead.
 *
 * <p>This also the base class of {@link ClasspathResource}.
 */
public class UrlResource extends BaseResource implements ReadableResource {

    // TODO java.net.http <https://openjdk.org/groups/net/httpclient/intro.html> alternative!

    public enum Scheme {
        jar,
        http
    }

    public static class Provider implements ResourceProvider {

        private final ImmutableSet<Scheme> schemes;

        public Provider(Scheme... schemes) {
            this.schemes = ImmutableSet.copyOf(schemes);
        }

        @Override
        public Resource getResource(URI uri) {
            var uriScheme = uri.getScheme();
            for (var testScheme : schemes) {
                if (uriScheme.startsWith(testScheme.name())) {
                    try {
                        return new ReadableButNotWritableDelegatingResource(
                                new UrlResource(uri.toURL()));
                    } catch (MalformedURLException e) {
                        throw new IllegalArgumentException(
                                "Malformed http: URI is not valid URL" + uri, e);
                    }
                }
            }
            return null;
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(UrlResource.class);

    private static final MediaTypeDetector mtd = new MediaTypeDetector();

    private final URL url;

    /**
     * Constructor.
     *
     * @param uri URI of Resource; may be "logical", and e.g. include query parameters.
     * @param url URL to read; must be "physical", and typically does not include query parameters.
     * @param mediaType MediaType (incl. Charset)
     */
    public UrlResource(URI uri, URL url, MediaType mediaType) {
        super(uri, mediaType);
        this.url = url;
    }

    public UrlResource(URI uri, URL url) {
        this(uri, url, mediaType(url, null));
    }

    public UrlResource(URL url) {
        this(URIs.create(url), url, mediaType(url, null));
    }

    public UrlResource(URL url, MediaType mediaType) {
        super(URIs.create(url), mediaType);
        this.url = url;
    }

    @Deprecated // TODO Remove, as un-used and pointless? Review Test Coverage #1st...
    public UrlResource(URL url, Charset charset) {
        super(URIs.create(url), mediaType(url, charset));
        this.url = url;
    }

    private static MediaType mediaType(URL url, Charset charset) {
        // This is slow - but more accurate; see https://www.baeldung.com/java-file-mime-type
        URLConnection c = null;
        try {
            LOG.trace("mediaType: openConnection {}", url);
            c = url.openConnection();
            c.connect(); // MUST connect(), else failures are ignored!
            // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Content-Type-Options ?
            var contentTypeFromServer = c.getContentType();
            var encodingFromServer = c.getContentEncoding();

            // TODO If encodingFromServer == null, try extracting from contentTypeFromServer
            // https://stackoverflow.com/questions/3934251/urlconnection-does-not-get-the-charset

            if (encodingFromServer == null && charset != null) {
                encodingFromServer = charset.name();
            }

            return mtd.detect(contentTypeFromServer, encodingFromServer, url.toURI());

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            if (c instanceof HttpURLConnection) {
                ((HttpURLConnection) c).disconnect();
            }
        }
    }

    @Override
    public ByteSource byteSource() {
        return Resources.asByteSource(url);
    }
}

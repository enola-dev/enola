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

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.MediaTypeDetector;

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

public class UrlResource extends BaseResource implements ReadableResource {

    public static class Provider implements ResourceProvider {

        @Override
        public Resource getResource(URI uri) {
            if (uri.getScheme().startsWith("http")) {
                try {
                    return new ReadableButNotWritableDelegatingResource(
                            new UrlResource(uri.toURL()));
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException(
                            "Malformed http: URI is not valid URL" + uri, e);
                }
            } else return null;
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(UrlResource.class);

    private static final MediaTypeDetector mtd = new MediaTypeDetector();

    private final URL url;

    public UrlResource(URL url) {
        super(URI.create(url.toString()), mediaType(url, null));
        this.url = url;
    }

    public UrlResource(URL url, MediaType mediaType) {
        super(URI.create(url.toString()), mediaType);
        this.url = url;
    }

    @Deprecated // TODO Remove, as un-used and pointless? Review Test Coverage #1st...
    public UrlResource(URL url, Charset charset) {
        super(URI.create(url.toString()), mediaType(url, charset));
        this.url = url;
    }

    private static MediaType mediaType(URL url, Charset charset) {
        // This is slow - but more accurate; see https://www.baeldung.com/java-file-mime-type
        URLConnection c = null;
        try {
            LOG.debug("mediaType: openConnection {}", url);
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
        LOG.debug("byteSource: Resources.asByteSource {}", url);
        return Resources.asByteSource(url);
    }
}

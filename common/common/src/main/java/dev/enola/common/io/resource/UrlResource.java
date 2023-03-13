/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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

import java.io.IOException;
import java.net.*;

public class UrlResource implements ReadableResource {

    private static final MediaTypeDetector mtd = new MediaTypeDetector();

    private final URL url;
    private final URI uri;

    public UrlResource(URL url) {
        this.url = url;
        try {
            this.uri = url.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public MediaType mediaType() {
        // This is slow - but more accurate; see https://www.baeldung.com/java-file-mime-type
        URLConnection c = null;
        try {
            c = url.openConnection();
            // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Content-Type-Options ?
            var contentTypeFromServer = c.getContentType();
            var encodingFromServer = c.getContentEncoding();

            final var fc = c;
            return mtd.detect(
                    contentTypeFromServer, encodingFromServer, uri, () -> fc.getInputStream());
        } catch (IOException e) {
            return MediaTypeDetector.DEFAULT;
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

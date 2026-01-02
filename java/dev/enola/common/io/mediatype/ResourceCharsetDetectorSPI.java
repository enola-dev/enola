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
package dev.enola.common.io.mediatype;

import com.google.common.io.ByteSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Optional;

// TODO Increase test coverage... (this currently only used by YamlMediaType)
public abstract class ResourceCharsetDetectorSPI implements ResourceCharsetDetector {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceCharsetDetectorSPI.class);

    @Override
    public abstract Optional<Charset> detectCharset(URI uri, ByteSource source);

    /**
     * Peeks at the first N bytes of a resource.
     *
     * @return byte array of length up to N bytes, or shorter if the resource had less bytes, or
     *     there was an error reading from it
     */
    protected final byte[] peek(int n, URI uri, ByteSource source) {
        try {
            return source.slice(0, n).read();
        } catch (IOException e) {
            LOG.warn("Failed to peek at the first {} bytes", n, uri, e);
            return EMPTY; // skipcq: JAVA-S1049
        }
    }

    private static final byte[] EMPTY = new byte[0];
}

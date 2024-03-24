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
package dev.enola.common.io.mediatype;

import dev.enola.common.io.resource.AbstractResource;
import dev.enola.common.io.resource.ReadableResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

public abstract class ResourceCharsetDetectorSPI implements ResourceCharsetDetector {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceCharsetDetectorSPI.class);

    @Override
    public final Optional<Charset> detectCharset(AbstractResource resource) {
        if (resource instanceof ReadableResource) {
            return detectCharset((ReadableResource) resource);
        }
        return Optional.empty();
    }

    protected abstract Optional<Charset> detectCharset(ReadableResource resource);

    /**
     * Peeks at the first N bytes of resource.
     *
     * @return byte array of size, or null if the resource had less bytes, or there was an error
     *     reading from it
     */
    protected final byte[] peek(int n, ReadableResource resource) {
        try {
            final byte[] bytes = resource.byteSource().slice(0, n).read();
            if (bytes.length == n) return bytes;
            else return null;
        } catch (IOException e) {
            LOG.warn("Failed to peek at the first {} bytes of {}", n, resource.uri(), e);
            return null;
        }
    }
}

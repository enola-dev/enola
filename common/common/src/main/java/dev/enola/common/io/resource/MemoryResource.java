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

import static java.util.Objects.requireNonNull;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import java.net.URI;

public class MemoryResource implements Resource {

    private final URI uri;
    private final MediaType mediaType;
    private final MemoryByteSink memoryByteSink = new MemoryByteSink();

    protected MemoryResource(MediaType mediaType, URI uri) {
        this.uri = requireNonNull(uri, "uri");
        this.mediaType = requireNonNull(mediaType, "mediaType");
    }

    public MemoryResource(MediaType mediaType) {
        this.uri = URI.create("memory:" + Integer.toHexString(hashCode()));
        this.mediaType = requireNonNull(mediaType, "mediaType");
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public MediaType mediaType() {
        return mediaType;
    }

    @Override
    public ByteSink byteSink() {
        return memoryByteSink;
    }

    @Override
    public ByteSource byteSource() {
        return new MemoryByteSource(memoryByteSink.toByteArray());
    }

    @Override
    public String toString() {
        return "MemoryResource{mediaType="
                + mediaType
                + '}'
                + "@"
                + Integer.toHexString(hashCode());
    }
}

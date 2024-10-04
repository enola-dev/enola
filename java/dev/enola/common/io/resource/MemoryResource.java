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

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import java.net.URI;
import java.util.concurrent.atomic.AtomicLong;

public class MemoryResource extends BaseResource implements Resource {

    // TODO Provider implements ResourceProvider for memory: ?

    private static final AtomicLong counter = new AtomicLong();

    private final MemoryByteSink memoryByteSink = new MemoryByteSink();

    protected MemoryResource(URI uri, MediaType mediaType, boolean fixedMediaType) {
        super(uri, mediaType, fixedMediaType);
    }

    public MemoryResource(URI uri, MediaType mediaType) {
        super(uri, mediaType);
    }

    public MemoryResource(MediaType mediaType) {
        // TODO Unify this with TestResource create()
        this(URI.create("memory:" + Long.toHexString(counter.incrementAndGet())), mediaType);
    }

    @Override
    public ByteSink byteSink() {
        return memoryByteSink;
    }

    @Override
    public ByteSource byteSource() {
        return new MemoryByteSource(memoryByteSink.toByteArray());
    }
}

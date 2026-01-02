/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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

    private static String next() {
        return "memory:" + Long.toHexString(counter.incrementAndGet());
    }

    private static URI nextURI() {
        return URI.create(next());
    }

    private static URI nextURI(String querySuffix) {
        return URI.create(next() + "?" + querySuffix);
    }

    private final MemoryByteSink memoryByteSink = new MemoryByteSink();

    public MemoryResource(URI uri, MediaType mediaType) {
        super(uri, mediaType);
    }

    public MemoryResource(MediaType mediaType) {
        // TODO Unify this with TestResource create()
        this(nextURI(), mediaType);
    }

    public MemoryResource(MediaType mediaType, String querySuffix) {
        this(nextURI(querySuffix), mediaType);
    }

    @Override
    public ByteSink byteSink() {
        return memoryByteSink;
    }

    @Override
    public ByteSource byteSource() {
        return ByteSource.wrap(memoryByteSink.toByteArray());
    }
}

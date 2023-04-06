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

import com.google.common.net.MediaType;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class MemoryResourcePool {

    private static final Map<Long, CloseableMemoryResource> pool = new HashMap<>();
    private static final AtomicLong counter = new AtomicLong();

    public static CloseableMemoryResource create(MediaType mediaType) {
        var id = counter.get();
        var r = new CloseableMemoryResource(mediaType, URI.create("memory:" + id), id);
        pool.put(id, r);
        return r;
    }

    public static Resource get(String id) {
        var i = Long.parseLong(id);
        var r = pool.get(i);
        if (r == null) {
            throw new IllegalStateException("MemoryResourcePool already closed? #" + id);
        }
        return r;
    }

    public static class CloseableMemoryResource extends MemoryResource implements Closeable {
        private final long id;

        CloseableMemoryResource(MediaType mediaType, URI uri, long id) {
            super(mediaType, uri);
            this.id = id;
        }

        @Override
        public void close() throws IOException {
            pool.remove(id);
        }
    }
}

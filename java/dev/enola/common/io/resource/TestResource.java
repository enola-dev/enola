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

import com.google.common.net.MediaType;

import dev.enola.common.io.iri.URIs;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class TestResource extends MemoryResource implements CloseableResource {

    private static final String SCHEME = "test";

    private static final Map<Long, TestResource> pool = new HashMap<>();
    private static final AtomicLong counter = new AtomicLong();
    private final long id;

    private TestResource(MediaType mediaType, URI uri, long id) {
        super(uri, mediaType);
        this.id = id;
    }

    public static CloseableResource create(MediaType mediaType) {
        // TODO Unify this with MemoryResource constructor
        var id = counter.get();
        var uri = URI.create(SCHEME + ":" + id);
        var r = new TestResource(mediaType, uri, id);
        pool.put(id, r);
        return r;
    }

    @Override
    public void close() throws IOException {
        pool.remove(id);
    }

    public static class Provider implements ResourceProvider {

        @Override
        public Resource getResource(URI uri) {
            if (!SCHEME.equals(uri.getScheme())) return null;

            var id = URIs.getPath(uri);
            var i = Long.parseLong(id);
            var r = pool.get(i);
            if (r == null) {
                throw new IllegalStateException("TestResource already closed? URI=" + uri);
            }
            return r;
        }
    }
}

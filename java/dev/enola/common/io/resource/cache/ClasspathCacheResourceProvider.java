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
package dev.enola.common.io.resource.cache;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;

import dev.enola.common.io.resource.*;

import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.Map;

class ClasspathCacheResourceProvider implements ResourceProvider {

    record ClasspathLocationWithMediaType(String loc, MediaType mt) {}

    private final ImmutableMap<URI, ReadableResource> cache;

    ClasspathCacheResourceProvider(Map<URI, ClasspathLocationWithMediaType> map) {
        var builder = ImmutableMap.<URI, ReadableResource>builderWithExpectedSize(map.size());
        map.forEach(
                (uri, clwmt) ->
                        builder.put(
                                uri,
                                new DelegatingResource(
                                        new ClasspathResource(clwmt.loc, clwmt.mt),
                                        uri,
                                        clwmt.mt)));
        this.cache = builder.build();
    }

    @Override
    public @Nullable ReadableResource getReadableResource(URI uri) {
        return cache.get(uri);
    }

    @Override
    public @Nullable Resource getResource(URI uri) {
        return new ReadableButNotWritableDelegatingResource(getReadableResource(uri));
    }
}

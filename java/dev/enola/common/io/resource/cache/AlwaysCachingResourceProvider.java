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
package dev.enola.common.io.resource.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import dev.enola.common.io.resource.Resource;
import dev.enola.common.io.resource.ResourceProvider;

import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.Optional;

/**
 * AlwaysCachingResourceProvider is a {@link ResourceProvider} which caches everything.
 *
 * <p>Note that if the delegate ResourceProvider returns null because it could not get the resource,
 * this is cached as well ("persistent cache miss") - and not retried! (Unless Cache flows over, or
 * entry expires; of course.)
 */
public class AlwaysCachingResourceProvider implements ResourceProvider {

    // TODO Make Guava Cache configurable...

    // TODO Expose OpenTelemetry metrics: requests, hits, ...

    private final LoadingCache<URI, Optional<Resource>> cache =
            CacheBuilder.newBuilder()
                    .maximumSize(12345)
                    .build(
                            new CacheLoader<>() {
                                @Override
                                public Optional<Resource> load(URI iri) throws Exception {
                                    return Optional.ofNullable(delegate.getResource(iri));
                                }
                            });

    protected final ResourceProvider delegate;

    public AlwaysCachingResourceProvider(ResourceProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    public @Nullable Resource getResource(URI uri) {
        return cache.getUnchecked(uri).orElse(null);
    }
}

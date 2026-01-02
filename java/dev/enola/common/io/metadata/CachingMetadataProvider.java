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
package dev.enola.common.io.metadata;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.jspecify.annotations.Nullable;

public abstract class CachingMetadataProvider<T> implements MetadataProvider<T> {

    // This isn't used anywhere, yet - because it doesn't seem to actually really make
    // e.g. "time models/build.bash" any noticeable faster... optimizations in OkHttpResource are
    // better than this! Beware, test/profiling is *NOT* reproducible, because remote servers may
    // throttle!!

    // TODO Make Guava Cache configurable...

    private final MetadataProvider<T> delegate;

    private final LoadingCache<String, Metadata> getIRICache =
            CacheBuilder.newBuilder()
                    .maximumSize(12345)
                    .build(
                            new CacheLoader<>() {
                                @Override
                                public Metadata load(String iri) throws Exception {
                                    return delegate.get(iri);
                                }
                            });

    private final LoadingCache<ObjectIRI<T>, Metadata> getObjectIRICache =
            CacheBuilder.newBuilder()
                    .maximumSize(12345)
                    .build(
                            new CacheLoader<>() {
                                @Override
                                public Metadata load(ObjectIRI<T> objectIRI) throws Exception {
                                    return delegate.get(objectIRI.object, objectIRI.iri);
                                }
                            });

    protected CachingMetadataProvider(MetadataProvider<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Metadata get(String iri) {
        return getIRICache.getUnchecked(iri);
    }

    @Override
    public Metadata get(@Nullable T object, String iri) {
        return getObjectIRICache.getUnchecked(new ObjectIRI<T>(object, iri));
    }

    private record ObjectIRI<T>(@Nullable T object, String iri) {}
}

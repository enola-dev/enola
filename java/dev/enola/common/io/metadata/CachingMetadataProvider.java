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
package dev.enola.common.io.metadata;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.jspecify.annotations.Nullable;

public class CachingMetadataProvider implements MetadataProvider {

    // TODO Instead of working on this, first make OkHttpResource cache non-cacheable URLs!

    // TODO Beware, test/profiling is *very* unreliable, because remote servers seem to throttle!!
    // TODO Profile why using this class makes `time models/build.bash` ~5x slower, instead faster?!
    // (FYI Using a HashMap() instead of a LoadingCache still makes it ~x4 slower; and
    // ConcurrentHashMap ~7x slower.)

    // TODO Make Guava Cache configurable...

    private final MetadataProvider delegate;

    LoadingCache<String, Metadata> getIRICache =
            CacheBuilder.newBuilder()
                    .maximumSize(12345)
                    .build(
                            new CacheLoader<>() {
                                @Override
                                public Metadata load(String iri) throws Exception {
                                    return delegate.get(iri);
                                }
                            });

    LoadingCache<ObjectIRI, Metadata> getObjectIRICache =
            CacheBuilder.newBuilder()
                    .maximumSize(12345)
                    .build(
                            new CacheLoader<>() {
                                @Override
                                public Metadata load(ObjectIRI objectIRI) throws Exception {
                                    return delegate.get(objectIRI.object, objectIRI.iri);
                                }
                            });

    public CachingMetadataProvider(MetadataProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    public Metadata get(String iri) {
        return getIRICache.getUnchecked(iri);
    }

    @Override
    public Metadata get(@Nullable Object object, String iri) {
        return getObjectIRICache.getUnchecked(new ObjectIRI(object, iri));
    }

    private record ObjectIRI(@Nullable Object object, String iri) {}
}

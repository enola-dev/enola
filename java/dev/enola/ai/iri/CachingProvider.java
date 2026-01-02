/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.ai.iri;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.net.URI;
import java.util.Optional;

public class CachingProvider<T> implements Provider<T> {

    private final Provider<T> delegate;
    private final Iterable<String> uriTemplates;
    private final Iterable<URI> uriExamples;

    private final LoadingCache<URI, Optional<T>> cache =
            CacheBuilder.newBuilder()
                    .maximumSize(37)
                    .build(
                            new CacheLoader<>() {
                                @Override
                                public Optional<T> load(URI iri) {
                                    return delegate.optional(iri);
                                }
                            });

    public CachingProvider(Provider<T> delegate) {
        this.delegate = delegate;
        this.uriTemplates = delegate.uriTemplates();
        this.uriExamples = delegate.uriExamples();
    }

    @Override
    public String name() {
        return delegate.name();
    }

    @Override
    public String docURL() {
        return delegate.docURL();
    }

    @Override
    public Iterable<String> uriTemplates() {
        return this.uriTemplates;
    }

    @Override
    public Iterable<URI> uriExamples() {
        return this.uriExamples;
    }

    @Override
    public Optional<T> optional(URI uri) {
        return cache.getUnchecked(uri);
    }
}

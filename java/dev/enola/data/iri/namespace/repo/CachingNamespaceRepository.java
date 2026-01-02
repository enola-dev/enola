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
package dev.enola.data.iri.namespace.repo;

import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * {@link dev.enola.data.iri.namespace.repo.NamespaceRepository} which caches another one.
 *
 * <p>This is initialized one-time at construction; changes to the underlying NamespaceRepository
 * are NOT automatically updated and IGNORED.
 */
public class CachingNamespaceRepository implements NamespaceRepository {

    private final NamespaceRepository cache;

    CachingNamespaceRepository(NamespaceRepository slow) {
        var builder = new NamespaceRepositoryBuilder();
        builder.storeAll(slow.list());
        this.cache = builder.build();
    }

    @Override
    public Optional<String> getIRI(String prefix) {
        return cache.getIRI(prefix);
    }

    @Override
    public Iterable<String> listIRI() {
        return cache.listIRI();
    }

    @Override
    public @Nullable Namespace get(String iri) {
        return cache.get(iri);
    }
}

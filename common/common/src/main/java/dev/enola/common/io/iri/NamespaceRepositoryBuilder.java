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
package dev.enola.common.io.iri;

import com.google.common.collect.ImmutableSortedMap;

import dev.enola.repository.RepositoryBuilder;

import java.util.Optional;

public class NamespaceRepositoryBuilder extends RepositoryBuilder<Namespace> {

    protected final ImmutableSortedMap.Builder<String, String> prefixes =
            ImmutableSortedMap.naturalOrder();

    public NamespaceRepositoryBuilder addAll(Iterable<Namespace> namespaces) {
        namespaces.forEach(namespace -> add(namespace));
        return this;
    }

    public NamespaceRepositoryBuilder add(Namespace namespace) {
        var iri = namespace.iri();
        require(iri, "iri");
        add(iri, namespace);
        prefixes.put(namespace.prefix(), namespace.iri());
        return this;
    }

    public NamespaceRepositoryBuilder add(String prefix, String iri) {
        add(new ImmutableNamespace(prefix, iri));
        return this;
    }

    @Override
    public NamespaceRepository build() {
        return new ImmutableNamespaceRepository(items.buildOrThrow(), prefixes.buildOrThrow());
    }

    private class ImmutableNamespaceRepository extends RepositoryBuilder<Namespace>.RepositoryImpl
            implements NamespaceRepository {

        private final ImmutableSortedMap<String, String> prefixes;

        protected ImmutableNamespaceRepository(
                ImmutableSortedMap<String, Namespace> items,
                ImmutableSortedMap<String, String> prefixes) {
            super(items);
            this.prefixes = prefixes;
        }

        @Override
        public Optional<String> getIRI(String prefix) {
            return Optional.ofNullable(prefixes.get(prefix));
        }
    }
}

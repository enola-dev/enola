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
package dev.enola.datatype;

import com.google.common.collect.ImmutableSortedMap;

import dev.enola.data.RepositoryBuilder;

import java.util.Optional;

public class DatatypeRepositoryBuilder extends RepositoryBuilder<Datatype<?>> {

    @Override
    protected String getIRI(Datatype<?> datatype) {
        return require(datatype.iri(), "iri");
    }

    @Override
    public DatatypeRepositoryBuilder store(Datatype<?> item) {
        super.store(item);
        return this;
    }

    @Override
    public DatatypeRepositoryBuilder storeAll(Iterable<Datatype<?>> items) {
        super.storeAll(items);
        return this;
    }

    @Override
    public DatatypeRepository build() {
        return new ImmutableDatatypeRepository(buildMap());
    }

    private static class ImmutableDatatypeRepository
            extends RepositoryBuilder.RepositoryImpl<Datatype<?>> implements DatatypeRepository {

        protected ImmutableDatatypeRepository(ImmutableSortedMap<String, Datatype<?>> items) {
            super(items);
        }

        @Override
        public Optional<Datatype<?>> match(String text) {
            for (var datatype : list()) {
                var pattern = datatype.pattern();
                if (pattern.isEmpty()) continue;
                if (pattern.get().matcher(text).matches()) return Optional.of(datatype);
            }
            return Optional.empty();
        }
    }
}

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
package dev.enola.repository;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;

public class RepositoryBuilder<T> {

    protected final ImmutableSortedMap.Builder<String, T> items = ImmutableSortedMap.naturalOrder();

    protected void add(String iri, T item) {
        items.put(iri, item);
    }

    public Repository<T> build() {
        return new RepositoryImpl<T>(items.buildOrThrow());
    }

    protected <O> O require(O what, String identification) {
        if (what == null) throw new IllegalArgumentException("Missing required: " + identification);
        if (what instanceof String) {
            String whatString = (String) what;
            if (whatString.trim().isEmpty())
                throw new IllegalArgumentException("Empty: " + identification);
        }
        return what;
    }

    protected static class RepositoryImpl<T> implements Repository<T> {
        private final ImmutableSortedMap<String, T> items;

        protected RepositoryImpl(ImmutableSortedMap<String, T> items) {
            this.items = items;
        }

        @Override
        public ImmutableSet<String> listIRI() {
            return items.keySet();
        }

        @Override
        public T get(String iri) {
            return items.get(iri);
        }

        @Override
        public ImmutableCollection<T> list() {
            return items.values();
        }
    }
}

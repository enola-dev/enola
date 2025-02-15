/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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
package dev.enola.data;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import dev.enola.common.Builder;

import java.util.HashMap;
import java.util.Map;

/** RepositoryBuilder builds immutable {@link Repository} instances. */
public abstract class RepositoryBuilder<B extends RepositoryBuilder<B, T>, T>
        implements Store<RepositoryBuilder<B, T>, T>, Builder<Repository<T>> {

    private final Map<String, T> map = new HashMap<>();

    protected abstract String getIRI(T value);

    protected T merge(T existing, T update) {
        throw new UnsupportedOperationException("TODO Implement in subclass");
    }

    @Override
    public void merge(T item) {
        var iri = getIRI(item);
        var existing = map.putIfAbsent(iri, item);
        if (existing != null) map.put(iri, merge(existing, item));
    }

    @Override
    @CanIgnoreReturnValue
    @SuppressWarnings("unchecked")
    public final B store(T item) {
        var iri = getIRI(item);
        var existing = map.putIfAbsent(iri, item);
        if (existing != null)
            throw new IllegalArgumentException(
                    item
                            + " cannot replace "
                            + existing
                            + "; but consider using merge() instead of store()");
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public B storeAll(Iterable<T> items) { // skipcq: JAVA-W1016
        return (B) Store.super.storeAll(items);
    }

    @Override
    public Repository<T> build() {
        return new RepositoryImpl<>(buildMap());
    }

    protected ImmutableSortedMap<String, T> buildMap() {
        return ImmutableSortedMap.<String, T>naturalOrder().putAll(map).buildOrThrow();
    }

    protected <O> O require(O what, String identification) {
        if (what == null) throw new IllegalArgumentException("Missing required: " + identification);
        if (what instanceof String whatString) {
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

        @Override
        public String toString() {
            return "RepositoryImpl{" + "items=" + items.keySet() + '}';
        }
    }
}

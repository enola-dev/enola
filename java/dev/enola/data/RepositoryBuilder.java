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
package dev.enola.data;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;

import dev.enola.common.Builder;

import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * RepositoryBuilder builds immutable {@link Repository} instances.
 *
 * <p>This Builder class itself is NOT thread-safe. The {@link Repository} returned by its {@link
 * #build()} however is thread-safe (simply because it's immutable). Use {@link MemoryRepositoryRW}
 * for a thread-safe {@link Store}.
 */
public abstract class RepositoryBuilder<T> extends AbstractMapRepositoryRW<T>
        implements RepositoryRW<T>, Builder<Repository<T>> {

    private final Map<String, T> map = new HashMap<>();

    protected RepositoryBuilder(ImmutableList<Trigger<? extends T>> triggers) {
        super(triggers);
    }

    // TODO @Deprecated
    protected RepositoryBuilder() {
        this(ImmutableList.of());
    }

    @Override
    protected Map<String, T> map() {
        return map;
    }

    @Override
    public RepositoryBuilder<T> store(T item) {
        super.store(item);
        return this;
    }

    @Override
    public RepositoryBuilder<T> storeAll(Iterable<T> items) { // skipcq: JAVA-W1016
        super.storeAll(items);
        return this;
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
        public @Nullable T get(String iri) {
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

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
package dev.enola.data;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.ThreadSafe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MemoryRepositoryRW is an in-memory {@link RepositoryRW} implemented using a {@link
 * ConcurrentHashMap}.
 */
@ThreadSafe
public abstract class MemoryRepositoryRW<T> implements RepositoryRW<T> {

    private final Map<String, T> map = new ConcurrentHashMap<>();

    protected abstract String getIRI(T value);

    protected abstract T merge(T existing, T update);

    @Override
    public void merge(T item) {
        var iri = getIRI(item);
        var existing = map.putIfAbsent(iri, item);
        if (existing != null) map.put(iri, merge(existing, item));
    }

    @Override
    @CanIgnoreReturnValue
    public final Void store(T item) {
        if (map.putIfAbsent(getIRI(item), item) != null)
            throw new IllegalArgumentException(item.toString());
        return null;
    }

    @Override
    @CanIgnoreReturnValue
    public final Void store(Iterable<T> items) {
        for (T item : items) {
            store(item);
        }
        return null;
    }

    @Override
    public T get(String iri) {
        return map.get(iri);
    }

    @Override
    public Iterable<T> list() {
        return map.values();
    }

    @Override
    public Iterable<String> listIRI() {
        return map.keySet();
    }
}

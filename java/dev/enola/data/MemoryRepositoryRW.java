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

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.ThreadSafe;

import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MemoryRepositoryRW is an in-memory {@link RepositoryRW} implemented using a {@link
 * ConcurrentHashMap}.
 */
@ThreadSafe
public abstract class MemoryRepositoryRW<T> implements RepositoryRW<T> {

    // TODO Share more code between this and RepositoryBuilder, now that they're similar

    private final Map<String, T> map = new ConcurrentHashMap<>();
    private final ImmutableList<Trigger<T>> triggers;

    protected MemoryRepositoryRW(ImmutableList<Trigger<? extends T>> triggers) {
        this.triggers = hack(triggers);
    }

    // NB: Copy/pasted in RepositoryBuilder
    @SuppressWarnings("unchecked")
    private ImmutableList<Trigger<T>> hack(ImmutableList<Trigger<? extends T>> triggers) {
        var builder = ImmutableList.<Trigger<T>>builder();
        for (Trigger<? extends T> trigger : triggers) builder.add((Trigger<T>) trigger);
        return builder.build();
    }

    protected abstract String getIRI(T value);

    protected abstract T merge(T existing, T update);

    @Override
    public void merge(T item) {
        var iri = getIRI(item);
        var existing = map.putIfAbsent(iri, item);
        if (existing != null) {
            var merged = merge(existing, item);
            map.put(iri, merged);
            trigger(existing, merged);
        } else {
            trigger(null, item);
        }
    }

    @Override
    @CanIgnoreReturnValue
    public final MemoryRepositoryRW<T> store(T item) {
        var iri = getIRI(item);
        var existing = map.put(iri, item);
        trigger(existing, item);
        return this;
    }

    private void trigger(@Nullable T existing, T updated) {
        if (updated.equals(existing)) return;
        for (Trigger<T> trigger : triggers) {
            if (trigger.handles(updated)) trigger.updated(existing, updated);
        }
    }

    @Override
    public T get(String iri) {
        return map.get(requireNonNull(iri));
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

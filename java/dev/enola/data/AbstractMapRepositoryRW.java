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
package dev.enola.data;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import org.jspecify.annotations.Nullable;

import java.util.Map;

/** AbstractMapRepositoryRW is a RepositoryRW implementation that's backed by a Map. */
abstract class AbstractMapRepositoryRW<T> implements RepositoryRW<T> {

    private final ImmutableList<Trigger<T>> triggers;

    protected AbstractMapRepositoryRW(ImmutableList<Trigger<? extends T>> triggers) {
        this.triggers = hack(triggers);
    }

    protected abstract String getIRI(T value);

    protected abstract Map<String, T> map();

    @Override
    @CanIgnoreReturnValue
    public AbstractMapRepositoryRW<T> store(T item) {
        var iri = getIRI(item);
        var existing = map().put(iri, item);
        trigger(existing, item);
        return this;
    }

    @Override
    public Iterable<T> list() {
        return map().values();
    }

    @Override
    public Iterable<String> listIRI() {
        return map().keySet();
    }

    @Override
    public T get(String iri) {
        return map().get(requireNonNull(iri));
    }

    @SuppressWarnings("unchecked")
    private ImmutableList<Trigger<T>> hack(ImmutableList<Trigger<? extends T>> triggers) {
        var builder = ImmutableList.<Trigger<T>>builder();
        for (Trigger<? extends T> trigger : triggers) builder.add((Trigger<T>) trigger);
        return builder.build();
    }

    protected void trigger(@Nullable T existing, T updated) {
        if (updated.equals(existing)) return;
        for (Trigger<T> trigger : triggers) {
            if (trigger.handles(updated)) trigger.updated(existing, updated);
        }
    }
}

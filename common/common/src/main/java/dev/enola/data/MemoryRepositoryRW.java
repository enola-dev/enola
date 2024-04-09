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

    @Override
    public Void store(T value) {
        map.put(getIRI(value), value);
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

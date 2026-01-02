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

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.ThreadSafe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MemoryRepositoryRW is an in-memory {@link RepositoryRW} implemented using a {@link
 * ConcurrentHashMap}. It is suitable for us by multiple concurrent threads.
 */
@ThreadSafe
public abstract class MemoryRepositoryRW<T> extends AbstractMapRepositoryRW<T>
        implements RepositoryRW<T> {

    private final Map<String, T> map = new ConcurrentHashMap<>();

    protected MemoryRepositoryRW(ImmutableList<Trigger<? extends T>> triggers) {
        super(triggers);
    }

    @Override
    protected Map<String, T> map() {
        return map;
    }

    /*
       @Override
       protected void trigger(@Nullable T existing, T updated) {
           // TODO Run all triggers non-blocking in parallel background threads...
           super.trigger(existing, updated);
       }
    */
}

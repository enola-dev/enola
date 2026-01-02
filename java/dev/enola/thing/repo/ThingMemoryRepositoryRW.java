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
package dev.enola.thing.repo;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.ThreadSafe;

import dev.enola.data.MemoryRepositoryRW;
import dev.enola.data.Repository;
import dev.enola.data.Store;
import dev.enola.data.Trigger;
import dev.enola.thing.Thing;

/**
 * ThingMemoryRepositoryRW is an in-memory read &amp; write (i.e. both a {@link Store} and a {@link
 * Repository} of {@link Thing}s.
 *
 * <p>{@link ThingMemoryRepositoryROBuilder} is one of possibly several other alternatives for this.
 */
@ThreadSafe
public class ThingMemoryRepositoryRW extends MemoryRepositoryRW<Thing>
        implements ThingRepositoryStore {

    public ThingMemoryRepositoryRW(ImmutableList<Trigger<? extends Thing>> triggers) {
        super(triggers);
    }

    public ThingMemoryRepositoryRW() {
        this(ImmutableList.of());
    }

    @Override
    protected String getIRI(Thing value) {
        return value.iri();
    }
}

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

import dev.enola.data.RepositoryBuilder;
import dev.enola.data.Trigger;
import dev.enola.thing.Thing;

/**
 * Builds a read-only Repository of {@link Thing}s.
 *
 * <p>{@link ThingMemoryRepositoryRW} is one of possibly several other alternatives for this.
 */
public class ThingMemoryRepositoryROBuilder extends RepositoryBuilder<Thing>
        implements ThingRepositoryStore {

    public ThingMemoryRepositoryROBuilder(ImmutableList<Trigger<? extends Thing>> triggers) {
        super(triggers);
    }

    public ThingMemoryRepositoryROBuilder() {
        this(ImmutableList.of());
    }

    @Override
    protected String getIRI(Thing thing) {
        return require(thing.iri(), "iri");
    }

    @Override
    public ThingMemoryRepositoryROBuilder store(Thing item) {
        super.store(item);
        return this;
    }

    @Override
    public ThingMemoryRepositoryROBuilder storeAll(Iterable<Thing> items) {
        super.storeAll(items);
        return this;
    }

    @Override
    public ThingRepository build() {
        var thingRepository = super.build();
        return new ThingRepository() {

            @Override
            public Iterable<String> listIRI() {
                return thingRepository.listIRI();
            }

            @Override
            public Thing get(String iri) {
                return thingRepository.get(iri);
            }
        };
    }
}

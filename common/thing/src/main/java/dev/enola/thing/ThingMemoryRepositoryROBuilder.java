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
package dev.enola.thing;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import dev.enola.data.Repository;
import dev.enola.data.RepositoryBuilder;
import dev.enola.data.Store;

/**
 * Builds a read-only Repository of {@link Thing}s.
 *
 * <p>{@link ThingMemoryRepositoryRW} is one of possibly several other alternatives for this.
 */
public class ThingMemoryRepositoryROBuilder extends RepositoryBuilder<Thing>
        implements Store<ThingMemoryRepositoryROBuilder, Thing> {

    @Override
    @CanIgnoreReturnValue
    public ThingMemoryRepositoryROBuilder store(Thing thing) {
        String thingIRI = thing.iri();
        require(thingIRI, "iri");
        store(thingIRI, thing);
        return this;
    }

    @Override
    public ThingRepository build() {
        return wrap(super.build());
    }

    private ThingRepository wrap(final Repository<Thing> repository) {
        return new ThingRepository() {

            @Override
            public Iterable<String> listIRI() {
                return repository.listIRI();
            }

            @Override
            public Thing get(String iri) {
                return repository.get(iri);
            }
        };
    }
}

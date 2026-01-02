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

import static com.google.common.truth.Truth.assertThat;

import dev.enola.data.Store;
import dev.enola.thing.*;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.template.TemplateThingRepository;

import org.junit.Test;

public class ThingRepositoriesTest {

    public static final Thing testThing(Thing.Builder<?> builder) {
        return builder.iri("http://example.com")
                .set("http://example.com/message", "hello")
                .set("http://example.com/link", new Link("http://example.com"))
                .set("http://example.com/mls", new LangString("Saluton", "eo"))
                .set("http://example.com/lit", new Literal("k&ç#'", "test:type"))
                .add("http://example.com/list", new Link("http://example.com"))
                .add("http://example.com/list", new Literal("k&ç#'", "test:type"))
                .build();
    }

    private static final Thing TEST_THING = testThing(ImmutableThing.builder());

    private void checkStore(Store<Thing> thingStore) {
        // Store twice, still has to work
        thingStore.store(TEST_THING);
        thingStore.store(TEST_THING);
    }

    private void checkThingRepository(ThingRepository thingRepository) {
        assertThat(thingRepository.listIRI()).contains(TEST_THING.iri());
        assertThat(thingRepository.list()).contains(TEST_THING);
        assertThat(thingRepository.get(TEST_THING.iri())).isEqualTo(TEST_THING);
    }

    @Test
    public void memoryRepositoryRO() {
        var store = new ThingMemoryRepositoryROBuilder();
        checkStore(store);
        var readOnlyRepo = store.build();
        checkThingRepository(readOnlyRepo);
        checkThingRepository(new TemplateThingRepository(readOnlyRepo));
    }

    @Test
    public void memoryRepositoryRW() {
        var readWriteRepoStore = new ThingMemoryRepositoryRW();
        checkStore(readWriteRepoStore);
        checkThingRepository(readWriteRepoStore);
        checkThingRepository(new TemplateThingRepository(readWriteRepoStore));
    }
}

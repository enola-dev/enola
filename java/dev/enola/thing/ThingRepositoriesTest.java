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

import static com.google.common.truth.Truth.assertThat;

import static org.junit.Assert.assertThrows;

import dev.enola.data.Store;

import org.junit.Test;

public class ThingRepositoriesTest {

    static Thing TEST_THING =
            ImmutableThing.builder()
                    .iri("http://example.com")
                    .set("http://example.com/message", "hello")
                    .build();

    private void checkStore(Store<?, Thing> thingStore) {
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

        store.store(TEST_THING);
        assertThrows(IllegalArgumentException.class, () -> store.build());
    }

    @Test
    public void memoryRepositoryRW() {
        var readWriteRepoStore = new ThingMemoryRepositoryRW();
        checkStore(readWriteRepoStore);
        assertThrows(IllegalArgumentException.class, () -> readWriteRepoStore.store(TEST_THING));
        checkThingRepository(readWriteRepoStore);
    }
}

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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
package dev.enola.infer.rdf;

import com.google.common.collect.ImmutableList;
import dev.enola.thing.repo.ThingMemoryRepositoryROBuilder;
import dev.enola.thing.repo.ThingMemoryRepositoryRW;
import dev.enola.thing.repo.ThingRepositoryStore;
import org.junit.Test;

public class RDFSTriggersTest {

    @Test
    public void thingMemoryRepositoryRW() {
        thingRepositoryStore(
                new ThingMemoryRepositoryRW(ImmutableList.of(new RDFSPropertyTrigger())));
    }

    @Test
    public void thingMemoryRepositoryROBuilder() {
        thingRepositoryStore(
                new ThingMemoryRepositoryROBuilder(ImmutableList.of(new RDFSPropertyTrigger())));
    }

    void thingRepositoryStore(ThingRepositoryStore repo) {
        justOneProperty(repo);
        classAndProperties(repo);
        propertyClassProperty(repo);
        addRemove(repo);
    }

    void justOneProperty(ThingRepositoryStore repo) {}

    void classAndProperties(ThingRepositoryStore repo) {}

    void propertyClassProperty(ThingRepositoryStore repo) {}

    void addRemove(ThingRepositoryStore repo) {}
}

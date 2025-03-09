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
package dev.enola.core;

import dev.enola.core.thing.ThingConnector;
import dev.enola.thing.Thing;
import dev.enola.thing.proto.Things;
import dev.enola.thing.repo.ThingsRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ThingsRepositoryAdapter implements ThingsRepository {
    // TODO Remove?! Only (un)used in EnolaServiceRegistry

    private final ThingConnector thingConnector;

    public ThingsRepositoryAdapter(ThingConnector thingConnector) {
        this.thingConnector = thingConnector;
    }

    @Override
    public Stream<Thing> getThings(String iri) {
        /* TODO if (ListThingService.ENOLA_ROOT_LIST_IRIS.equals(iri)) {
           var thingBuilder = ImmutableThing.builderWithExpectedSize(1);
           thingBuilder.iri(iri);
           thingBuilder.set(ListThingService.ENOLA_ROOT_LIST_PROPERTY, null);
           return Stream.of(thingBuilder.build());
        */

        Things.Builder thingsBuilder = Things.newBuilder();
        Map<String, String> parameters = new HashMap<>(); // TODO !!!
        thingConnector.augment(thingsBuilder, iri, parameters);

        return Stream.of(); // TODO !!!
    }
}

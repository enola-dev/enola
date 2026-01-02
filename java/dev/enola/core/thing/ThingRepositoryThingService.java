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
package dev.enola.core.thing;

import com.google.protobuf.Any;

import dev.enola.common.context.TLC;
import dev.enola.core.EnolaException;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.datatype.DatatypeRepositoryBuilder;
import dev.enola.thing.Thing;
import dev.enola.thing.message.JavaThingToProtoThingConverter;
import dev.enola.thing.repo.ThingRepository;
import dev.enola.thing.repo.ThingsProvider;

import java.util.Map;

/**
 * ThingRepositoryThingService is an {@link ThingService} which delegates to a {@link
 * ThingRepository}.
 */
public class ThingRepositoryThingService implements ThingService {

    private final ThingsProvider thingsProvider;
    private final ThingRepository thingRepository;
    private final JavaThingToProtoThingConverter javaThingToProtoThingConverter;

    public ThingRepositoryThingService(
            ThingsProvider thingsProvider, ThingRepository thingRepository) {
        this.thingsProvider = thingsProvider;
        this.thingRepository = thingRepository;
        // TODO Implement looking up Datatypes in ThingRepository!
        DatatypeRepository datatypeRepository =
                TLC.optional(DatatypeRepository.class)
                        .orElseGet(() -> new DatatypeRepositoryBuilder().build());
        this.javaThingToProtoThingConverter =
                new JavaThingToProtoThingConverter(datatypeRepository);
    }

    @Override
    public Iterable<Thing> getThings(String iri, Map<String, String> parameters)
            throws EnolaException {
        return thingsProvider.getThings(iri).toList();
    }

    @Override
    public Any getThing(String iri, Map<String, String> parameters) {
        var javaThing = thingRepository.get(iri);
        if (javaThing == null) {
            throw new IllegalStateException("This should never happen: " + iri);
        }
        var protoThing = javaThingToProtoThingConverter.convert(javaThing);
        return Any.pack(protoThing.build());
    }
}

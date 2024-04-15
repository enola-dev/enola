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
package dev.enola.core.thing;

import com.google.protobuf.Any;

import dev.enola.core.EnolaException;
import dev.enola.core.proto.ListEntitiesRequest;
import dev.enola.core.proto.ListEntitiesResponse;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.datatype.DatatypeRepositoryBuilder;
import dev.enola.thing.ThingRepository;
import dev.enola.thing.message.JavaThingToProtoThingConverter;

import java.util.Map;

/**
 * ThingRepositoryThingService is an {@link ThingService} which delegates to a {@link
 * ThingRepository}.
 */
public class ThingRepositoryThingService implements ThingService {

    private final ThingRepository thingRepository;
    private final JavaThingToProtoThingConverter javaThingToProtoThingConverter;

    public ThingRepositoryThingService(ThingRepository thingRepository) {
        this.thingRepository = thingRepository;
        // TODO Replace empty DatatypeRepository with one which looks up in ThingRepository
        DatatypeRepository datatypeRepository = new DatatypeRepositoryBuilder().build();
        this.javaThingToProtoThingConverter =
                new JavaThingToProtoThingConverter(datatypeRepository);
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

    @Override
    public ListEntitiesResponse listEntities(ListEntitiesRequest r) throws EnolaException {
        throw new UnsupportedOperationException(
                "listEntities() will be removed, and never implemented here");
    }
}

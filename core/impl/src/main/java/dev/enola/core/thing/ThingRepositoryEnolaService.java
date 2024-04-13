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
import dev.enola.core.EnolaService;
import dev.enola.core.proto.GetThingRequest;
import dev.enola.core.proto.GetThingResponse;
import dev.enola.core.proto.ListEntitiesRequest;
import dev.enola.core.proto.ListEntitiesResponse;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.datatype.DatatypeRepositoryBuilder;
import dev.enola.thing.ThingRepository;
import dev.enola.thing.message.JavaThingToProtoThingConverter;

/**
 * ThingRepositoryEnolaService is an {@link EnolaService} which delegates to a {@link
 * ThingRepository}.
 */
public class ThingRepositoryEnolaService implements EnolaService {

    private final JavaThingToProtoThingConverter jt2ptConverter;
    private final ThingRepository thingRepository;

    public ThingRepositoryEnolaService(ThingRepository thingRepository) {
        // TODO Replace empty DatatypeRepository with one which looks up in ThingRepository
        this(thingRepository, new DatatypeRepositoryBuilder().build());
    }

    @Deprecated // TODO Remove, see above
    public ThingRepositoryEnolaService(
            ThingRepository thingRepository,
            // TODO Remove datatypeRepository arg, once Datatype implements Thing
            // (Because we can then use the thingRepository as a DatatypeRepository.)
            DatatypeRepository datatypeRepository) {
        this.thingRepository = thingRepository;
        this.jt2ptConverter = new JavaThingToProtoThingConverter(datatypeRepository);
    }

    @Override
    public GetThingResponse getThing(GetThingRequest r) throws EnolaException {
        var iri = r.getIri();
        var javaThing = thingRepository.get(iri);
        var responseBuilder = GetThingResponse.newBuilder();
        if (javaThing != null) {
            var protoThingBuilder = jt2ptConverter.convert(javaThing);
            var any = Any.pack(protoThingBuilder.build());
            responseBuilder.setThing(any);
        }
        return responseBuilder.build();
    }

    @Override
    public ListEntitiesResponse listEntities(ListEntitiesRequest r) throws EnolaException {
        throw new UnsupportedOperationException("Unimplemented method 'listEntities'");
    }
}

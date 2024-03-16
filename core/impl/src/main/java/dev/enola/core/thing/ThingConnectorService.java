/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2024 The Enola <https://enola.dev> Authors
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

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Any;
import com.google.protobuf.Message.Builder;

import dev.enola.core.EnolaException;
import dev.enola.core.EnolaService;
import dev.enola.core.meta.proto.Type;
import dev.enola.core.proto.GetThingRequest;
import dev.enola.core.proto.GetThingResponse;
import dev.enola.core.proto.ListEntitiesRequest;
import dev.enola.core.proto.ListEntitiesResponse;
import dev.enola.core.view.EnolaMessages;

public class ThingConnectorService implements EnolaService {

    private final Type type;
    private final ImmutableList<ThingConnector> registry;
    private final EnolaMessages enolaMessages;

    public ThingConnectorService(
            Type type, ImmutableList<ThingConnector> aspects, EnolaMessages enolaMessages) {
        this.type = type;
        this.registry = aspects;
        this.enolaMessages = requireNonNull(enolaMessages);
    }

    @Override
    public GetThingResponse getThing(GetThingRequest r) throws EnolaException {
        var eri = r.getIri();

        Builder thing = enolaMessages.newBuilder(type.getProto());

        for (var aspect : registry) {
            aspect.augment(thing, type);
        }

        var responseBuilder = GetThingResponse.newBuilder();
        responseBuilder.setThing(Any.pack(thing.build()));
        return responseBuilder.build();
    }

    @Override
    public ListEntitiesResponse listEntities(ListEntitiesRequest r) throws EnolaException {
        // TODO This doesn't make any sense for Things, and needs to be removed in future clean-up!
        return ListEntitiesResponse.newBuilder().build();
    }
}

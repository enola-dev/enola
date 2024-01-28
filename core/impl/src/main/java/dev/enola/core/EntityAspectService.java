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
package dev.enola.core;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Any;

import dev.enola.core.connector.proto.ConnectorServiceListRequest;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.Entity;
import dev.enola.core.proto.GetThingRequest;
import dev.enola.core.proto.GetThingResponse;
import dev.enola.core.proto.ListEntitiesRequest;
import dev.enola.core.proto.ListEntitiesResponse;

import java.util.ArrayList;

class EntityAspectService implements EnolaService {

    private final EntityKind entityKind;
    private final ImmutableList<EntityAspect> registry;

    public EntityAspectService(EntityKind entityKind, ImmutableList<EntityAspect> aspects) {
        this.entityKind = entityKind;
        this.registry = aspects;
    }

    @Override
    public GetThingResponse getThing(GetThingRequest r) throws EnolaException {
        var entity = Entity.newBuilder();
        var id = IDs.parse(r.getEri());
        entity.setId(id);

        for (var aspect : registry) {
            aspect.augment(entity, entityKind);
        }

        var responseBuilder = GetThingResponse.newBuilder();
        responseBuilder.setThing(Any.pack(entity.build()));
        return responseBuilder.build();
    }

    @Override
    public ListEntitiesResponse listEntities(ListEntitiesRequest r) throws EnolaException {
        var id = IDs.parse(r.getEri());
        var entities = new ArrayList<Entity.Builder>();
        var connectorRequest =
                ConnectorServiceListRequest.newBuilder()
                        .setId(id)
                        .putAllRelatedFilter(r.getRelatedFilterMap())
                        .build();

        for (var aspect : registry) {
            aspect.list(connectorRequest, entityKind, entities);
        }

        var responseBuilder = ListEntitiesResponse.newBuilder();
        for (var entity : entities) {
            responseBuilder.addEntities(entity);
        }
        return responseBuilder.build();
    }
}

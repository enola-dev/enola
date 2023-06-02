/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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

import dev.enola.core.connector.proto.ConnectorServiceListRequest;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.*;

import java.util.ArrayList;
import java.util.List;

class EntityAspectService implements EnolaService {

    private final EntityKind entityKind;
    private final List<EntityAspect> registry = new ArrayList<>();

    public EntityAspectService(EntityKind entityKind) {
        this.entityKind = entityKind;
    }

    // TODO Initialize to ImmutableList in constructor?
    void add(EntityAspect aspect) {
        registry.add(aspect);
    }

    @Override
    public GetEntityResponse getEntity(GetEntityRequest r) throws EnolaException {
        var entity = Entity.newBuilder();
        entity.setId(r.getId());

        for (var aspect : registry) {
            aspect.augment(entity, entityKind);
        }

        var response = GetEntityResponse.newBuilder().setEntity(entity).build();
        return response;
    }

    @Override
    public ListEntitiesResponse listEntities(ListEntitiesRequest r) throws EnolaException {
        var entities = new ArrayList<Entity.Builder>();
        var connectorRequest =
                ConnectorServiceListRequest.newBuilder()
                        .setId(r.getId())
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

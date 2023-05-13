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
package dev.enola.core.meta;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Any;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Timestamp;

import dev.enola.core.EnolaException;
import dev.enola.core.IDs;
import dev.enola.core.connector.proto.ConnectorServiceListRequest;
import dev.enola.core.meta.proto.Connector;
import dev.enola.core.meta.proto.Data;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.Entity;
import dev.enola.core.proto.ID;

import java.util.List;

public class EntityKindAspect implements EntityAspectWithRepository {

    private static final String TYPE_URL_PREFIX = "type.enola.dev";

    private static final Data SCHEMA_DATA =
            Data.newBuilder()
                    .setLabel("Model")
                    .setTypeUrl(TYPE_URL_PREFIX + "/" + EntityKind.getDescriptor().getFullName())
                    .build();

    private static final ID ENTITY_KIND_ENTITY_KIND_ID =
            ID.newBuilder().setNs("enola").setEntity("entity_kind").build();

    // This is the EntityKind of an EntityKind. This is not a typo.
    static final EntityKind ENTITY_KIND_ENTITY_KIND =
            EntityKind.newBuilder()
                    .setId(ID.newBuilder(ENTITY_KIND_ENTITY_KIND_ID).addPaths("name"))
                    .setEmoji("🕵🏾‍♀️")
                    .setLabel("Enola.dev Entity Kind")
                    .setDocUrl("https://docs.enola.dev/concepts/core-arch/")
                    .putData("schema", SCHEMA_DATA)
                    .addConnectors(
                            Connector.newBuilder().setJavaClass(EntityKindAspect.class.getName()))
                    .build();

    // TODO Use EntityKindRepository.CachedEntityKind.lastModified instead
    private static final Timestamp ONE_TIMESTAMP =
            Timestamp.newBuilder().setSeconds(1).setNanos(1).build();

    private EntityKindRepository ekr;

    @Override
    public void setEntityKindRepository(EntityKindRepository ekr) {
        this.ekr = ekr;
    }

    @Override
    public void augment(Entity.Builder entity, EntityKind entityKindEntityKind)
            throws EnolaException {
        entity.setTs(ONE_TIMESTAMP);

        var name = entity.getId().getPaths(0);
        var entityKindID = IDs.parse(name);
        var entityKind = ekr.get(entityKindID);
        var entityKindAsAny = Any.pack(entityKind, TYPE_URL_PREFIX);
        entity.putData("schema", entityKindAsAny);
    }

    @Override
    public void list(
            ConnectorServiceListRequest request,
            EntityKind entityKind,
            List<Entity.Builder> entities)
            throws EnolaException {
        for (var ekID : ekr.listID()) {
            var entityKindAsEntity = Entity.newBuilder();
            var name = IDs.toPath(IDs.withoutPath(ekID));
            entityKindAsEntity.setId(ID.newBuilder(ENTITY_KIND_ENTITY_KIND_ID).addPaths(name));
            augment(entityKindAsEntity, entityKind);
            entities.add(entityKindAsEntity);
        }
    }

    @Override
    public List<Descriptors.Descriptor> getDescriptors() throws EnolaException {
        return ImmutableList.of(
                Any.getDescriptor(),
                Timestamp.getDescriptor(),
                ID.getDescriptor(),
                EntityKind.getDescriptor());
    }
}

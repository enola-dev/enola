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

import static com.google.protobuf.Any.pack;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;

import dev.enola.core.EnolaException;
import dev.enola.core.EnolaServiceProvider;
import dev.enola.core.EntityAspect;
import dev.enola.core.connector.proto.ConnectorServiceListRequest;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.Entity;
import dev.enola.core.proto.ID;

import java.util.List;

public class SchemaAspect implements EntityAspect {

    // Matching schema.textproto
    static final ID.Builder idBuilderTemplate = ID.newBuilder().setNs("enola").setEntity("schema");

    private EnolaServiceProvider esp;

    public void setESP(EnolaServiceProvider enolaServiceProvider) {
        this.esp = enolaServiceProvider;
    }

    @Override
    public void augment(Entity.Builder entity, EntityKind entityKind) throws EnolaException {
        var name = entity.getId().getPaths(0);
        var descriptor = esp.getTypeRegistryWrapper().get().find(name).toProto();
        var any = pack(descriptor, "type.googleapis.com/");
        entity.putData("proto", any);
    }

    @Override
    public void list(
            ConnectorServiceListRequest request,
            EntityKind entityKind,
            List<Entity.Builder> entities)
            throws EnolaException {
        // TODO if (!IDs.withoutPath(request.getId()).equals(idBuilderTemplate)) return
        for (var name : esp.getTypeRegistryWrapper().names()) {
            var id = idBuilderTemplate.clone().addPaths(name);
            var newSchemaEntity = Entity.newBuilder().setId(id);
            augment(newSchemaEntity, null);
            entities.add(newSchemaEntity);
        }
    }

    public List<Descriptors.Descriptor> getDescriptors() throws EnolaException {
        return ImmutableList.of(DescriptorProtos.DescriptorProto.getDescriptor());
    }
}

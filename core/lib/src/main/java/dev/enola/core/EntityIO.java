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

import com.google.protobuf.Descriptors;

import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.protobuf.DescriptorRegistry;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.Entity;
import dev.enola.core.proto.EntityOrBuilder;

import java.io.IOException;

public class EntityIO {

    private final DescriptorRegistry descriptorRegistry;

    public EntityIO(DescriptorRegistry descriptorRegistry) {
        this.descriptorRegistry = descriptorRegistry;
    }

    public void write(EntityKind kind, Entity entity, WritableResource resource)
            throws IOException {
        protoIO(kind, entity).write(entity, resource);
    }

    public void read(EntityKind kind, ReadableResource resource, Entity.Builder entityBuilder)
            throws IOException {
        protoIO(kind, entityBuilder).read(resource, entityBuilder);
    }

    private ProtoIO protoIO(EntityKind kind, EntityOrBuilder entity) {
        var protoIO = ProtoIO.newBuilder();
        for (var dataKey : entity.getDataMap().keySet()) {
            var data = kind.getDataOrThrow(dataKey);
            var typeURL = data.getTypeUrl();
            Descriptors.Descriptor descriptor = descriptorRegistry.get(typeURL);
            protoIO.add(descriptor);
        }
        return protoIO.build();
    }
}

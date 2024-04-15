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
package dev.enola.core.view;

import com.google.common.collect.ImmutableMap;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.Message;

import dev.enola.common.protobuf.DescriptorProvider;
import dev.enola.common.protobuf.Messages;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.meta.proto.EntityKinds;
import dev.enola.core.proto.Entity;
import dev.enola.core.proto.ID;

public class EnolaMessages extends Messages {

    public EnolaMessages(
            DescriptorProvider descriptorProvider, ExtensionRegistryLite extensionRegistry) {
        super(descriptorProvider, extensionRegistry, enolaDefaultInstances());
    }

    public static ImmutableMap<String, Message> enolaDefaultInstances() {
        return ImmutableMap.<String, Message>builder()
                // thing.proto
                .put(
                        dev.enola.thing.proto.Thing.getDescriptor().getFullName(),
                        dev.enola.thing.proto.Thing.getDefaultInstance())
                .put(
                        dev.enola.thing.proto.Things.getDescriptor().getFullName(),
                        dev.enola.thing.proto.Things.getDefaultInstance())

                // enola_core.proto
                .put(Entity.getDescriptor().getFullName(), Entity.getDefaultInstance())
                .put(ID.getDescriptor().getFullName(), ID.getDefaultInstance())

                // enola_meta.proto
                .put(EntityKinds.getDescriptor().getFullName(), EntityKinds.getDefaultInstance())
                .put(EntityKind.getDescriptor().getFullName(), EntityKind.getDefaultInstance())
                .build();
    }
}

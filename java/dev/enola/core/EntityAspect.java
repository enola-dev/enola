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

import com.google.protobuf.Descriptors;

import dev.enola.core.connector.proto.ConnectorServiceListRequest;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.Entity;

import java.util.Collections;
import java.util.List;

/**
 * API for in-process Entity "connectors". This is the internal equivalent of the gRPC
 * ConnectorService.
 */
public interface EntityAspect {
    // TODO Move (refactor) this into a package dev.enola.core.entity.

    void augment(Entity.Builder entity, EntityKind entityKind) throws EnolaException;

    void list(
            ConnectorServiceListRequest request,
            EntityKind entityKind,
            List<Entity.Builder> entities)
            throws EnolaException;

    default List<Descriptors.Descriptor> getDescriptors() throws EnolaException {
        return Collections.emptyList();
    }
}

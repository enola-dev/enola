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
import dev.enola.core.proto.Entity;

import java.util.List;

/**
 * An {@link EntityAspect} which calls {@link EntityAspect#augment(Entity.Builder, EntityKind)} for
 * each entity in its default {@link EntityAspect#list(ConnectorServiceListRequest, EntityKind,
 * List)} implementation.
 */
public interface EntityAspectRepeater extends EntityAspect {
    @Override
    default void list(
            ConnectorServiceListRequest request,
            EntityKind entityKind,
            List<Entity.Builder> entities)
            throws EnolaException {

        for (var entity : entities) {
            augment(entity, entityKind);
        }
    }
}

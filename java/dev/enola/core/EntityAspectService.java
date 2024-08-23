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

import dev.enola.common.convert.ConversionException;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.Entity;
import dev.enola.core.thing.ThingService;
import dev.enola.thing.Thing;

import java.util.Map;

// TODO Move (refactor) this into a package dev.enola.core.entity.
class EntityAspectService implements ThingService {

    private final EntityKind entityKind;
    private final ImmutableList<EntityAspect> registry;

    public EntityAspectService(EntityKind entityKind, ImmutableList<EntityAspect> aspects) {
        this.entityKind = entityKind;
        this.registry = aspects;
    }

    @Override
    public Iterable<Thing> getThings(String iri, Map<String, String> parameters)
            throws EnolaException {
        throw new UnsupportedOperationException(
                "Not implemented, because this class is about to be removed anyways!");
    }

    @Override
    public Any getThing(String iri, Map<String, String> parameters) {
        var entity = Entity.newBuilder();
        var id = IDs.parse(iri);
        entity.setId(id);

        for (var aspect : registry) {
            try {
                aspect.augment(entity, entityKind);
            } catch (EnolaException e) {
                // TODO Temporary workaround, until EnolaException is removed everywhere
                throw new ConversionException(iri, e);
            }
        }

        return Any.pack(entity.build());
    }

    @Override
    public String toString() {
        return "EntityAspectService{entityKind=" + entityKind + "}";
    }
}

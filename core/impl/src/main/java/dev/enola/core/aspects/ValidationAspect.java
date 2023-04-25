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
package dev.enola.core.aspects;

import com.google.protobuf.Descriptors;

import dev.enola.common.protobuf.MessageValidator;
import dev.enola.common.protobuf.MessageValidators;
import dev.enola.common.protobuf.ValidationException;
import dev.enola.core.EnolaException;
import dev.enola.core.EntityAspect;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.Entity;
import dev.enola.core.proto.EntityOrBuilder;

import java.util.Set;

public class ValidationAspect
        implements EntityAspect, MessageValidator<EntityKind, EntityOrBuilder> {

    @Override
    public void augment(Entity.Builder entity, EntityKind entityKind) throws EnolaException {
        var results = MessageValidators.Result.newBuilder();
        try {
            this.validate(entityKind, entity, results);
            results.build().throwIt();
        } catch (ValidationException e) {
            throw new EnolaException(e);
        }
    }

    @Override
    public void validate(
            EntityKind kind, EntityOrBuilder entity, MessageValidators.Result.Builder r) {
        validateKeys(
                Entity.getDescriptor().findFieldByNumber(Entity.RELATED_FIELD_NUMBER),
                kind.getRelatedMap().keySet(),
                entity.getRelatedMap().keySet(),
                r);
        validateKeys(
                Entity.getDescriptor().findFieldByNumber(Entity.LINK_FIELD_NUMBER),
                kind.getLinkMap().keySet(),
                entity.getLinkMap().keySet(),
                r);
        validateKeys(
                Entity.getDescriptor().findFieldByNumber(Entity.DATA_FIELD_NUMBER),
                kind.getDataMap().keySet(),
                entity.getDataMap().keySet(),
                r);
    }

    private void validateKeys(
            Descriptors.FieldDescriptor field,
            Set<String> kind,
            Set<String> entity,
            MessageValidators.Result.Builder r) {
        entity.forEach(
                key -> {
                    if (!kind.contains(key)) {
                        r.add(field, key + " Map key unknown in EntityType: " + kind);
                    }
                });
    }
}

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

import com.github.fge.uritemplate.URITemplate;
import com.github.fge.uritemplate.URITemplateException;
import com.github.fge.uritemplate.URITemplateParseException;
import com.github.fge.uritemplate.vars.VariableMap;
import com.google.common.collect.ImmutableMap;

import dev.enola.common.protobuf.ValidationException;
import dev.enola.common.validation.Validation;
import dev.enola.common.validation.Validations;
import dev.enola.core.EnolaException;
import dev.enola.core.EntityAspect;
import dev.enola.core.IDs;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.Entity;

import java.util.Map;

public class UriTemplateAspect implements EntityAspect {

    private final Map<String, URITemplate> templates;

    public UriTemplateAspect(EntityKind entityKind) throws ValidationException {
        var mapBuilder = ImmutableMap.<String, URITemplate>builder();
        for (var entry : entityKind.getLinkMap().entrySet()) {
            var key = entry.getKey();
            try {
                mapBuilder.put(key, new URITemplate(entry.getValue().getUriTemplate()));
            } catch (URITemplateParseException e) {
                var v1 =
                        Validation.newBuilder()
                                .setPath("link." + key)
                                .setError(e.getMessage())
                                .build();
                var vs = Validations.newBuilder().addValidations(v1).build();
                throw new ValidationException(vs);
            }
        }
        this.templates = mapBuilder.build();
    }

    @Override
    public void augment(Entity.Builder entity, EntityKind entityKind) throws EnolaException {
        for (var key : entityKind.getLinkMap().keySet()) {
            var variablesBuilder = VariableMap.newBuilder();
            var map = IDs.pathMap(entityKind.getId(), entity.getId());
            for (var entry : map.entrySet()) {
                variablesBuilder.addScalarValue("path." + entry.getKey(), entry.getValue());
            }
            VariableMap variables = variablesBuilder.freeze();
            try {
                var uri = templates.get(key).toString(variables);
                entity.putLink(key, uri);
            } catch (URITemplateException e) {
                // TODO Add "context", e.g. which key for which Entity ID failed?
                throw new EnolaException(e);
            }
        }
    }
}

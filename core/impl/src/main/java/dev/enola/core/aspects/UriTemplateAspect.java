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
package dev.enola.core.aspects;

import static com.google.common.base.Strings.isNullOrEmpty;

import com.github.fge.uritemplate.URITemplate;
import com.github.fge.uritemplate.URITemplateException;
import com.github.fge.uritemplate.URITemplateParseException;
import com.github.fge.uritemplate.vars.VariableMap;
import com.google.common.collect.ImmutableMap;

import dev.enola.common.protobuf.ValidationException;
import dev.enola.common.validation.Validation;
import dev.enola.common.validation.Validations;
import dev.enola.core.EnolaException;
import dev.enola.core.EntityAspectRepeater;
import dev.enola.core.IDs;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.Entity;
import dev.enola.core.proto.ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// TODO Rename (refactor) to UriTemplateEntityAspect
// TODO Move (refactor) this into a package dev.enola.core.entity.aspect
public class UriTemplateAspect implements EntityAspectRepeater {

    // TODO Fail if an URI Template refers to an unknown variable/value (as-is it's just ignored)

    private final Map<String, URITemplate> linkTemplates;
    private final Map<String, IdTemplates> relatedTemplates;

    public UriTemplateAspect(EntityKind entityKind) throws ValidationException {
        // Parse all EntityKind.Links #uri_templates
        var linkMapBuilder = ImmutableMap.<String, URITemplate>builder();
        for (var entry : entityKind.getLinkMap().entrySet()) {
            var key = entry.getKey();
            var template = entry.getValue().getUriTemplate();
            if (!isNullOrEmpty(template)) {
                linkMapBuilder.put(key, createURITemplate(template, key));
            }
        }
        this.linkTemplates = linkMapBuilder.build();

        // Parse all EntityKind.EntityRelationships #ID templates
        var relatedMapBuilder = ImmutableMap.<String, IdTemplates>builder();
        for (var entry : entityKind.getRelatedMap().entrySet()) {
            var key = entry.getKey();
            var t = new IdTemplates();
            t.ns = createURITemplate(entry.getValue().getId().getNs(), key);
            t.entity = createURITemplate(entry.getValue().getId().getEntity(), key);
            for (var path : entry.getValue().getId().getPathsList()) {
                t.paths.add(createURITemplate(path, key));
            }
            relatedMapBuilder.put(key, t);
        }
        this.relatedTemplates = relatedMapBuilder.build();
    }

    private URITemplate createURITemplate(String template, String key) throws ValidationException {
        try {
            return new URITemplate(template);
        } catch (URITemplateParseException e) {
            var v1 =
                    Validation.newBuilder().setPath("link." + key).setError(e.getMessage()).build();
            var vs = Validations.newBuilder().addValidations(v1).build();
            throw new ValidationException(vs);
        }
    }

    @Override
    public void augment(Entity.Builder entity, EntityKind entityKind) throws EnolaException {
        // TODO Performance: Skip entirely if model has no uriTemplate...

        // Prepare all available template placeholder variables
        var variablesBuilder = VariableMap.newBuilder();
        var map = IDs.pathMap(entityKind.getId(), entity.getId());
        for (var entry : map.entrySet()) {
            variablesBuilder.addScalarValue("path." + entry.getKey(), entry.getValue());
        }
        VariableMap variables = variablesBuilder.freeze();

        try {
            // Set an Entity's #link-s based on EntityKind.Link's #uri_template
            for (var key : entityKind.getLinkMap().keySet()) {
                // Only if no other Aspect (Connector) already set this Link
                if (!entity.containsLink(key)) {
                    var template = linkTemplates.get(key);
                    if (template != null) {
                        var uri = template.toString(variables);
                        entity.putLink(key, uri);
                    }
                }
            }

            // Set an Entity's #related-s based on EntityKind.EntityRelationship's #ID
            for (var key : entityKind.getRelatedMap().keySet()) {
                // Only if no other Aspect (Connector) already set this EntityRelationship
                if (!entity.containsRelated(key)) {
                    var templates = relatedTemplates.get(key);
                    var id = ID.newBuilder();
                    id.setNs(templates.ns.toString(variables));
                    id.setEntity(templates.entity.toString(variables));
                    for (var path : templates.paths) {
                        id.addPaths(path.toString(variables));
                    }
                    entity.putRelated(key, id.build());
                }
            }

        } catch (URITemplateException e) {
            // TODO Add "context", e.g. which key for which Entity ID failed?
            throw new EnolaException(e);
        }
    }

    private static class IdTemplates {
        URITemplate ns;
        URITemplate entity;
        List<URITemplate> paths = new ArrayList<>(3);
    }
}

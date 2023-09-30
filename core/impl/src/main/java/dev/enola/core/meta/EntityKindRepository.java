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

import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTOBUF_TEXTPROTO_UTF_8;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.ErrorResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.protobuf.MessageValidators;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.common.protobuf.ValidationException;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.meta.proto.EntityKinds;
import dev.enola.core.proto.ID;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class EntityKindRepository {

    private static final MessageValidators v = EntityKindValidations.INSTANCE;
    private final Map<String, Map<String, CachedEntityKind>> map = new TreeMap<>();

    public EntityKindRepository() {
        try {
            put(EntityKindAspect.ENTITY_KIND_ENTITY_KIND);
            try {
                load(new ClasspathResource("schema.textproto", PROTOBUF_TEXTPROTO_UTF_8));
            } catch (IOException e) {
                throw new IllegalStateException("Built-in ClasspathResource missing?!", e);
            }
        } catch (ValidationException e) {
            // This cannot happen, because the built-in kinds are valid.
            throw new IllegalStateException("BUG!", e);
        }
    }

    public EntityKindRepository put(EntityKind entityKind) throws ValidationException {
        var results = MessageValidators.Result.newBuilder();
        put(entityKind, ErrorResource.INSTANCE, Optional.empty(), results);
        validateFinal(results);
        return this;
    }

    private synchronized void put(
            EntityKind entityKind,
            ReadableResource resource,
            Optional<Instant> lastModified,
            MessageValidators.Result.Builder r)
            throws ValidationException {
        var id = entityKind.getId();

        // TODO Separately validating ID first should eventually not be required anymore,
        // because Validation should "recurse into" messages by itself, later.
        v.validate(id, r);
        v.validate(entityKind, r);

        map.computeIfAbsent(id.getNs(), s -> new TreeMap<>())
                .put(id.getEntity(), new CachedEntityKind(entityKind, resource, lastModified));
    }

    public Optional<EntityKind> getOptional(ID id) {
        var subMap = map.get(id.getNs());
        if (subMap == null) {
            return Optional.empty();
        }
        final var cachedEntityKind = subMap.get(id.getEntity());
        if (cachedEntityKind == null) {
            return Optional.empty();
        }
        final EntityKind[] finalEntityKind = new EntityKind[] {cachedEntityKind.entityKind};
        cachedEntityKind.lastModified.ifPresent(
                originalLastModified -> {
                    cachedEntityKind
                            .resource
                            .lastModifiedIfKnown()
                            .ifPresent(
                                    currentLastModified -> {
                                        if (currentLastModified.isAfter(originalLastModified)) {
                                            try {
                                                load(cachedEntityKind.resource);
                                                finalEntityKind[0] =
                                                        subMap.get(id.getEntity()).entityKind;
                                            } catch (IOException | ValidationException e) {
                                                throw new IllegalStateException(
                                                        "Reload failed: "
                                                                + cachedEntityKind.resource.uri(),
                                                        e);
                                            }
                                        }
                                    });
                });
        return Optional.of(finalEntityKind[0]);
    }

    public EntityKind get(ID id) {
        return getOptional(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(id + " unknown; available are: " + map));
    }

    public EntityKindRepository load(ReadableResource resource)
            throws IOException, ValidationException {
        return load(List.of(resource));
    }

    public EntityKindRepository load(Iterable<ReadableResource> resources)
            throws IOException, ValidationException {
        var results = MessageValidators.Result.newBuilder();
        for (var resource : resources) {
            var lastModified = resource.lastModifiedIfKnown();
            var kinds = new ProtoIO().read(resource, EntityKinds.newBuilder()).build();
            for (var kind : kinds.getKindsList()) {
                put(kind, resource, lastModified, results);
            }
        }
        validateFinal(results);
        return this;
    }

    /**
     * Final validation, for consistency of all models and the references across all of their {@link
     * #load(ReadableResource)}-ed resources. Throws ValidationException if r has any problems.
     */
    private void validateFinal(MessageValidators.Result.Builder r) throws ValidationException {
        var eks = EntityKinds.newBuilder();
        list().forEach(ek -> eks.addKinds(ek));
        v.validate(this, eks, r);
        r.build().throwIt();
    }

    public Collection<EntityKind> list() {
        var eks = new ArrayList<EntityKind>();
        map.values()
                .forEach(
                        innerMap ->
                                innerMap.values().stream()
                                        .map(CachedEntityKind::entityKind)
                                        .forEach(eks::add));
        return eks;
    }

    public List<ID> listID() {
        var ids = new ArrayList<ID>();
        map.values()
                .forEach(
                        innerMap ->
                                innerMap.values().stream()
                                        .map(CachedEntityKind::entityKind)
                                        .map(EntityKind::getId)
                                        .forEach(ids::add));
        return ids;
    }

    private static final class CachedEntityKind {
        final EntityKind entityKind;
        final ReadableResource resource;
        final Optional<Instant> lastModified;

        CachedEntityKind(
                EntityKind entityKind, ReadableResource resource, Optional<Instant> lastModified) {
            this.entityKind = entityKind;
            this.resource = resource;
            this.lastModified = lastModified;
        }

        EntityKind entityKind() {
            return entityKind;
        }

        @Override
        public String toString() {
            return "CachedEntityKind{"
                    + "entityKind="
                    + entityKind
                    + ", resource="
                    + resource
                    + ", lastModified="
                    + lastModified
                    + '}';
        }
    }
}

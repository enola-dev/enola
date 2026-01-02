/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.model.enola.meta.io;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableMap;

import dev.enola.model.enola.meta.*;
import dev.enola.model.enola.meta.Class;
import dev.enola.thing.impl.MutableThing;
import dev.enola.thing.java.ProxyTBF;
import dev.enola.thing.java.TBF;
import dev.enola.thing.repo.id.ThingByIdProvider;

import java.util.HashMap;
import java.util.Map;

public class MetaThingByIdProvider extends Abstract implements ThingByIdProvider {

    // skipcq: JAVA-E0169
    static class Builder implements dev.enola.common.Builder<MetaThingByIdProvider> {

        private final TBF tbf = new ProxyTBF(MutableThing.FACTORY);

        private final Map<String, Schema.Builder> schemas = new HashMap<>();
        private final Map<String, Datatype.Builder> datatypes = new HashMap<>();
        private final Map<String, Property.Builder> properties = new HashMap<>();
        private final Map<String, Class.Builder> classes = new HashMap<>();

        Schema.Builder<Schema> schema(String id) {
            return schemas.computeIfAbsent(id, _id -> Schema.builder(tbf).id(_id));
        }

        Datatype.Builder<Datatype> datatype(Schema schema, String name) {
            var id = requireNonNull(schema, "schema").id() + "." + requireNonNull(name, "name");
            return datatypes.computeIfAbsent(id, _id -> init(Datatype.builder(tbf), schema, name));
        }

        Property.Builder<Property> property(Schema schema, String name) {
            var id = requireNonNull(schema).id() + "." + requireNonNull(name);
            return properties.computeIfAbsent(id, _id -> init(Property.builder(tbf), schema, name));
        }

        Class.Builder<Class> clazz(Schema schema, String name) {
            var id = requireNonNull(schema).id() + "." + requireNonNull(name);
            return classes.computeIfAbsent(id, _id -> init(Class.builder(tbf), schema, name));
        }

        private <T extends Type.Builder<?>> T init(T builder, Schema schema, String name) {
            builder.schema(schema).name(name);
            builder.iri(schema.iri() + "/" + name);
            return builder;
        }

        @Override
        public MetaThingByIdProvider build() {
            return new MetaThingByIdProvider(
                    build(this.schemas),
                    build(this.datatypes),
                    build(this.properties),
                    build(this.classes));
        }

        private <B> ImmutableMap<String, B> build(Map<String, ?> map) {
            ImmutableMap.Builder<String, B> mapBuilder =
                    ImmutableMap.builderWithExpectedSize(map.size());
            map.forEach(
                    (id, builder) ->
                            mapBuilder.put(id, (B) ((dev.enola.common.Builder) builder).build()));
            return mapBuilder.build();
        }
    }

    private final ImmutableMap<String, Schema> schemas;
    private final ImmutableMap<String, Datatype> datatypes;
    private final ImmutableMap<String, Property> properties;
    private final ImmutableMap<String, Class> classes;

    private MetaThingByIdProvider(
            ImmutableMap<String, Schema> schemas,
            ImmutableMap<String, Datatype> datatypes,
            ImmutableMap<String, Property> properties,
            ImmutableMap<String, Class> classes) {
        this.schemas = schemas;
        this.datatypes = datatypes;
        this.properties = properties;
        this.classes = classes;
    }

    @Override
    Map<String, Schema> schemas() {
        return schemas;
    }

    @Override
    Map<String, Datatype> datatypes() {
        return datatypes;
    }

    @Override
    Map<String, Property> properties() {
        return properties;
    }

    @Override
    Map<String, Class> classes() {
        return classes;
    }
}

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
package dev.enola.model.enola.meta.bootstrap;

import dev.enola.common.context.TLC;
import dev.enola.model.enola.meta.Class;
import dev.enola.model.enola.meta.Datatype;
import dev.enola.model.enola.meta.Property;
import dev.enola.model.enola.meta.Schema;
import dev.enola.thing.repo.ThingProvider;

import java.util.HashSet;
import java.util.Set;

public class MutableSchema extends MutableCommon implements Schema, Schema.Builder {

    private String java_package;
    private final Set<Datatype> schemaDatatypes = new HashSet<>();
    private final Set<Property> schemaProperties = new HashSet<>();
    private final Set<Class> schemaClasses = new HashSet<>();

    @Override
    public Class type() {
        return (Class) TLC.get(ThingProvider.class).get("https://enola.dev/meta/Schema");
    }

    @Override
    public String java_package() {
        return java_package;
    }

    @Override
    public Schema.Builder java_package(String java_package) {
        this.java_package = java_package;
        return this;
    }

    @Override
    public Set<Datatype> schemaDatatypes() {
        return schemaDatatypes;
    }

    @Override
    public Set<Property> schemaProperties() {
        return schemaProperties;
    }

    @Override
    public Set<Class> schemaClasses() {
        return schemaClasses;
    }

    @Override
    public Schema.Builder addSchemaDatatype(Datatype datatype) {
        schemaDatatypes.add(datatype);
        return this;
    }

    @Override
    public Schema.Builder addSchemaProperty(Property property) {
        schemaProperties.add(property);
        return this;
    }

    @Override
    public Schema.Builder addSchemaClass(Class clazz) {
        schemaClasses.add(clazz);
        return this;
    }

    @Override
    public Schema build() {
        return this;
    }
}

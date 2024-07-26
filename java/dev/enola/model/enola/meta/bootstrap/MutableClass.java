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
import dev.enola.model.enola.meta.Property;
import dev.enola.model.enola.meta.Schema;
import dev.enola.thing.repo.ThingProvider;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

// NB: This hand-written class may eventually get replaced by a code-generated one!
public class MutableClass extends MutableType implements Class, Class.Builder {

    // TODO Use String IDs instead of Class & Property, and use TLC/TP get() in accessors?!
    private final List<Class> parents = new ArrayList<>();
    private final List<Property> idProperties = new ArrayList<>();
    private final List<Property> properties = new ArrayList<>();
    private @Nullable String iriTemplate;

    @Override
    public Class type() {
        return TLC.get(ThingProvider.class).get(Class.CLASS_IRI, Class.class);
    }

    @Override
    public Iterable<Class> parents() {
        return parents;
    }

    @Override
    public Class.Builder addParent(Class parent) {
        parents.add(parent);
        return this;
    }

    @Override
    public Iterable<Property> classIdProperties() {
        return idProperties;
    }

    @Override
    public Class.Builder addClassIdProperty(Property property) {
        idProperties.add(property);
        return this;
    }

    @Override
    public Iterable<Property> classProperties() {
        return properties;
    }

    @Override
    public Class.Builder addClassProperty(Property property) {
        properties.add(property);
        return this;
    }

    @Override
    public String iriTemplate() {
        return iriTemplate;
    }

    @Override
    public Class.Builder iriTemplate(String iriTemplate) {
        this.iriTemplate = iriTemplate;
        return this;
    }

    @Override
    public Class.Builder schema(Schema schema) {
        super.schema(schema);
        return this;
    }

    @Override
    public Class.Builder name(String name) {
        super.name(name);
        return this;
    }

    @Override
    public Class build() {
        return this;
    }
}

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
package dev.enola.model.enola.meta;

import dev.enola.thing.KIRI;
import dev.enola.thing.Thing;
import dev.enola.thing.java.TBF;

import org.jspecify.annotations.Nullable;

// skipcq: JAVA-E0169
public interface Class extends Type, dev.enola.model.w3.rdfs.Class {

    String CLASS_IRI = "https://enola.dev/meta/Class";

    default Iterable<Class> parents() {
        return getThings(KIRI.E.META.PARENTS, Class.class);
    }

    // NB: Cannot be named properties() due to conflict with Thing#properties()
    default Iterable<Property> classProperties() {
        return getThings(KIRI.E.META.CLASS_PROPERTIES, Property.class);
    }

    default Iterable<Property> classIdProperties() {
        return getThings(KIRI.E.META.CLASS_ID_PROPERTIES, Property.class);
    }

    default @Nullable String iriTemplate() {
        return getString(KIRI.E.META.IRI_TEMPLATE);
    }

    interface Builder<B extends Class> // skipcq: JAVA-E0169
            extends Thing.Builder<B>,
                    Class,
                    Type.Builder<B>,
                    dev.enola.model.w3.rdfs.Class.Builder<B> { // skipcq: JAVA-E0169

        @Override
        default Class.Builder<B> schema(Schema schema) {
            Type.Builder.super.schema(schema);
            return this;
        }

        @Override
        default Class.Builder<B> name(String name) {
            Type.Builder.super.name(name);
            return this;
        }

        default Class.Builder<B> addParent(Class parent) {
            add(KIRI.E.META.PARENTS, parent.iri());
            return this;
        }

        default Class.Builder<B> addClassProperty(Property property) {
            add(KIRI.E.META.CLASS_PROPERTIES, property.iri());
            return this;
        }

        default Class.Builder<B> addClassIdProperty(Property property) {
            add(KIRI.E.META.CLASS_ID_PROPERTIES, property.iri());
            return this;
        }

        default Class.Builder<B> iriTemplate(String iriTemplate) {
            set(KIRI.E.META.IRI_TEMPLATE, iriTemplate);
            return this;
        }
    }

    @SuppressWarnings("unchecked")
    static Class.Builder<Class> builder(TBF tbf) {
        return tbf.create(Class.Builder.class, Class.class);
    }
}

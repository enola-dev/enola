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

public interface Schema extends Common {

    String CLASS_IRI = "https://enola.dev/meta/Schema";

    default @Nullable String id() {
        return getString(KIRI.E.META.ID);
    }

    default @Nullable String java_package() {
        return getString(KIRI.E.META.JAVA);
    }

    default Iterable<Datatype> schemaDatatypes() {
        return getThings(KIRI.E.META.DATATYPES, Datatype.class);
    }

    default Iterable<Property> schemaProperties() {
        return getThings(KIRI.E.META.SCHEMA_PROPERTIES, Property.class);
    }

    default Iterable<Class> schemaClasses() {
        return getThings(KIRI.E.META.SCHEMA_CLASSES, Class.class);
    }

    interface Builder<B extends Schema> // skipcq: JAVA-E0169
            extends Thing.Builder<B>, Schema, Common.Builder<B> {

        default Schema.Builder<B> id(String id) {
            set(KIRI.E.META.ID, id);
            return this;
        }

        default Schema.Builder<B> java_package(String java_package) {
            set(KIRI.E.META.JAVA, java_package);
            return this;
        }

        default Schema.Builder<B> addSchemaDatatype(Datatype datatype) {
            add(KIRI.E.META.DATATYPES, datatype.iri());
            return this;
        }

        default Schema.Builder<B> addSchemaProperty(Property property) {
            add(KIRI.E.META.SCHEMA_PROPERTIES, property.iri());
            return this;
        }

        default Schema.Builder<B> addSchemaClass(Class clazz) {
            add(KIRI.E.META.SCHEMA_CLASSES, clazz.iri());
            return this;
        }
    }

    @SuppressWarnings("unchecked")
    static Schema.Builder<Schema> builder(TBF tbf) {
        return tbf.create(Schema.Builder.class, Schema.class);
    }
}

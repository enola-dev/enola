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
package dev.enola.model.enola.meta;

public interface Schema extends Common {

    String CLASS_IRI = "https://enola.dev/meta/Schema";

    String id();

    String java_package();

    Iterable<Datatype> schemaDatatypes();

    Iterable<Property> schemaProperties();

    Iterable<Class> schemaClasses();

    interface Builder<B extends Schema> extends Schema, Common.Builder<B> { // skipcq: JAVA-E0169

        Schema.Builder<B> id(String id);

        Schema.Builder<B> java_package(String java_package);

        Schema.Builder<B> addSchemaDatatype(Datatype datatype);

        Schema.Builder<B> addSchemaProperty(Property property);

        Schema.Builder<B> addSchemaClass(Class clazz);

        @Override
        Schema build();
    }
}

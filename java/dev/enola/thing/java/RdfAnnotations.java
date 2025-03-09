/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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
package dev.enola.thing.java;

import dev.enola.thing.Link;
import dev.enola.thing.Thing;

import java.util.Optional;

public class RdfAnnotations {

    // TODO Cache these! Because repeated Class.getAnnotation() is quite expensive.

    @SuppressWarnings("OptionalOfNullableMisuse")
    static Optional<String> classIRI(Class<?> clazz) {
        return Optional.ofNullable(clazz.getAnnotation(RdfClass.class)).map(RdfClass::iri);
    }

    public static void addType(Class<?> clazz, Thing.Builder<?> builder) {
        if (builder instanceof Thing.Builder2<?> builder2)
            classIRI(clazz).ifPresent(classIRI -> builder2.add(HasType.IRI, new Link(classIRI)));
        if (builder instanceof HasType.Builder<?> hasTypeBuilder)
            classIRI(clazz).ifPresent(hasTypeBuilder::addType);
        // TODO Remove this when Builder2 & Builder are merged! Wrong use of set() instead of add().
        classIRI(clazz).ifPresent(classIRI -> builder.set(HasType.IRI, new Link(classIRI)));
    }
}

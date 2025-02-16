/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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
package dev.enola.model.w3.rdfs;

import com.google.common.collect.Iterables;

import dev.enola.model.w3.rdf.Property;

public interface Class extends Resource {

    // TODO What's the point of this?! Remove...
    default boolean isClass() {
        return Iterables.contains(typesIRI(), IRI.Class.Class);
    }

    // TODO Multiple, or single?
    default Iterable<Class> subClassOfs() {
        return getThings(IRI.Predicate.subClassOf, Class.class);
    }

    /** This is the inverse of {@link Property#domain()}. */
    default Iterable<Property> rdfsClassProperties() {
        return getThings("https://enola.dev/properties", Property.class);
    }
}

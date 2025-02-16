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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import dev.enola.model.w3.rdf.Property;
import dev.enola.thing.HasIRI;
import dev.enola.thing.KIRI;
import dev.enola.thing.Link;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.ProxyTBF;

public interface Class extends Resource, HasClassIRI {

    // TODO Move somewhere else... but IRI enums are RDFS', while this comes from enola: ...
    String PROPERTIES = "https://enola.dev/properties";

    // TODO What's the point of this?! Remove...
    default boolean isClass() {
        return Iterables.contains(typesIRIs(), IRI.Class.Class);
    }

    // TODO Multiple, or single?
    default Iterable<Class> subClassOfs() {
        return getThings(IRI.Predicate.subClassOf, Class.class);
    }

    /** This is the inverse of {@link Property#domain()}. */
    default Iterable<Property> rdfsClassProperties() {
        return getThings(PROPERTIES, Property.class);
    }

    default Iterable<Object> rdfsClassPropertiesIRIs() {
        return getLinks(PROPERTIES);
    }

    default boolean hasRdfsClassProperty(String iri) {
        return hasLink(PROPERTIES, iri);
    }

    @Override
    Builder<? extends Class> copy();

    @SuppressWarnings("unchecked")
    static Builder<Class> builder() {
        var builder = new ProxyTBF(ImmutableThing.FACTORY).create(Builder.class, Class.class);
        builder.addType(KIRI.RDFS.CLASS);
        return builder;
    }

    interface Builder<B extends Class> // skipcq: JAVA-E0169
            extends Resource.Builder<B> {

        default Builder<B> addRdfsClassProperty(HasIRI iri) {
            // TODO Merge Thing.Builder with Thing.Builder2, and then: add(PROPERTIES, iri);
            //   The current solution is an ugly hack and needs fundamental review...
            //   just like HasType.Builder.addType - same problem there...
            set(PROPERTIES, ImmutableList.of(new Link(iri.iri())));
            return this;
        }

        @Override
        @CanIgnoreReturnValue
        Builder<B> iri(String iri);
    }
}

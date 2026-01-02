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
package dev.enola.model.w3.rdf;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import dev.enola.model.w3.rdfs.Class;
import dev.enola.model.w3.rdfs.Resource;
import dev.enola.thing.HasPredicateIRI;
import dev.enola.thing.KIRI;
import dev.enola.thing.Link;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.ProxyTBF;
import dev.enola.thing.java.RdfClass;

import java.util.Optional;

@RdfClass(iri = KIRI.RDFS.CLASS)
// Nota bene: The rdf:class (@type) of a Property is Class, not Property!!
public interface Property extends Resource, HasPredicateIRI {

    default Optional<Property> subPropertyOf() {
        return getThing(
                "http://www.w3.org/2000/01/rdf-schema#subPropertyOf",
                Property.class,
                Property.Builder.class);
    }

    /**
     * <a href="https://www.w3.org/TR/rdf12-schema/#ch_domain">Domains</a> of this property.
     *
     * <p>Beware that, if more than one, then this means intersection, not union; see <a
     * href="https://g.co/gemini/share/1ec30662b500">this explanation</a>.
     */
    default Iterable<Class> domains() {
        return getThings(KIRI.RDFS.DOMAIN, Class.class, Class.Builder.class);
    }

    /**
     * <a href="https://www.w3.org/TR/rdf12-schema/#ch_range">Domains</a> of this property.
     *
     * <p>Beware that, if more than one, then this means intersection, not union; see <a
     * href="https://g.co/gemini/share/1ec30662b500">this explanation</a>.
     */
    default Iterable<Class> ranges() {
        return getThings(KIRI.RDFS.RANGE, Class.class, Class.Builder.class);
    }

    @Override
    Builder<? extends Property> copy();

    // skipcq: JAVA-E0169
    interface Builder<B extends Property> extends Resource.Builder<B> {

        @CanIgnoreReturnValue
        default Builder<B> domain(String iri) {
            set(KIRI.RDFS.DOMAIN, new Link(iri));
            return this;
        }

        @CanIgnoreReturnValue
        default Builder<B> range(String iri) {
            set(KIRI.RDFS.RANGE, new Link(iri));
            return this;
        }

        @Override
        @CanIgnoreReturnValue
        Builder<B> iri(String iri);
    }

    @SuppressWarnings("unchecked")
    static Property.Builder<Property> builder() {
        return new ProxyTBF(ImmutableThing.FACTORY).create(Builder.class, Property.class);
    }
}

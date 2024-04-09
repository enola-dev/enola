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
package dev.enola.thing;

import com.google.common.collect.ImmutableMap;

import java.util.Collection;

/**
 * Thing is the central data structure of Enola.
 *
 * <p>Each Thing has an {@link #iri()}, which uniquely identifies it. All Things have 0..n
 * properties, each identified by an IRI itself, and having a value. Each such value has a (Java,
 * here) Type.
 *
 * <p>This is, of course, heavily inspired by TBL's vision of the <i>Semantic Web</i> of <i>Linked
 * Data</i>, such as also described by by standards such has RDF and then used e.g. by SPARQL, or
 * JSON-LD, etc.
 */
public interface Thing {

    // TODO Fix properties / predicate (which is the same!) inconsistency in method names & doc!

    String iri();

    /**
     * The Map's key is the IRI of a Property, and the value is as would be returned by {@link
     * #get(String)}.
     */
    default ImmutableMap<String, Object> properties() {
        var predicateIRIs = predicateIRIs();
        var builder = ImmutableMap.<String, Object>builderWithExpectedSize(predicateIRIs.size());
        for (var predicateIRI : predicateIRIs) {
            builder.put(predicateIRI, get(predicateIRI));
        }
        return builder.build();
    }

    /** IRIs of the Predicates about this Thing. */
    Collection<String> predicateIRIs();

    /**
     * Object of predicate. The type is e.g. directly a String, Integer etc. or a {@link Literal}.
     * Alteratively, it may be another Thing (if it's been "resolved") or a {@link Link} with an IRI
     * (if unresolved) or another Map (for an "inline embedded/expanded blank node") or a List of
     * such items. The object is immutable.
     */
    <T> T get(String predicateIRI);

    @SuppressWarnings("unchecked")
    default <T> T get(String predicateIRI, Class<T> klass) {
        Object object = get(predicateIRI);
        if (!klass.isInstance(object))
            throw new IllegalArgumentException(
                    iri() + "'s " + predicateIRI + " is " + object.getClass() + " not " + klass);
        return (T) object;
    }

    default String getString(String predicateIRI) {
        return get(predicateIRI, String.class);
    }

    // TODO get... other types.

    public interface Builder extends dev.enola.common.Builder<Thing> {

        Builder iri(String iri);

        Builder set(String predicateIRI, Object value);

        // Supplier<Builder> builderSupplier();

        @Override
        Thing build();
    }
}

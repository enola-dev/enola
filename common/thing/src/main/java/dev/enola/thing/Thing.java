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

import java.util.Collection;

public interface Thing {

    String iri();

    /**
     * The Map's key is the Thing of a Property, and the value is e.g. directly a String, Integer
     * etc. or a {@link Literal}. Alteratively, it may be another Thing (if it's been "resolved") or
     * a {@link Link} with an IRI (if unresolved) or another Map (for an "inline embedded/expanded
     * blank node") or a List of such items.
     */
    // ImmutableMap<Thing, Object> properties();

    /** Predicates about this Thing. */
    // Collection<Thing> predicates();

    /** IRIs of the Predicates about this Thing. */
    Collection<String> predicateIRIs();

    /**
     * Object of predicate. The type is e.g. directly a String, Integer etc. or a {@link Literal}.
     * Alteratively, it may be another Thing (if it's been "resolved") or a {@link Link} with an IRI
     * (if unresolved) or another Map (for an "inline embedded/expanded blank node") or a List of
     * such items.
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

        void iri(String iri);

        void set(String predicateIRI, Object value);

        @Override
        Thing build();
    }
}

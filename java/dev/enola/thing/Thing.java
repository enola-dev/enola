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

import com.google.errorprone.annotations.ImmutableTypeParameter;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.jspecify.annotations.Nullable;

/**
 * Thing is the central data structure of Enola.
 *
 * <p>Each Thing has an {@link #iri()}, which uniquely identifies it. All Things have 0..n
 * predicates, each identified by an IRI itself, and having a value. Each such value has a (Java,
 * here) Type (see {@link PredicatesObjects}).
 *
 * <p>This is, of course, heavily inspired by TBL's vision of the <i>Semantic Web</i> of <i>Linked
 * Data</i>, such as also described by standards such has RDF and then used e.g. by SPARQL, or
 * JSON-LD, etc.
 */
public interface Thing extends HasIRI, PredicatesObjects {

    @Override
    String iri();

    @Override
    @Deprecated
    Builder<? extends Thing> copy();

    @SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
    interface Builder<B extends Thing> extends PredicatesObjects.Builder<B> { // skipcq: JAVA-E0169

        Builder<B> iri(String iri);

        Builder<B> set(String predicateIRI, Object value);

        Builder<B> set(String predicateIRI, Object value, @Nullable String datatypeIRI);

        @Override
        B build();
    }

    // TODO Once ImmutableThing.Builder implements Builder2, just fold it into above
    // TODO How to best name this, and the equivalent in PredicatesObjects?
    @SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
    interface Builder2<B extends Thing> // skipcq: JAVA-E0169
            extends Thing.Builder<B>, PredicatesObjects.Builder2<B> {

        <@ImmutableTypeParameter T> Thing.Builder2<B> add(String predicateIRI, T value);

        default <@ImmutableTypeParameter T> Thing.Builder2<B> add(
                HasPredicateIRI predicate, T value) {
            return add(predicate.iri(), value);
        }

        <@ImmutableTypeParameter T> Thing.Builder2<B> add(
                String predicateIRI, T value, @Nullable String datatypeIRI);

        default <@ImmutableTypeParameter T> Thing.Builder2<B> add(
                HasPredicateIRI predicate, T value, @Nullable String datatypeIRI) {
            return add(predicate.iri(), value, datatypeIRI);
        }

        <@ImmutableTypeParameter T> Thing.Builder2<B> addOrdered(String predicateIRI, T value);

        default <@ImmutableTypeParameter T> Thing.Builder2<B> addOrdered(
                HasPredicateIRI predicate, T value) {
            return addOrdered(predicate.iri(), value);
        }

        <@ImmutableTypeParameter T> Thing.Builder2<B> addOrdered(
                String predicateIRI, T value, @Nullable String datatypeIRI);

        default <@ImmutableTypeParameter T> Thing.Builder2<B> addOrdered(
                HasPredicateIRI predicate, T value, @Nullable String datatypeIRI) {
            return addOrdered(predicate.iri(), value, datatypeIRI);
        }
    }
}

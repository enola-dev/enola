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
package dev.enola.thing;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
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
 * Data</i>, such as also described by standards such as RDF and then used e.g. by SPARQL, or
 * JSON-LD, etc.
 */
public interface Thing extends HasIRI, PredicatesObjects /*<Thing>*/ {

    // TODO @Override default <U extends PredicatesObjects<Thing>> U as(Class<U> clazz) {

    @Override
    String iri();

    @Override
    Builder<? extends Thing> copy();

    @SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
    interface Builder<B extends Thing> // skipcq: JAVA-E0169
            extends HasIRI.Builder<Builder<B>>, PredicatesObjects.Builder<B> { // skipcq: JAVA-E0169

        @Override
        @CanIgnoreReturnValue
        Builder<B> iri(String iri);

        @Override
        <@ImmutableTypeParameter T> Builder<B> set(String predicateIRI, T value);

        @Override
        <@ImmutableTypeParameter T> Builder<B> set(
                String predicateIRI, T value, @Nullable String datatypeIRI);

        // TODO It would be cool if we could enforce that T value must be an Immutable.
        // But using Error Prone's <@ImmutableTypeParameter T> instead of <T> does not work.

        @Override
        <@ImmutableTypeParameter T> Builder<B> add(String predicateIRI, T value);

        @Override
        <@ImmutableTypeParameter T> Builder<B> addAll(String predicateIRI, Iterable<T> value);

        @Override
        <@ImmutableTypeParameter T> Builder<B> add(
                String predicateIRI, T value, @Nullable String datatypeIRI);

        @Override
        <@ImmutableTypeParameter T> Builder<B> addAll(
                String predicateIRI, Iterable<T> value, @Nullable String datatypeIRI);

        @Override
        <@ImmutableTypeParameter T> Builder<B> addOrdered(String predicateIRI, T value);

        @Override
        <@ImmutableTypeParameter T> Builder<B> addOrdered(
                String predicateIRI, T value, @Nullable String datatypeIRI);

        @Override
        <@ImmutableTypeParameter T> Builder<B> add(HasPredicateIRI predicate, T value);

        @Override
        <@ImmutableTypeParameter T> Builder<B> add(
                HasPredicateIRI predicate, T value, @Nullable String datatypeIRI);

        @Override
        <@ImmutableTypeParameter T> Builder<B> addAll(HasPredicateIRI predicate, Iterable<T> value);

        @Override
        <@ImmutableTypeParameter T> Builder<B> addAll(
                HasPredicateIRI predicate, Iterable<T> value, @Nullable String datatypeIRI);

        @Override
        <@ImmutableTypeParameter T> Builder<B> addOrdered(HasPredicateIRI predicate, T value);

        @Override
        <@ImmutableTypeParameter T> Builder<B> addOrdered(
                HasPredicateIRI predicate, T value, @Nullable String datatypeIRI);

        @Override
        B build();
    }
}

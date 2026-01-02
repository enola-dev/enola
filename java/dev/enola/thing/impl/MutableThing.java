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
package dev.enola.thing.impl;

import com.google.errorprone.annotations.ImmutableTypeParameter;

import dev.enola.thing.HasPredicateIRI;
import dev.enola.thing.Thing;
import dev.enola.thing.ThingOrBuilder;
import dev.enola.thing.java.HasType;
import dev.enola.thing.java.RdfAnnotations;
import dev.enola.thing.java.TBF;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.jspecify.annotations.Nullable;

/**
 * Implementation of both {@link Thing} and its {@link Thing.Builder} which is simple and mutable.
 *
 * <p>When {@link #build()} then it returns an {@link IImmutableThing} copy of this (NOT itself).
 *
 * <p>This implementation is pretty inefficient, for both its runtime performance and memory
 * consumption, and should only be used "shortly lived"; prefer (building this into a) {@link
 * IImmutableThing} implementations, such as (typically) the {@link ImmutableThing} or its generated
 * subclasses, for any objects that will be "kept around".
 *
 * <p>This implementation is not thread safe, obviously.
 */
// TODO Make MutableThing package private, and let users create them only via the #FACTORY TBF
@SuppressFBWarnings("EQ_DOESNT_OVERRIDE_EQUALS")
// skipcq: JAVA-W0100
public class MutableThing<B extends IImmutableThing> extends MutablePredicatesObjects<B>
        implements ThingOrBuilder<B> {

    public static final TBF FACTORY =
            new TBF() {
                @Override
                public boolean handles(String typeIRI) {
                    return true;
                }

                @Override
                @SuppressWarnings("Immutable") // TODO Error Prone bug?!
                public Thing.Builder<Thing> create(String typeIRI) {
                    return new MutableThing().add(HasType.IRI, typeIRI);
                }

                @Override
                @SuppressWarnings({"unchecked", "rawtypes"})
                public <T extends Thing, TB extends Thing.Builder<T>> TB create(
                        Class<TB> builderInterface, Class<T> thingInterface) {
                    var builder = (TB) new MutableThing();
                    RdfAnnotations.addType(thingInterface, builder);
                    return builder;
                }

                @Override
                @SuppressWarnings({"unchecked", "rawtypes"})
                public <T extends Thing, TB extends Thing.Builder<T>> TB create(
                        Class<TB> builderInterface, Class<T> thingInterface, int expectedSize) {
                    var builder = (TB) new MutableThing(expectedSize);
                    RdfAnnotations.addType(thingInterface, builder);
                    return builder;
                }
            };

    private @Nullable String iri;

    public MutableThing() {
        super();
    }

    public MutableThing(int expectedSize) {
        super(expectedSize);
    }

    @Override
    public Thing.Builder<B> iri(String iri) {
        this.iri = iri;
        return this;
    }

    @Override
    public String iri() {
        if (iri == null) throw new IllegalStateException();
        return iri;
    }

    @Override
    public <@ImmutableTypeParameter T> Thing.Builder<B> set(String predicateIRI, T value) {
        super.set(predicateIRI, value);
        return this;
    }

    @Override
    public <@ImmutableTypeParameter T> Thing.Builder<B> set(
            String predicateIRI, T value, @Nullable String datatypeIRI) {
        super.set(predicateIRI, value, datatypeIRI);
        return this;
    }

    @Override
    public <@ImmutableTypeParameter T> Thing.Builder<B> add(String predicateIRI, T value) {
        super.add(predicateIRI, value);
        return this;
    }

    @Override
    public <@ImmutableTypeParameter T> Thing.Builder<B> addAll(
            String predicateIRI, Iterable<T> values) {
        super.addAll(predicateIRI, values);
        return this;
    }

    @Override
    public <@ImmutableTypeParameter T> Thing.Builder<B> add(
            String predicateIRI, T value, @Nullable String datatypeIRI) {
        super.add(predicateIRI, value, datatypeIRI);
        return this;
    }

    @Override
    public <@ImmutableTypeParameter T> Thing.Builder<B> addAll(
            String predicateIRI, Iterable<T> values, @Nullable String datatypeIRI) {
        super.addAll(predicateIRI, values, datatypeIRI);
        return this;
    }

    @Override
    public <@ImmutableTypeParameter T> Thing.Builder<B> addOrdered(String predicateIRI, T value) {
        super.addOrdered(predicateIRI, value);
        return this;
    }

    @Override
    public <@ImmutableTypeParameter T> Thing.Builder<B> addOrdered(
            String predicateIRI, T value, @Nullable String datatypeIRI) {
        super.addOrdered(predicateIRI, value, datatypeIRI);
        return this;
    }

    @Override
    public <@ImmutableTypeParameter T> Thing.Builder<B> add(HasPredicateIRI predicate, T value) {
        super.add(predicate, value);
        return this;
    }

    @Override
    public <@ImmutableTypeParameter T> Thing.Builder<B> add(
            HasPredicateIRI predicate, T value, @Nullable String datatypeIRI) {
        super.add(predicate, value, datatypeIRI);
        return this;
    }

    @Override
    public <@ImmutableTypeParameter T> Thing.Builder<B> addAll(
            HasPredicateIRI predicate, Iterable<T> value) {
        super.addAll(predicate, value);
        return this;
    }

    @Override
    public <@ImmutableTypeParameter T> Thing.Builder<B> addAll(
            HasPredicateIRI predicate, Iterable<T> value, @Nullable String datatypeIRI) {
        super.addAll(predicate, value, datatypeIRI);
        return this;
    }

    @Override
    public <@ImmutableTypeParameter T> Thing.Builder<B> addOrdered(
            HasPredicateIRI predicate, T value) {
        super.addOrdered(predicate, value);
        return this;
    }

    @Override
    public <@ImmutableTypeParameter T> Thing.Builder<B> addOrdered(
            HasPredicateIRI predicate, T value, @Nullable String datatypeIRI) {
        super.addOrdered(predicate, value, datatypeIRI);
        return this;
    }

    @Override
    public Thing.Builder<? extends Thing> copy() {
        return this;
    }

    @Override
    public final int hashCode() {
        return ThingHashCodeEqualsToString.hashCode(this);
    }

    @Override
    @SuppressWarnings({"EqualsWhichDoesntCheckParameterClass", "EqualsDoesntCheckParameterClass"})
    public final boolean equals(Object obj) {
        return ThingHashCodeEqualsToString.equals(this, obj);
    }

    @Override
    public final String toString() {
        return ThingHashCodeEqualsToString.toString(this, properties, datatypes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public B build() {
        if (iri == null) throw new IllegalStateException(PackageLocalConstants.NEEDS_IRI_MESSAGE);

        var pair = ImmutableObjects.build(properties, datatypes);
        return (B) new ImmutableThing(iri, pair.properties(), pair.datatypes());
    }
}

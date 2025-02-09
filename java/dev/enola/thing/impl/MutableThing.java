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
package dev.enola.thing.impl;

import dev.enola.thing.Thing;
import dev.enola.thing.ThingOrBuilder;
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
@SuppressFBWarnings("EQ_DOESNT_OVERRIDE_EQUALS")
// skipcq: JAVA-W0100
public class MutableThing<B extends IImmutableThing> extends MutablePredicatesObjects<B>
        implements ThingOrBuilder<B> {

    public static final TBF FACTORY =
            new TBF() {
                @Override
                @SuppressWarnings({"unchecked", "rawtypes"})
                public <T extends Thing, TB extends Thing.Builder<?>> TB create(
                        Class<TB> builderInterface, Class<T> thingInterface) {
                    if (builderInterface.equals(Thing.Builder.class)
                            && thingInterface.equals(Thing.class)) return (TB) new MutableThing();
                    else
                        throw new IllegalArgumentException(
                                "This implementation does not support "
                                        + builderInterface
                                        + " and "
                                        + thingInterface);
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
    public Thing.Builder2<B> iri(String iri) {
        this.iri = iri;
        return this;
    }

    @Override
    public String iri() {
        if (iri == null) throw new IllegalStateException();
        return iri;
    }

    @Override
    public Thing.Builder2<B> set(String predicateIRI, Object value) {
        super.set(predicateIRI, value);
        return this;
    }

    @Override
    public Thing.Builder2<B> set(String predicateIRI, Object value, @Nullable String datatypeIRI) {
        super.set(predicateIRI, value, datatypeIRI);
        return this;
    }

    @Override
    public <T> Thing.Builder2<B> add(String predicateIRI, T value) {
        super.add(predicateIRI, value);
        return this;
    }

    @Override
    public <T> Thing.Builder2<B> addAll(String predicateIRI, Iterable<T> value) {
        super.addAll(predicateIRI, value);
        return this;
    }

    @Override
    public <T> Thing.Builder2<B> add(String predicateIRI, T value, @Nullable String datatypeIRI) {
        super.add(predicateIRI, value, datatypeIRI);
        return this;
    }

    @Override
    public <T> Thing.Builder2<B> addAll(
            String predicateIRI, Iterable<T> value, @Nullable String datatypeIRI) {
        super.addAll(predicateIRI, value, datatypeIRI);
        return this;
    }

    @Override
    public <T> Thing.Builder2<B> addOrdered(String predicateIRI, T value) {
        super.addOrdered(predicateIRI, value);
        return this;
    }

    @Override
    public <T> Thing.Builder2<B> addOrdered(
            String predicateIRI, T value, @Nullable String datatypeIRI) {
        super.addOrdered(predicateIRI, value, datatypeIRI);
        return this;
    }

    @Override
    @Deprecated
    public Thing.Builder2<? extends Thing> copy() {
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
        return ThingHashCodeEqualsToString.toString(this);
    }

    @Override
    @SuppressWarnings("unchecked") // TODO How to remove (B) type cast?!
    public B build() {
        var immutableBuilder = ImmutableThing.builderWithExpectedSize(predicateIRIs().size());
        if (iri == null) throw new IllegalStateException(PackageLocalConstants.NEEDS_IRI_MESSAGE);
        immutableBuilder.iri(iri);
        deepBuildInto(immutableBuilder);
        return (B) immutableBuilder.build();
    }
}

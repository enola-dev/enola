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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import dev.enola.common.context.TLC;
import dev.enola.thing.Thing;
import dev.enola.thing.java.TBF;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.jspecify.annotations.Nullable;

/**
 * {@link Thing} with only an IRI and no properties (optimized).
 *
 * <p>See {@link dev.enola.thing.repo.AlwaysThingProvider}.
 */
// TODO Make OnlyIRIThing package private; this should only be used by AlwaysThingProvider!
@SuppressFBWarnings("EQ_DOESNT_OVERRIDE_EQUALS")
// skipcq: JAVA-W0100
public class OnlyIRIThing implements IImmutableThing {

    private final String iri;

    public OnlyIRIThing(String iri) {
        this.iri = iri;
    }

    @Override
    public String iri() {
        return iri;
    }

    @Override
    public ImmutableMap<String, Object> properties() {
        return ImmutableMap.of();
    }

    @Override
    public ImmutableSet<String> predicateIRIs() {
        return ImmutableSet.of();
    }

    @Override
    public @Nullable String datatype(String predicateIRI) {
        return null;
    }

    @Override
    public ImmutableMap<String, String> datatypes() {
        return ImmutableMap.of();
    }

    @Override
    public <T> @Nullable T get(String predicateIRI) {
        return null;
    }

    @Override
    public Thing.Builder<? extends Thing> copy() {
        return TLC.get(TBF.class).create();
    }

    @Override
    public final int hashCode() {
        return ThingHashCodeEqualsToString.hashCode(this);
    }

    @Override
    public final boolean equals(Object obj) {
        return ThingHashCodeEqualsToString.equals(this, obj);
    }

    @Override
    public final String toString() {
        return ThingHashCodeEqualsToString.toString(this, properties(), datatypes());
    }
}

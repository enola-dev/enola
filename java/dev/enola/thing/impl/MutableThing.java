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
package dev.enola.thing.impl;

import dev.enola.thing.Thing;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link Thing} and its {@link Thing.Builder} which is simple and mutable.
 *
 * <p>This implementation is pretty inefficient, for both its runtime performance and a memory
 * consumption, and should only be used "short lived"; prefer {@link IImmutableThing}
 * implementations, such as (typically) the {@link ImmutableThing} or its generated subclasses, for
 * any objects which will be "kept around".
 *
 * <p>This implementation is not thread safe, obviously.
 */
@SuppressFBWarnings("EQ_DOESNT_OVERRIDE_EQUALS")
// skipcq: JAVA-W0100
public class MutableThing<B extends MutableThing> extends AbstractThing
        implements Thing, Thing.Builder<B> {

    protected @Nullable String iri;
    protected final Map<String, Object> properties;
    protected final Map<String, String> datatypes;

    public MutableThing() {
        properties = new HashMap<>();
        datatypes = new HashMap<>();
    }

    public MutableThing(int expectedSize) {
        properties = new HashMap<>(expectedSize); // exact
        datatypes = new HashMap<>(expectedSize); // upper bound
    }

    @Override
    public Builder<B> iri(String iri) {
        this.iri = iri;
        return this;
    }

    @Override
    public String iri() {
        return iri;
    }

    @Override
    public Builder<B> set(String predicateIRI, Object value) {
        properties.put(predicateIRI, value);
        return this;
    }

    @Override
    public Builder<B> set(String predicateIRI, Object value, String datatypeIRI) {
        properties.put(predicateIRI, value);
        datatypes.put(predicateIRI, datatypeIRI);
        return this;
    }

    @Override
    public <T> @Nullable T get(String predicateIRI) {
        return (T) properties.get(predicateIRI);
    }

    @Override
    public Map<String, Object> properties() {
        return properties;
    }

    @Override
    public Set<String> predicateIRIs() {
        return properties.keySet();
    }

    @Override
    public @Nullable String datatype(String predicateIRI) {
        return datatypes.get(predicateIRI);
    }

    @Override
    public Map<String, String> datatypes() {
        return datatypes;
    }

    @Override
    public Builder<? extends Thing> copy() {
        return this;
    }

    @Override
    // TODO Return an IImmutableThing instead of this here?!
    public B build() {
        // TODO Remove (B) type cast
        return (B) this;
    }
}

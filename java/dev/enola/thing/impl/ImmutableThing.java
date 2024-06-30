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

import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.ThreadSafe;

import dev.enola.thing.Literal;
import dev.enola.thing.Thing;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

@Immutable
@ThreadSafe
public class ImmutableThing extends ImmutablePredicatesObjects implements IImmutableThing {

    private final String iri;

    protected ImmutableThing(
            String iri,
            ImmutableMap<String, Object> properties,
            ImmutableMap<String, String> datatypes) {
        super(properties, datatypes);
        this.iri = Objects.requireNonNull(iri, "iri");
    }

    public static ImmutableThing copyOf(Thing thing) {
        if (thing instanceof ImmutableThing immutableThing) return immutableThing;

        return new ImmutableThing(
                thing.iri(),
                ImmutableMap.copyOf(thing.properties()),
                ImmutableMap.copyOf(thing.datatypes()));
    }

    public static Thing.Builder<? extends ImmutableThing> builder() {
        return new Builder<>();
    }

    public static Thing.Builder builderWithExpectedSize(int expectedSize) {
        return new Builder(expectedSize);
    }

    @Override
    public String iri() {
        return iri;
    }

    @Override
    public boolean equals(Object obj) {
        return ThingHashCodeEqualsToString.equals(this, obj);
    }

    @Override
    public int hashCode() {
        return ThingHashCodeEqualsToString.hashCode(this);
    }

    @Override
    public String toString() {
        return ThingHashCodeEqualsToString.toString(this);
    }

    @Override
    public Builder<? extends ImmutableThing> copy() {
        return new Builder<>(iri, properties(), datatypes());
    }

    @SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
    public static class Builder<B extends IImmutableThing> // skipcq: JAVA-E0169
            implements Thing.Builder<B> {

        protected final ImmutableMap.Builder<String, Object> properties;
        protected final ImmutableMap.Builder<String, String> datatypes;
        protected @Nullable String iri;

        protected Builder() {
            properties = ImmutableMap.builder();
            datatypes = ImmutableMap.builder();
        }

        protected Builder(int expectedSize) {
            properties = ImmutableMap.builderWithExpectedSize(expectedSize); // exact
            datatypes = ImmutableMap.builderWithExpectedSize(expectedSize); // upper bound
        }

        protected Builder(
                String iri,
                final ImmutableMap<String, Object> properties,
                final ImmutableMap<String, String> datatypes) {
            iri(iri);
            this.properties =
                    ImmutableMap.<String, Object>builderWithExpectedSize(properties.size())
                            .putAll(properties);
            this.datatypes =
                    ImmutableMap.<String, String>builderWithExpectedSize(properties.size())
                            .putAll(datatypes);
        }

        @Override
        public Thing.Builder<B> iri(String iri) {
            if (this.iri != null) throw new IllegalStateException("IRI already set: " + this.iri);
            this.iri = iri;
            return this;
        }

        @Override
        public Thing.Builder<B> set(String predicateIRI, Object value) {
            if (value instanceof Literal literal)
                set(predicateIRI, literal.value(), literal.datatypeIRI());
            else properties.put(predicateIRI, value);
            return this;
        }

        @Override
        public Thing.Builder<B> set(
                String predicateIRI, Object value, @Nullable String datatypeIRI) {
            properties.put(predicateIRI, value);
            if (datatypeIRI != null) datatypes.put(predicateIRI, datatypeIRI);
            return this;
        }

        @Override
        public B build() {
            if (iri == null) throw new IllegalStateException("Cannot build Thing without IRI");
            // TODO Remove (B) type cast
            return (B) new ImmutableThing(iri, properties.build(), datatypes.build());
        }
    }
}

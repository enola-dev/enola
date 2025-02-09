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

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.ThreadSafe;

import dev.enola.thing.Thing;
import dev.enola.thing.java.TBF;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.jspecify.annotations.Nullable;

@Immutable
@ThreadSafe
public class ImmutableThing extends ImmutablePredicatesObjects implements IImmutableThing {

    private final String iri;

    protected ImmutableThing(
            String iri,
            ImmutableMap<String, Object> properties,
            ImmutableMap<String, String> datatypes) {
        super(properties, datatypes);
        this.iri = requireNonNull(iri, "iri");
    }

    public static Thing.Builder<? extends ImmutableThing> builder() {
        return new Builder<>();
    }

    public static Thing.Builder<? extends ImmutableThing> builderWithExpectedSize(
            int expectedSize) {
        return new Builder<>(expectedSize);
    }

    public static final TBF FACTORY =
            new TBF() {
                @Override
                @SuppressWarnings("unchecked")
                public <T extends Thing, B extends Thing.Builder<?>> B create(
                        Class<B> builderInterface, Class<T> thingInterface) {
                    if (builderInterface.equals(Thing.Builder.class)
                            && thingInterface.equals(Thing.class)) return (B) builder();
                    else
                        throw new IllegalArgumentException(
                                "This implementation does not support "
                                        + builderInterface
                                        + " and "
                                        + thingInterface);
                }
            };

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
            extends ImmutablePredicatesObjects.Builder<B> implements Thing.Builder<B> {

        protected @Nullable String iri;

        protected Builder() {
            super();
        }

        protected Builder(int expectedSize) {
            super(expectedSize);
        }

        protected Builder(
                String iri,
                final ImmutableMap<String, Object> properties,
                final ImmutableMap<String, String> datatypes) {
            super(properties, datatypes);
            iri(iri);
        }

        @Override
        public Thing.Builder<B> iri(String iri) {
            if (this.iri != null && !this.iri.equals((iri)))
                throw new IllegalStateException(
                        "IRI already set: " + this.iri + ", cannot set to: " + iri);
            this.iri = requireNonNull(iri);
            return this;
        }

        @Override
        public Thing.Builder<B> set(String predicateIRI, Object value) {
            super.set(predicateIRI, value);
            return this;
        }

        @Override
        public Thing.Builder<B> set(
                String predicateIRI, Object value, @Nullable String datatypeIRI) {
            super.set(predicateIRI, value, datatypeIRI);
            return this;
        }

        @Override
        public B build() {
            if (iri == null)
                throw new IllegalStateException(PackageLocalConstants.NEEDS_IRI_MESSAGE);
            // TODO Remove (B) type cast
            return (B) new ImmutableThing(iri, properties.build(), datatypes.build());
        }

        @Override
        public String toString() {
            // TODO https://github.com/google/guava/issues/7408 to avoid .build()
            return "Builder{"
                    + "iri="
                    + iri
                    + ", properties="
                    + properties.build()
                    + ", datatypes="
                    + datatypes.build()
                    + '}';
        }
    }
}

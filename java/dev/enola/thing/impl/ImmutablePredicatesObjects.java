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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.ThreadSafe;

import dev.enola.thing.Literal;
import dev.enola.thing.PredicatesObjects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.jspecify.annotations.Nullable;

@Immutable
@ThreadSafe
public class ImmutablePredicatesObjects implements IImmutablePredicatesObjects {

    // Suppressed because of @ImmutableTypeParameter T in PredicatesObjects.Builder#set:
    @SuppressWarnings("Immutable")
    protected final ImmutableMap<String, Object> properties;

    protected final ImmutableMap<String, String> datatypes;

    protected ImmutablePredicatesObjects(
            ImmutableMap<String, Object> properties, ImmutableMap<String, String> datatypes) {
        this.properties = properties;
        this.datatypes = datatypes;
    }

    public static <T extends IImmutablePredicatesObjects>
            IImmutablePredicatesObjects.Builder<T> builder() {
        return new ImmutablePredicatesObjects.Builder<>();
    }

    public static PredicatesObjects.Builder<? extends ImmutablePredicatesObjects>
            builderWithExpectedSize(int expectedSize) {
        return new ImmutablePredicatesObjects.Builder<>(expectedSize);
    }

    @Override
    public ImmutableMap<String, Object> properties() {
        return properties;
    }

    @Override
    public ImmutableSet<String> predicateIRIs() {
        return properties.keySet();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @Nullable T get(String predicateIRI) {
        return (T) properties.get(predicateIRI);
    }

    @Override
    public ImmutableMap<String, String> datatypes() {
        return datatypes;
    }

    @Override
    public @Nullable String datatype(String predicateIRI) {
        return datatypes.get(predicateIRI);
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
    public PredicatesObjects.Builder<? extends ImmutablePredicatesObjects> copy() {
        return new Builder<>(properties(), datatypes());
    }

    @SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
    static class Builder<B extends PredicatesObjects> // skipcq: JAVA-E0169
            implements PredicatesObjects.Builder<B> {

        // TODO Keep the iteration order of this internal maps consistent between the implementation
        // chosen here, and the one used in the MutablePredicatesObjects; this makes switching TBL
        // implementations easier, and without unexpected property order side effects on tests.

        protected final ImmutableMap.Builder<String, Object> properties;
        protected final ImmutableMap.Builder<String, String> datatypes;

        Builder() {
            properties = ImmutableMap.builder();
            datatypes = ImmutableMap.builder();
        }

        Builder(int expectedSize) {
            properties = ImmutableMap.builderWithExpectedSize(expectedSize); // exact
            datatypes = ImmutableMap.builderWithExpectedSize(expectedSize); // upper bound
        }

        Builder(
                final ImmutableMap<String, Object> properties,
                final ImmutableMap<String, String> datatypes) {
            this.properties =
                    ImmutableMap.<String, Object>builderWithExpectedSize(properties.size())
                            .putAll(properties);
            this.datatypes =
                    ImmutableMap.<String, String>builderWithExpectedSize(properties.size())
                            .putAll(datatypes);
        }

        @Override
        public PredicatesObjects.Builder<B> set(String predicateIRI, Object value) {
            if (value == null) return this;
            if (value instanceof String string && string.isEmpty()) return this;
            if (value instanceof Iterable iterable && Iterables.isEmpty(iterable)) return this;
            ImmutableObjects.check(value);
            if (value instanceof Literal literal)
                set(predicateIRI, literal.value(), literal.datatypeIRI());
            else properties.put(predicateIRI, value);
            return this;
        }

        @Override
        public PredicatesObjects.Builder<B> set(
                String predicateIRI, Object value, @Nullable String datatypeIRI) {
            if (value == null) return this;
            if (value instanceof String string && string.isEmpty()) return this;
            if (value instanceof Iterable iterable && Iterables.isEmpty(iterable)) return this;
            ImmutableObjects.check(value);
            if (datatypeIRI != null) {
                if (value instanceof Literal)
                    throw new IllegalArgumentException("Cannot set Literal AND Datatype");
                datatypes.put(predicateIRI, datatypeIRI);
            }
            properties.put(predicateIRI, value);
            return this;
        }

        @Override
        public B build() {
            // TODO Remove (B) type cast
            return (B) new ImmutablePredicatesObjects(properties.build(), datatypes.build());
        }

        @Override
        public String toString() {
            // TODO https://github.com/google/guava/issues/7408 to avoid .build()
            return "Builder{"
                    + "properties="
                    + properties.build()
                    + ", datatypes="
                    + datatypes.build()
                    + '}';
        }
    }
}

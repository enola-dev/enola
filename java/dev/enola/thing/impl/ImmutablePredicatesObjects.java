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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Immutable
@ThreadSafe
// TODO Make ImmutablePredicatesObjects package private, and let users create them via the TBF (?)
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

    public static PredicatesObjects.Builder<? extends IImmutablePredicatesObjects>
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
    public PredicatesObjects.Builder<? extends IImmutablePredicatesObjects> copy() {
        return new Builder<>(properties(), datatypes());
    }

    @SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
    static class Builder<B extends PredicatesObjects> // skipcq: JAVA-E0169
            implements PredicatesObjects.Builder<B> {

        // NB: Keep the iteration order of the internal maps consistent between the implementation
        // chosen here, and the one used in the MutablePredicatesObjects; this makes switching TBF
        // implementations easier, and without unexpected property order side effects on tests.

        // Nota bene: It is tempting to want to use ImmutableMap.Builder internally here. However,
        // ImmutableMap.Builder only has put*() and no get() methods - which makes it unsuitable
        // here, because we need to be able to get() to implement add*().

        protected final Map<String, Object> properties;
        protected final Map<String, String> datatypes;

        // TODO Use HashMap instead of LinkedHashMap

        Builder() {
            properties = new LinkedHashMap<>(8, 0.9f);
            datatypes = new LinkedHashMap<>(0, 1.0f);
        }

        Builder(int expectedSize) {
            properties = new LinkedHashMap<>(expectedSize, 0.9f); // exact
            datatypes = new LinkedHashMap<>(expectedSize / 4, 1.0f); // upper bound
        }

        Builder(
                final ImmutableMap<String, Object> properties,
                final ImmutableMap<String, String> datatypes) {
            this.properties = new HashMap<>(properties);
            this.datatypes = new HashMap<>(datatypes);
        }

        @Override
        public PredicatesObjects.Builder<B> set(String predicateIRI, Object value) {
            // TODO Re-review this... this prevents copy() callers from "clearing" properties!
            if (value == null) return this;
            if (value instanceof String string && string.isEmpty()) return this;
            if (value instanceof Iterable<?> iterable && Iterables.isEmpty(iterable)) return this;
            ImmutableObjects.check(value);
            if (value instanceof Literal(String literalValue, String datatypeIRI))
                set(predicateIRI, literalValue, datatypeIRI);
            else properties.put(predicateIRI, value);
            return this;
        }

        @Override
        public PredicatesObjects.Builder<B> set(
                String predicateIRI, Object value, @Nullable String datatypeIRI) {
            if (value == null) return this;
            if (value instanceof String string && string.isEmpty()) return this;
            if (value instanceof Iterable<?> iterable && Iterables.isEmpty(iterable)) return this;
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
        @SuppressWarnings("unchecked")
        public B build() {
            // NB: ImmutableThing.Builder#build() has the same:
            var immutableProperties = ImmutableMap.copyOf(this.properties);
            var immutableDataypes = ImmutableMap.copyOf(this.datatypes);
            return (B) new ImmutablePredicatesObjects(immutableProperties, immutableDataypes);
        }

        @Override
        public String toString() {
            return "Builder{" + "properties=" + properties + ", datatypes=" + datatypes + '}';
        }
    }
}

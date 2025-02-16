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

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import dev.enola.thing.Literal;
import dev.enola.thing.PredicatesObjects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.jspecify.annotations.Nullable;

import java.util.*;

// TODO Make MutablePredicatesObjects package private, and let users create them via the TBF (?)
@SuppressFBWarnings("EQ_DOESNT_OVERRIDE_EQUALS")
// skipcq: JAVA-W0100
public class MutablePredicatesObjects<B extends IImmutablePredicatesObjects>
        implements PredicatesObjects, PredicatesObjects.Builder2<B> {

    // TODO Keep the iteration order of this internal maps consistent between the implementation
    // chosen here, and the one used in ImmutablePredicatesObjects.Builder; this makes switching TBL
    // implementations easier, and without unexpected property order side effects on tests.
    //
    // Because ImmutableMap.Builder behaves like LinkedHashMap, and preserves insertion
    // order, we have that here, instead of a simple HashMap.

    private final Map<String, Object> properties;
    private final Map<String, String> datatypes;

    public MutablePredicatesObjects() {
        properties = new LinkedHashMap<>();
        datatypes = new LinkedHashMap<>();
    }

    public MutablePredicatesObjects(int expectedSize) {
        properties = new LinkedHashMap<>(expectedSize); // exact
        datatypes = new LinkedHashMap<>(expectedSize); // upper bound
    }

    @Override
    public Builder2<B> set(String predicateIRI, Object value) {
        if (value == null) return this;
        if (value instanceof String string && string.isEmpty()) return this;
        if (value instanceof Iterable iterable && Iterables.isEmpty(iterable)) return this;
        if (value instanceof Literal(String value1, String datatypeIRI))
            set(predicateIRI, value1, datatypeIRI);
        else properties.put(predicateIRI, value);
        return this;
    }

    @Override
    public Builder2<B> set(String predicateIRI, Object value, @Nullable String datatypeIRI) {
        if (value == null) return this;
        if (value instanceof String string && string.isEmpty()) return this;
        if (value instanceof Iterable iterable && Iterables.isEmpty(iterable)) return this;
        if (datatypeIRI != null) {
            if (value instanceof Literal)
                throw new IllegalArgumentException("Cannot set Literal AND Datatype");
            datatypes.put(predicateIRI, datatypeIRI);
        }
        properties.put(predicateIRI, value);
        return this;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> Builder2<B> add(String predicateIRI, T value) {
        if (value == null) return this;
        if (value instanceof String string && string.isEmpty()) return this;
        var object = properties.get(predicateIRI);
        if (object == null) {
            var builder = ImmutableSet.builder();
            properties.put(predicateIRI, builder);
            builder.add(value);
        } else if (object instanceof ImmutableSet.Builder builder) {
            builder.add(value);
        } else
            throw new IllegalStateException(
                    predicateIRI + " is not an ImmutableSet.Builder: " + object);
        return this;
    }

    @Override
    public <T> Builder2<B> addAll(String predicateIRI, Iterable<T> value) {
        if (value == null) return this;
        var object = properties.get(predicateIRI);
        if (object == null) {
            var builder = ImmutableSet.builder();
            properties.put(predicateIRI, builder);
            builder.addAll(value);
        } else if (object instanceof ImmutableSet.Builder builder) {
            builder.addAll(value);
        } else
            throw new IllegalStateException(
                    predicateIRI + " is not an ImmutableSet.Builder: " + object);
        return this;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> Builder2<B> addOrdered(String predicateIRI, T value) {
        if (value == null) return this;
        if (value instanceof String string && string.isEmpty()) return this;
        var object = properties.get(predicateIRI);
        if (object == null) {
            var builder = ImmutableList.builder();
            properties.put(predicateIRI, builder);
            builder.add(value);
        } else if (object instanceof ImmutableList.Builder builder) {
            builder.add(value);
        } else
            throw new IllegalStateException(
                    predicateIRI + " is not an ImmutableList.Builder: " + object);
        return this;
    }

    @Override
    public <T> Builder2<B> add(String predicateIRI, T value, @Nullable String datatypeIRI) {
        if (value == null) return this;
        if (value instanceof String string && string.isEmpty()) return this;
        checkCollectionDatatype(predicateIRI, datatypeIRI);
        add(predicateIRI, value);
        return this;
    }

    @Override
    public <T> Builder2<B> addAll(
            String predicateIRI, Iterable<T> value, @Nullable String datatypeIRI) {
        if (value == null) return this;
        checkCollectionDatatype(predicateIRI, datatypeIRI);
        addAll(predicateIRI, value);
        return this;
    }

    @Override
    public <T> Builder2<B> addOrdered(String predicateIRI, T value, @Nullable String datatypeIRI) {
        if (value == null) return this;
        if (value instanceof String string && string.isEmpty()) return this;
        checkCollectionDatatype(predicateIRI, datatypeIRI);
        addOrdered(predicateIRI, value);
        return this;
    }

    private void checkCollectionDatatype(String predicateIRI, @Nullable String datatypeIRI) {
        // Nota bene: This is, of course, actually stricter than what RDF would technically allow...
        // ... but this is intentional and matches intended strongly type safe generated code.
        if (datatypeIRI != null) {
            var previous = datatypes.putIfAbsent(predicateIRI, datatypeIRI);
            if (previous != null && !datatypeIRI.equals(previous))
                throw new IllegalStateException(
                        predicateIRI + " has another Datatype: " + previous);
        }
    }

    @Override
    @Deprecated
    @SuppressWarnings("unchecked")
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
    @Deprecated
    public Builder2<? extends PredicatesObjects> copy() {
        return this;
    }

    @Override
    public int hashCode() {
        return ThingHashCodeEqualsToString.hashCode(this);
    }

    @Override
    @SuppressWarnings({"EqualsWhichDoesntCheckParameterClass", "EqualsDoesntCheckParameterClass"})
    public boolean equals(Object obj) {
        return ThingHashCodeEqualsToString.equals(this, obj);
    }

    @Override
    public String toString() {
        return ThingHashCodeEqualsToString.toString(this);
    }

    @Override
    @SuppressWarnings("unchecked") // TODO How to remove (B) type cast?!
    public B build() {
        var immutableBuilder =
                ImmutablePredicatesObjects.builderWithExpectedSize(properties.size());
        deepBuildInto(immutableBuilder);
        return (B) immutableBuilder.build();
    }

    @SuppressWarnings("Immutable") // TODO This (tries to...) make deep copies of all objects...
    protected void deepBuildInto(
            PredicatesObjects.Builder<? extends IImmutablePredicatesObjects> immutableBuilder) {
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            var predicateIRI = entry.getKey();
            var object = entry.getValue();
            if (object instanceof ImmutableCollection.Builder<?> immutableCollectionBuilder)
                object = immutableCollectionBuilder.build();
            if (object instanceof List<?> list) object = ImmutableList.copyOf(list);
            if (object instanceof Set<?> list) object = ImmutableSet.copyOf(list);
            if (object instanceof MutablePredicatesObjects<?> mutablePredicatesObjects)
                object = mutablePredicatesObjects.build();
            // Keep these ^^^ conversions in sync with:
            ImmutableObjects.check(object);
            immutableBuilder.set(predicateIRI, object, datatype(predicateIRI));
        }
    }
}

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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

import com.google.common.collect.*;
import com.google.errorprone.annotations.ImmutableTypeParameter;

import dev.enola.thing.Literal;
import dev.enola.thing.PredicatesObjects;

import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

// NOT public, intentionally just package private!
class MutablePredicatesObjectsBuilder<B extends IImmutablePredicatesObjects>
        implements PredicatesObjects.Builder<B> {

    // Nota bene: It is tempting to want to use ImmutableMap.Builder internally here. However,
    // ImmutableMap.Builder only has put*() and no get() methods, which makes it unsuitable
    // here - because we need to be able to get() to implement add*().

    protected final Map<String, Object> properties;
    protected final Map<String, String> datatypes;

    public MutablePredicatesObjectsBuilder() {
        properties = Maps.newHashMapWithExpectedSize(8);
        datatypes = Maps.newHashMapWithExpectedSize(0);
    }

    public MutablePredicatesObjectsBuilder(int expectedSize) {
        properties = Maps.newHashMapWithExpectedSize(expectedSize); // exact
        datatypes = Maps.newHashMapWithExpectedSize(expectedSize / 4); // upper bound
    }

    protected MutablePredicatesObjectsBuilder(
            ImmutableMap<String, Object> properties, ImmutableMap<String, String> datatypes) {
        this.properties = new HashMap<>(properties);
        this.datatypes = new HashMap<>(datatypes);
    }

    @Override
    public <@ImmutableTypeParameter T> PredicatesObjects.Builder<B> set(
            String predicateIRI, T value) {
        if (value == null) return this;
        if (value instanceof String string && string.isEmpty()) return this;
        if (value instanceof Iterable iterable && Iterables.isEmpty(iterable)) return this;
        if (value instanceof Literal(String literalValue, String datatypeIRI))
            set(predicateIRI, literalValue, datatypeIRI);
        else properties.put(predicateIRI, value);
        return this;
    }

    @Override
    public <@ImmutableTypeParameter T> PredicatesObjects.Builder<B> set(
            String predicateIRI, T value, @Nullable String datatypeIRI) {
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
    public <@ImmutableTypeParameter T> PredicatesObjects.Builder<B> add(
            String predicateIRI, T value) {
        if (value == null) return this;
        if (value instanceof String string && string.isEmpty()) return this;
        var object = properties.get(predicateIRI);
        if (object == null) {
            var builder = ImmutableSet.builder();
            properties.put(predicateIRI, builder);
            builder.add(value);
        } else if (object instanceof ImmutableCollection.Builder builder) {
            builder.add(value);
        } else if (object instanceof Iterable iterable) {
            var builder = ImmutableSet.builder();
            properties.put(predicateIRI, builder);
            builder.addAll(iterable);
            builder.add(value);
        } else {
            var builder = ImmutableSet.builder();
            properties.put(predicateIRI, builder);
            builder.add(object);
            builder.add(value);
        }
        return this;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <@ImmutableTypeParameter T> PredicatesObjects.Builder<B> addAll(
            String predicateIRI, Iterable<T> values) {
        if (values == null) return this;
        if (Iterables.isEmpty(values)) return this;
        var object = properties.get(predicateIRI);
        if (object == null) {
            var builder = ImmutableSet.builder();
            properties.put(predicateIRI, builder);
            builder.addAll(values);
        } else if (object instanceof ImmutableCollection.Builder builder) {
            builder.addAll(values);
        } else if (object instanceof Iterable iterable) {
            var builder = ImmutableSet.builder();
            properties.put(predicateIRI, builder);
            builder.addAll(iterable);
            builder.addAll(values);
        } else {
            var builder = ImmutableSet.builder();
            properties.put(predicateIRI, builder);
            builder.add(object);
            builder.addAll(values);
        }
        return this;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <@ImmutableTypeParameter T> PredicatesObjects.Builder<B> addOrdered(
            String predicateIRI, T value) {
        if (value == null) return this;
        if (value instanceof String string && string.isEmpty()) return this;
        var object = properties.get(predicateIRI);
        if (object == null) {
            var builder = ImmutableList.builder();
            properties.put(predicateIRI, builder);
            builder.add(value);
        } else if (object instanceof ImmutableList.Builder listBuilder) {
            listBuilder.add(value);
        } else if (object instanceof ImmutableSet.Builder setBuilder) {
            var set = setBuilder.build();
            var listBuilder = ImmutableList.builderWithExpectedSize(set.size() + 1);
            properties.put(predicateIRI, listBuilder);
            listBuilder.addAll(set);
            listBuilder.add(value);
        } else if (object instanceof Iterable iterable) {
            var builder = ImmutableSet.builder();
            properties.put(predicateIRI, builder);
            builder.addAll(iterable);
            builder.add(value);
        } else {
            var builder = ImmutableList.builder();
            properties.put(predicateIRI, builder);
            builder.add(object);
            builder.add(value);
        }
        return this;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <@ImmutableTypeParameter T> PredicatesObjects.Builder<B> addAllOrdered(
            String predicateIRI, Iterable<T> values) {
        if (values == null) return this;
        if (Iterables.isEmpty(values)) return this;
        var object = properties.get(predicateIRI);
        if (object == null) {
            var builder = ImmutableList.builder();
            properties.put(predicateIRI, builder);
            builder.addAll(values);
        } else if (object instanceof ImmutableList.Builder listBuilder) {
            listBuilder.addAll(values);
        } else if (object instanceof ImmutableSet.Builder setBuilder) {
            var set = setBuilder.build();
            var listBuilder =
                    ImmutableList.builderWithExpectedSize(set.size() + Iterables.size(values));
            properties.put(predicateIRI, listBuilder);
            listBuilder.addAll(set);
            listBuilder.addAll(values);
        } else if (object instanceof Iterable iterable) {
            var builder = ImmutableSet.builder();
            properties.put(predicateIRI, builder);
            builder.addAll(iterable);
            builder.addAll(values);
        } else {
            var builder = ImmutableSet.builder();
            properties.put(predicateIRI, builder);
            builder.add(object);
            builder.addAll(values);
        }
        return this;
    }

    @Override
    public <@ImmutableTypeParameter T> PredicatesObjects.Builder<B> add(
            String predicateIRI, T value, @Nullable String datatypeIRI) {
        if (value == null) return this;
        if (value instanceof String string && string.isEmpty()) return this;
        checkCollectionDatatype(predicateIRI, datatypeIRI);
        add(predicateIRI, value);
        return this;
    }

    @Override
    public <@ImmutableTypeParameter T> PredicatesObjects.Builder<B> addAll(
            String predicateIRI, Iterable<T> values, @Nullable String datatypeIRI) {
        if (values == null) return this;
        checkCollectionDatatype(predicateIRI, datatypeIRI);
        addAll(predicateIRI, values);
        return this;
    }

    @Override
    public <@ImmutableTypeParameter T> PredicatesObjects.Builder<B> addOrdered(
            String predicateIRI, T value, @Nullable String datatypeIRI) {
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
    @SuppressWarnings("unchecked")
    public B build() {
        var pair = ImmutableObjects.build(properties, datatypes);
        return (B) new ImmutablePredicatesObjects(pair.properties(), pair.datatypes());
    }

    @Override
    public String toString() {
        return ThingHashCodeEqualsToString.toString(this, properties, datatypes);
    }
}

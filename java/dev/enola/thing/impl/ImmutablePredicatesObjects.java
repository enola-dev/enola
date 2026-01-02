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
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.ThreadSafe;

import dev.enola.thing.PredicatesObjects;

import org.jspecify.annotations.Nullable;

@Immutable
@ThreadSafe
// TODO Make ImmutablePredicatesObjects package private, and let users create them via the TBF (?)
public class ImmutablePredicatesObjects implements IImmutablePredicatesObjects {

    @SuppressWarnings("Immutable")
    // Immutability of Objects is guaranteed by @ImmutableTypeParameter in PredicatesObjects.Builder
    protected final ImmutableMap<String, Object> properties;

    protected final ImmutableMap<String, String> datatypes;

    protected ImmutablePredicatesObjects(
            ImmutableMap<String, Object> properties, ImmutableMap<String, String> datatypes) {
        this.properties = properties;
        this.datatypes = datatypes;
    }

    public static <T extends IImmutablePredicatesObjects>
            IImmutablePredicatesObjects.Builder<T> builder() {
        return new MutablePredicatesObjectsBuilder<>();
    }

    public static PredicatesObjects.Builder<? extends IImmutablePredicatesObjects>
            builderWithExpectedSize(int expectedSize) {
        return new MutablePredicatesObjectsBuilder<>(expectedSize);
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
        return ThingHashCodeEqualsToString.toString(this, properties, datatypes);
    }

    @Override
    public PredicatesObjects.Builder<? extends IImmutablePredicatesObjects> copy() {
        return new MutablePredicatesObjectsBuilder<>(properties(), datatypes());
    }
}

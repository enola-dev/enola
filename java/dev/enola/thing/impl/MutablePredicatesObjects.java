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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import dev.enola.thing.PredicatesObjects;
import dev.enola.thing.Thing;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressFBWarnings("EQ_DOESNT_OVERRIDE_EQUALS")
// skipcq: JAVA-W0100
public class MutablePredicatesObjects<B extends IImmutablePredicatesObjects>
        implements PredicatesObjects, PredicatesObjects.Builder<B> {

    protected @Nullable String iri;
    protected final Map<String, Object> properties;
    protected final Map<String, String> datatypes;

    public MutablePredicatesObjects() {
        properties = new HashMap<>();
        datatypes = new HashMap<>();
    }

    public MutablePredicatesObjects(int expectedSize) {
        properties = new HashMap<>(expectedSize); // exact
        datatypes = new HashMap<>(expectedSize); // upper bound
    }

    @Override
    public Builder<B> set(String predicateIRI, Object value) {
        properties.put(predicateIRI, value);
        return this;
    }

    @Override
    public Builder<B> set(String predicateIRI, Object value, @Nullable String datatypeIRI) {
        properties.put(predicateIRI, value);
        if (datatypeIRI != null) datatypes.put(predicateIRI, datatypeIRI);
        return this;
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
    public Builder<? extends PredicatesObjects> copy() {
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
            PredicatesObjects.Builder<? extends ImmutablePredicatesObjects> immutableBuilder) {
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            var predicateIRI = entry.getKey();
            var object = entry.getValue();
            if (object instanceof List<?> list) object = ImmutableList.copyOf(list);
            if (object instanceof Set<?> list) object = ImmutableSet.copyOf(list);
            if (object instanceof Thing) throw new IllegalStateException(object.toString());
            if (object instanceof MutablePredicatesObjects<?> mutablePredicatesObjects)
                object = mutablePredicatesObjects.build();
            immutableBuilder.set(predicateIRI, object, datatype(predicateIRI));
        }
    }
}

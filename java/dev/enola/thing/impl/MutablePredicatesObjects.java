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

import com.google.common.collect.*;

import dev.enola.thing.PredicatesObjects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.jspecify.annotations.Nullable;

import java.util.*;

// TODO Make MutablePredicatesObjects package private, and let users create them via the TBF (?)
@SuppressFBWarnings("EQ_DOESNT_OVERRIDE_EQUALS")
// skipcq: JAVA-W0100
public class MutablePredicatesObjects<B extends IImmutablePredicatesObjects>
        extends MutablePredicatesObjectsBuilder<B> implements PredicatesObjects {

    public MutablePredicatesObjects() {
        super();
    }

    public MutablePredicatesObjects(int expectedSize) {
        super(expectedSize);
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
}

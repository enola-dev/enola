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
package dev.enola.thing;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.ThreadSafe;

import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

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

    public static PredicatesObjects.Builder<? extends ImmutablePredicatesObjects> builder() {
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
    public Collection<String> predicateIRIs() {
        return properties.keySet();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @Nullable T get(String predicateIRI) {
        return (T) properties.get(predicateIRI);
    }

    public ImmutableMap<String, String> datatypes() {
        return datatypes;
    }

    @Override
    public @Nullable String datatype(String predicateIRI) {
        return datatypes.get(predicateIRI);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        // NO NEED: if (obj == null) return false;
        // NOT:     if (getClass() != obj.getClass()) return false;
        if (!(obj instanceof ImmutablePredicatesObjects other)) return false;
        return Objects.equals(this.properties, other.properties)
                && Objects.equals(this.datatypes, other.datatypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties, datatypes);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("properties", properties)
                .add("datatypes", datatypes)
                .toString();
    }

    @Override
    public PredicatesObjects.Builder<? extends ImmutablePredicatesObjects> copy() {
        return new Builder<>(properties(), datatypes());
    }

    private static final class Builder<B extends PredicatesObjects>
            implements PredicatesObjects.Builder<B> {

        private final ImmutableMap.Builder<String, Object> properties;
        private final ImmutableMap.Builder<String, String> datatypes;

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
        public PredicatesObjects.Builder set(String predicateIRI, Object value) {
            if (value instanceof Literal literal)
                set(predicateIRI, literal.value(), literal.datatypeIRI());
            else properties.put(predicateIRI, value);
            return this;
        }

        @Override
        public PredicatesObjects.Builder set(
                String predicateIRI, Object value, String datatypeIRI) {
            properties.put(predicateIRI, value);
            if (datatypeIRI != null) datatypes.put(predicateIRI, datatypeIRI);
            return this;
        }

        @Override
        public B build() {
            // TODO Remove (B) type cast
            return (B) new ImmutablePredicatesObjects(properties.build(), datatypes.build());
        }
    }
}

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
import com.google.errorprone.annotations.ThreadSafe;

import java.util.Objects;

@ThreadSafe
public final class ImmutableThing extends ImmutablePredicatesObjects implements Thing {

    private final String iri;

    public static Thing.Builder builder() {
        return new Builder();
    }

    public static Thing.Builder builderWithExpectedSize(int expectedSize) {
        return new Builder(expectedSize);
    }

    private ImmutableThing(
            String iri,
            ImmutableMap<String, Object> properties,
            ImmutableMap<String, String> datatypes) {
        super(properties, datatypes);
        this.iri = iri;
    }

    @Override
    public String iri() {
        return iri;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        // NO NEED: if (obj == null) return false;
        // NOT:     if (getClass() != obj.getClass()) return false;
        if (!(obj instanceof ImmutableThing)) return false;
        final ImmutableThing other = (ImmutableThing) obj;
        return Objects.equals(this.iri, other.iri)
                && Objects.equals(this.properties(), other.properties())
                && Objects.equals(this.datatypes(), other.datatypes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(iri, properties(), datatypes());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("iri", iri)
                .add("properties", properties())
                .add("datatypes", datatypes())
                .toString();
    }

    @Override
    public Thing.Builder copy() {
        return new Builder(iri, properties(), datatypes());
    }

    private static final class Builder implements Thing.Builder {

        private String iri;
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
        public Thing.Builder iri(String iri) {
            if (this.iri != null) throw new IllegalStateException("IRI already set: " + this.iri);
            this.iri = iri;
            return this;
        }

        @Override
        public Thing.Builder set(String predicateIRI, Object value) {
            if (value instanceof Literal literal)
                set(predicateIRI, literal.value(), literal.datatypeIRI());
            else properties.put(predicateIRI, value);
            return this;
        }

        @Override
        public Thing.Builder set(String predicateIRI, Object value, String datatypeIRI) {
            properties.put(predicateIRI, value);
            if (datatypeIRI != null) datatypes.put(predicateIRI, datatypeIRI);
            return this;
        }

        @Override
        public Thing build() {
            return new ImmutableThing(iri, properties.build(), datatypes.build());
        }
    }
}

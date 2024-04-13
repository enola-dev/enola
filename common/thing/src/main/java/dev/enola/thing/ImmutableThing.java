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

import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.ThreadSafe;

import dev.enola.thing.spi.AbstractThing;

import java.util.Collection;

@ThreadSafe
public final class ImmutableThing extends AbstractThing {

    private final String iri;
    private final ImmutableMap<String, Object> properties;
    private final ImmutableMap<String, String> datatypes;

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
        this.iri = iri;
        this.properties = properties;
        this.datatypes = datatypes;
    }

    @Override
    public String iri() {
        return iri;
    }

    @Override
    public Collection<String> predicateIRIs() {
        return properties.keySet();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String predicateIRI) {
        return (T) properties.get(predicateIRI);
    }

    @Override
    public String datatype(String predicateIRI) {
        return datatypes.get(predicateIRI);
    }

    private static final class Builder implements Thing.Builder {

        private String iri;
        private final ImmutableMap.Builder<String, Object> properties;
        private final ImmutableMap.Builder<String, String> datatypes;

        public Builder() {
            properties = ImmutableMap.builder();
            datatypes = ImmutableMap.builder();
        }

        public Builder(int expectedSize) {
            properties = ImmutableMap.builderWithExpectedSize(expectedSize); // exact
            datatypes = ImmutableMap.builderWithExpectedSize(expectedSize); // upper bound
        }

        @Override
        public Thing.Builder iri(String iri) {
            this.iri = iri;
            return this;
        }

        @Override
        public Thing.Builder set(String predicateIRI, Object value) {
            properties.put(predicateIRI, value);
            return this;
        }

        @Override
        public Thing.Builder set(String predicateIRI, Object value, String datatypeIRI) {
            set(predicateIRI, value);
            datatypes.put(predicateIRI, datatypeIRI);
            return this;
        }

        @Override
        public Thing build() {
            return new ImmutableThing(iri, properties.build(), datatypes.build());
        }

        // @Override
        // public Supplier<Thing.Builder> builderSupplier() {
        //     return Builder::new;
        // }
    }
}

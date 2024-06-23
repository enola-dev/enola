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
package dev.enola.thing.java.test;

import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.ThreadSafe;

import dev.enola.datatype.Datatype;
import dev.enola.thing.*;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.jspecify.annotations.Nullable;

@Immutable
@ThreadSafe
public class ImmutableTestThing extends GenJavaThing implements TestThing {

    // TODO This, and similar, classes should (eventually) be automagically code generated...
    // TODO Consider if an existing Beans code generator could be used? E.g. AutoValue, or
    // Immutables.org?

    private final @Nullable String label;
    private final @Nullable Integer number;

    protected ImmutableTestThing(
            String iri,
            @Nullable String label,
            @Nullable Integer number,
            ImmutableMap<String, Object> dynamic_properties,
            ImmutableMap<String, String> dynamic_datatypes) {
        super(iri, datatypesOfNonNullFields(label, number), dynamic_properties, dynamic_datatypes);
        this.label = label;
        this.number = number;
    }

    private static ImmutableMap<String, Datatype<?>> datatypesOfNonNullFields(
            @Nullable String label, @Nullable Integer number) {
        var builder = ImmutableMap.<String, Datatype<?>>builderWithExpectedSize(2);
        if (label != null) builder.put(KIRI.RDFS.LABEL, dev.enola.datatype.Datatypes.STRING);
        if (number != null) builder.put(NUMBER_URI, dev.enola.model.xsd.Datatypes.INT);
        return builder.build();
    }

    public static ImmutableTestThing create(String iri, String label, int number) {
        return new ImmutableTestThing(iri, label, number, ImmutableMap.of(), ImmutableMap.of());
    }

    public static Builder<? extends ImmutableTestThing> builder() {
        return new Builder<>();
    }

    @Override
    public @Nullable Integer number() {
        return number;
    }

    @Override
    public @Nullable String label() {
        return label;
    }

    @Override
    public ImmutableMap<String, Object> properties() {
        // TODO Compute this lazily once only, and cache it in a field?
        if (super.properties.isEmpty())
            return ImmutableMap.of(KIRI.RDFS.LABEL, label, NUMBER_URI, number);
        var builder = ImmutableMap.<String, Object>builder().putAll(super.properties);
        if (label != null) builder.put(KIRI.RDFS.LABEL, label);
        if (number != null) builder.put(NUMBER_URI, number);
        return builder.build();
    }

    @Override
    public <T> @Nullable T get(String predicateIRI) {
        // NB: Must be kept in sync with set(String predicateIRI, ...)
        if (KIRI.RDFS.LABEL.equals(predicateIRI)) return (T) label;
        else if (NUMBER_URI.equals(predicateIRI)) return (T) number;
        else return super.get(predicateIRI);
    }

    @Override
    public Builder<? extends ImmutableTestThing> copy() {
        // TODO Implement copy() correctly... see TestThingTest#copy(), it illustrates why NOK
        //  return new Builder<>(iri(), label, number, super.properties, datatypes());
        throw new UnsupportedOperationException("TODO");
    }

    @SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
    public static class Builder<B extends ImmutableTestThing> // skipcq: JAVA-E0169
            extends ImmutableThing.Builder<ImmutableTestThing>
            implements TestThing.Builder<ImmutableTestThing> {

        private @Nullable String label;
        private @Nullable Integer number;

        private Builder() {
            super(2);
        }

        protected Builder(
                String iri,
                String label,
                int number,
                final ImmutableMap<String, Object> properties,
                final ImmutableMap<String, String> datatypes) {
            super(iri, properties, datatypes);
            this.label = label;
            this.number = number;
        }

        @Override
        public Builder<B> iri(String iri) {
            super.iri(iri);
            return this;
        }

        @Override
        public Builder<B> label(String label) {
            this.label = label;
            return this;
        }

        @Override
        public Builder<B> number(Integer number) {
            this.number = number;
            return this;
        }

        @Override
        public Thing.Builder<ImmutableTestThing> set(
                String predicateIRI, Object value, String datatypeIRI) {
            // NB: Must be kept in sync with get(String predicateIRI)
            if (KIRI.RDFS.LABEL.equals(predicateIRI)) {
                label = (String) value;
                return this;
            } else if (NUMBER_URI.equals(predicateIRI)) {
                number = (Integer) value;
                return this;
            } else return super.set(predicateIRI, value, datatypeIRI);
        }

        @Override
        public Thing.Builder<ImmutableTestThing> set(String predicateIRI, Object value) {
            return this.set(predicateIRI, value, null);
        }

        @Override
        public B build() {
            if (iri == null) throw new IllegalStateException("Cannot build Thing without IRI");
            // TODO Remove (B) type cast
            return (B)
                    new ImmutableTestThing(
                            iri, label, number, properties.build(), datatypes.build());
        }
    }
}

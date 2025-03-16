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

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.ThreadSafe;

import dev.enola.thing.Link;
import dev.enola.thing.Thing;
import dev.enola.thing.java.HasType;
import dev.enola.thing.java.RdfAnnotations;
import dev.enola.thing.java.TBF;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.jspecify.annotations.Nullable;

@Immutable
@ThreadSafe
// TODO Make ImmutableThing package private, and let users create them only via the #FACTORY TBF
public class ImmutableThing extends ImmutablePredicatesObjects implements IImmutableThing {

    private final String iri;

    protected ImmutableThing(
            String iri,
            ImmutableMap<String, Object> properties,
            ImmutableMap<String, String> datatypes) {
        super(properties, datatypes);
        this.iri = requireNonNull(iri, "iri");
    }

    // TODO Is <? extends IImmutableThing> useless on this static method? Same as <IImmutableThing>?
    public static Thing.Builder<? extends IImmutableThing> builder() {
        return new Builder<>(ImmutableThing::new);
    }

    public static Thing.Builder<? extends IImmutableThing> builderWithExpectedSize(
            int expectedSize) {
        return new Builder<>(ImmutableThing::new, expectedSize);
    }

    // TODO Move this into .java package?
    // TODO Rename ImmutableThing.FACTORY to BUILDER_FACTORY
    public static final TBF FACTORY =
            new TBF() {
                @Override
                public boolean handles(String typeIRI) {
                    return true;
                }

                @Override
                @SuppressWarnings("unchecked")
                public Thing.Builder<Thing> create(String typeIRI) {
                    var builder = builder();
                    // TODO Replace set() with add(HasType.IRI, typeIRI) when Builder2...
                    builder.set(HasType.IRI, ImmutableList.of(new Link(typeIRI)));
                    return (Thing.Builder) builder;
                }

                @Override
                public boolean handles(Class<?> builderInterface) {
                    return builderInterface.equals(Thing.Builder.class);
                }

                @Override
                @SuppressWarnings("unchecked")
                public <T extends Thing, B extends Thing.Builder<T>> B create(
                        Class<B> builderInterface, Class<T> thingInterface) {
                    var builder = (B) builder();
                    RdfAnnotations.addType(thingInterface, builder);
                    return builder;
                }

                @Override
                @SuppressWarnings("unchecked")
                public <T extends Thing, B extends Thing.Builder<T>> B create(
                        Class<B> builderInterface, Class<T> thingInterface, int expectedSize) {
                    var builder = (B) builderWithExpectedSize(expectedSize);
                    RdfAnnotations.addType(thingInterface, builder);
                    return builder;
                }
            };

    public interface Factory {
        ImmutableThing create(
                String iri,
                ImmutableMap<String, Object> properties,
                ImmutableMap<String, String> datatypes);
    }

    @Override
    public String iri() {
        return iri;
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
    public Thing.Builder<? extends IImmutableThing> copy() {
        return new Builder<>(ImmutableThing::new, iri(), properties(), datatypes());
    }

    // TODO make inner class ImmutableThing.Builder protected
    @SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
    public static class Builder<B extends IImmutableThing> // skipcq: JAVA-E0169
            extends ImmutablePredicatesObjects.Builder<B> implements Thing.Builder<B> {

        private final Factory factory;

        protected @Nullable String iri;

        protected Builder() {
            this(ImmutableThing::new);
        }

        protected Builder(Factory factory) {
            super();
            this.factory = factory;
        }

        protected Builder(Factory factory, int expectedSize) {
            super(expectedSize);
            this.factory = factory;
        }

        protected Builder(
                Factory factory,
                String iri,
                final ImmutableMap<String, Object> properties,
                final ImmutableMap<String, String> datatypes) {
            super(properties, datatypes);
            this.factory = factory;
            iri(iri);
        }

        @Override
        public Thing.Builder<B> iri(String iri) {
            if (this.iri != null && !this.iri.equals((iri)))
                throw new IllegalStateException(
                        "IRI already set: " + this.iri + ", cannot set to: " + iri);
            this.iri = requireNonNull(iri);
            return this;
        }

        @Override
        public Thing.Builder<B> set(String predicateIRI, Object value) {
            super.set(predicateIRI, value);
            return this;
        }

        @Override
        public Thing.Builder<B> set(
                String predicateIRI, Object value, @Nullable String datatypeIRI) {
            super.set(predicateIRI, value, datatypeIRI);
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public B build() {
            if (iri == null)
                throw new IllegalStateException(PackageLocalConstants.NEEDS_IRI_MESSAGE);
            // NB: ImmutablePredicatesObjects.Builder#build() has the same:
            var immutableProperties = ImmutableMap.copyOf(this.properties);
            var immutableDataypes = ImmutableMap.copyOf(this.datatypes);
            return (B) factory.create(iri, immutableProperties, immutableDataypes);
        }

        @Override
        public String toString() {
            return "Builder{"
                    + "iri="
                    + iri
                    + ", properties="
                    + properties
                    + ", datatypes="
                    + datatypes
                    + '}';
        }
    }
}

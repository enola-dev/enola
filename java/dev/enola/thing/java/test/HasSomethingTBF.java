/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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

import dev.enola.thing.Thing;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.TBF;

// TODO Generate this, from a model
public final class HasSomethingTBF implements TBF {

    @Override
    public <B extends Thing.Builder<?>> boolean handles(Class<B> builderInterface) {
        return builderInterface.equals(HasSomething.Builder.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Thing, B extends Thing.Builder<T>> B create(
            Class<B> builderInterface, Class<T> thingInterface) {
        if (!builderInterface.equals(HasSomething.Builder.class)
                || !thingInterface.equals(HasSomething.class))
            throw new IllegalArgumentException(builderInterface + ", " + thingInterface);
        return (B) new HasSomethingBuilder();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Thing, B extends Thing.Builder<T>> B create(
            Class<B> builderInterface, Class<T> thingInterface, int expectedSize) {
        if (!builderInterface.equals(HasSomething.Builder.class)
                || !thingInterface.equals(HasSomething.class))
            throw new IllegalArgumentException(builderInterface + ", " + thingInterface);
        return (B) new HasSomethingBuilder(expectedSize);
    }

    private static final class HasSomethingBuilder extends ImmutableThing.Builder<HasSomething>
            implements HasSomething.Builder<HasSomething> {

        private HasSomethingBuilder(int expectedSize) {
            super(HasSomethingImpl::new, expectedSize);
        }

        private HasSomethingBuilder() {
            super(HasSomethingImpl::new);
        }

        private HasSomethingBuilder(ImmutableThing.Factory factory, HasSomething hasSomething) {
            super(factory, hasSomething.iri(), hasSomething.properties(), hasSomething.datatypes());
        }

        @Override
        public HasSomething.Builder<HasSomething> iri(String iri) {
            super.iri(iri);
            return this;
        }
    }

    private static final class HasSomethingImpl extends ImmutableThing implements HasSomething {
        private HasSomethingImpl(
                String iri,
                ImmutableMap<String, Object> properties,
                ImmutableMap<String, String> datatypes) {
            super(iri, properties, datatypes);
        }

        @Override
        public HasSomething.Builder<? extends HasSomething> copy() {
            return new HasSomethingBuilder(HasSomethingImpl::new, this);
        }
    }
}

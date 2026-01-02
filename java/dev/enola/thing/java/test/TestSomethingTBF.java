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
package dev.enola.thing.java.test;

import com.google.common.collect.ImmutableMap;

import dev.enola.thing.Thing;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.TBF;

// TODO Is this really needed?!
// TODO Generate this, from a model
public final class TestSomethingTBF implements TBF {
    // TODO make this package-local, and accessible only via TestSomething.Builder?

    @Override
    public boolean handles(String typeIRI) {
        return TestSomething.CLASS_IRI.equals(typeIRI);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Thing.Builder<Thing> create(String typeIRI) {
        if (!handles(typeIRI)) throw new IllegalArgumentException(typeIRI);
        return (Thing.Builder) new TestSomethingBuilder();
    }

    @Override
    public boolean handles(Class<?> builderInterface) {
        return builderInterface.equals(TestSomething.Builder.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Thing, B extends Thing.Builder<T>> B create(
            Class<B> builderInterface, Class<T> thingInterface) {
        if (!builderInterface.equals(TestSomething.Builder.class)
                || !thingInterface.equals(TestSomething.class))
            throw new IllegalArgumentException(builderInterface + ", " + thingInterface);
        return (B) new TestSomethingBuilder();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Thing, B extends Thing.Builder<T>> B create(
            Class<B> builderInterface, Class<T> thingInterface, int expectedSize) {
        if (!builderInterface.equals(TestSomething.Builder.class)
                || !thingInterface.equals(TestSomething.class))
            throw new IllegalArgumentException(builderInterface + ", " + thingInterface);
        return (B) new TestSomethingBuilder(expectedSize);
    }

    private static final class TestSomethingBuilder extends ImmutableThing.Builder<TestSomething>
            implements TestSomething.Builder<TestSomething> {

        private TestSomethingBuilder(int expectedSize) {
            super(TestSomethingImpl::new, expectedSize);
            addType(TestSomething.CLASS_IRI);
        }

        private TestSomethingBuilder() {
            super(TestSomethingImpl::new);
            addType(TestSomething.CLASS_IRI);
        }

        private TestSomethingBuilder(ImmutableThing.Factory factory, TestSomething testSomething) {
            super(
                    factory,
                    testSomething.iri(),
                    testSomething.properties(),
                    testSomething.datatypes());
        }

        @Override
        public TestSomething.Builder<TestSomething> iri(String iri) {
            super.iri(iri);
            return this;
        }
    }

    private static final class TestSomethingImpl extends ImmutableThing implements TestSomething {
        private TestSomethingImpl(
                String iri,
                ImmutableMap<String, Object> properties,
                ImmutableMap<String, String> datatypes) {
            super(iri, properties, datatypes);
        }

        @Override
        public TestSomething.Builder<? extends TestSomething> copy() {
            return new TestSomethingBuilder(TestSomethingImpl::new, this);
        }
    }
}

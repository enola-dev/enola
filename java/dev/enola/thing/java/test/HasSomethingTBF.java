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
    @SuppressWarnings("unchecked")
    public <T extends Thing, B extends Thing.Builder<T>> B create(
            Class<B> builderInterface, Class<T> thingInterface) {
        if (!builderInterface.equals(HasSomething.Builder.class)
                || !thingInterface.equals(HasSomething.class))
            throw new IllegalArgumentException(builderInterface + ", " + thingInterface);
        return (B) new HasSomethingBuilder();
    }

    private static final class HasSomethingBuilder extends ImmutableThing.Builder<HasSomething>
            implements HasSomething.Builder<HasSomething> {

        private HasSomethingBuilder() {
            super(HasSomethingImpl::new);
        }
    }

    private static final class HasSomethingImpl extends ImmutableThing implements HasSomething {
        private HasSomethingImpl(
                String iri,
                ImmutableMap<String, Object> properties,
                ImmutableMap<String, String> datatypes) {
            super(iri, properties, datatypes);
        }
    }
}

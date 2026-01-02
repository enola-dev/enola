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
package dev.enola.thing.java;

import dev.enola.thing.Thing;
import dev.enola.thing.impl.IImmutableThing;
import dev.enola.thing.java.test.TestSomething;

/** TBF is a Thing Builder Factory. */
public interface TBF {

    Thing.Builder<Thing> create(String typeIRI);

    /**
     * Creates a new {@link Thing.Builder} instance of (Java) type T.
     *
     * <p>You would typically not use this directly, but via a generated method on the respective
     * thing interface, e.g. {@link TestSomething#builder(TBF)}.
     */
    // TODO Fix that callers have to use  @SuppressWarnings("unchecked")
    // TODO Is javac really too stupid to validate callers, as intended?
    // e.g. create(Property.Builder.class, Class.class); should not compile - but does :(
    //   Or is it really a bug in javac?! Unlikely, but try latest JDK?
    //   Otherwise, simplify this... we only really need Builder class?
    <T extends Thing, B extends Thing.Builder<T>> B create(
            Class<B> builderInterface, Class<T> thingInterface, int expectedSize);

    <T extends Thing, B extends Thing.Builder<T>> B create(
            Class<B> builderInterface, Class<T> thingInterface);

    @SuppressWarnings("unchecked")
    // TODO This ^^^ is wrong, in case of MutableThing.. use Thing.Builder<Thing> instead?
    default Thing.Builder<IImmutableThing> create() {
        return create(Thing.Builder.class, Thing.class);
    }

    default Thing.Builder<IImmutableThing> create(int expectedSize) {
        return create(Thing.Builder.class, Thing.class, expectedSize);
    }

    default boolean handles(Class<?> builderInterface) {
        // TODO Push down default implementation into all implementations
        return true;
    }

    boolean handles(String typeIRI);
}

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
package dev.enola.thing.java2;

import dev.enola.thing.Thing;
import dev.enola.thing.impl.IImmutableThing;

/** TBF is a Thing Builder Factory. */
@FunctionalInterface
public interface TBF {

    /**
     * Creates a new {@link Thing.Builder} instance of (Java) type T.
     *
     * <p>T may be an interface, not a concrete class.
     */
    <T extends Thing, B extends Thing.Builder<?>> B create(
            Class<B> builderClass, Class<T> thingClass);

    @SuppressWarnings("unchecked")
    default Thing.Builder<IImmutableThing> create() {
        return create(Thing.Builder.class, Thing.class);
    }
}

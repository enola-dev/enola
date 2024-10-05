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
package dev.enola.thing.impl;

import dev.enola.thing.Thing;
import dev.enola.thing.ThingTester;
import dev.enola.thing.java2.TBF;

public class MutableThingTest extends ThingTester {

    @Override
    protected TBF getThingBuilderFactory() {
        return new TBF() {
            @Override
            @SuppressWarnings({"rawtypes", "unchecked"})
            public <T extends Thing, B extends Thing.Builder<?>> B create(
                    Class<B> builderClass, Class<T> thingClass) {
                if (builderClass.equals(Thing.Builder.class) && thingClass.equals(Thing.class))
                    return (B) new MutableThing(3);
                else
                    throw new IllegalArgumentException(
                            "This implementation does not support "
                                    + builderClass
                                    + " and "
                                    + thingClass);
            }
        };
    }
}

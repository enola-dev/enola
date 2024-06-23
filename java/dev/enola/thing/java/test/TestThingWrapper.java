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

import dev.enola.thing.KIRI;
import dev.enola.thing.Thing;
import dev.enola.thing.java.DelegatingThing;

import org.jspecify.annotations.Nullable;

public class TestThingWrapper extends DelegatingThing implements TestThing {

    // TODO This class, like MutableTestThing & ImmutableTestThing, should be generated...

    public TestThingWrapper(Thing delegate) {
        super(delegate);
    }

    // NB: Getters are exactly the same in MutableTestThing

    @Override
    public @Nullable String label() {
        return get(KIRI.RDFS.LABEL, String.class);
    }

    @Override
    public @Nullable Integer number() {
        return get(NUMBER_URI, Integer.class);
    }

    @Override
    public Thing.Builder<? extends Thing> copy() {
        throw new UnsupportedOperationException("TODO");
    }
}

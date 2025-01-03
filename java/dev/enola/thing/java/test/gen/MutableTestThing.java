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
package dev.enola.thing.java.test.gen;

import dev.enola.thing.KIRI;
import dev.enola.thing.impl.MutableThing;
import dev.enola.thing.java.test.TestThing;

import org.jspecify.annotations.Nullable;

/**
 * Implementation of {@link TestThing} based on {@link MutableThing} (with the same limitations).
 *
 * <p>Prefer using {@link ImmutableTestThing} in general.
 */
public class MutableTestThing extends MutableThing<BetterImmutableTestThing>
        implements TestThing, TestThing.Builder<BetterImmutableTestThing> {

    // TODO This class, like ImmutableTestThing, should (eventually) be generated...

    @Override
    public TestThing.Builder<BetterImmutableTestThing> label(String label) {
        set(KIRI.RDFS.LABEL, label);
        return this;
    }

    @Override
    public TestThing.Builder<BetterImmutableTestThing> number(Integer number) {
        set(NUMBER_URI, number);
        return this;
    }

    // NB: Getters are exactly the same in TestThingWrapper

    @Override
    public @Nullable String label() {
        return get(KIRI.RDFS.LABEL, String.class);
    }

    @Override
    public @Nullable Integer number() {
        return get(NUMBER_URI, Integer.class);
    }
}

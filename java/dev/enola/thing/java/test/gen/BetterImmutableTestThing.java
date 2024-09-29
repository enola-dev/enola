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
package dev.enola.thing.java.test.gen;

import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.ThreadSafe;

import dev.enola.thing.KIRI;
import dev.enola.thing.impl.IImmutableThing;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.test.TestThing;

import org.jspecify.annotations.Nullable;

@Immutable
@ThreadSafe
public class BetterImmutableTestThing extends ImmutableThing implements TestThing, IImmutableThing {

    BetterImmutableTestThing(
            String iri,
            ImmutableMap<String, Object> properties,
            ImmutableMap<String, String> datatypes) {
        super(iri, properties, datatypes);
    }

    @Override
    public @Nullable Integer number() {
        return get(NUMBER_URI, Integer.class);
    }

    @Override
    public @Nullable String label() {
        return get(KIRI.RDFS.LABEL, String.class);
    }
}

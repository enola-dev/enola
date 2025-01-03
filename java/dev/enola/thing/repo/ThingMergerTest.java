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
package dev.enola.thing.repo;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableSet;

import dev.enola.thing.KIRI;
import dev.enola.thing.impl.ImmutableThing;

import org.junit.Test;

public class ThingMergerTest {

    @Test
    public void origins() {
        var thing1 =
                ImmutableThing.builder()
                        .iri("http://example.org/a")
                        .set(KIRI.E.ORIGIN, "file:a.ttl")
                        .build();
        var thing2 =
                ImmutableThing.builder()
                        .iri("http://example.org/a")
                        .set(KIRI.E.ORIGIN, "file:x.ttl")
                        .build();
        var thing3 =
                ImmutableThing.builder()
                        .iri("http://example.org/a")
                        .set(KIRI.E.ORIGIN, ImmutableSet.of("file:x.ttl", "file:a.ttl"))
                        .build();

        var thing = ThingMerger.merge(thing1, thing2);
        assertThat(thing).isEqualTo(thing3);
    }
}

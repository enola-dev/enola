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

import org.junit.Test;

public class ThingsBuilderTest {

    @Test
    public void build() {
        var thingIRI = "http://example.com";
        var predicateIRI = "http://example.com/predicate";
        var thingsBuilder = new TypedThingsBuilder();

        var builder = thingsBuilder.getBuilder(thingIRI);
        builder.set(predicateIRI, "hi");

        builder = thingsBuilder.getBuilder(thingIRI);
        var thing = builder.build();

        assertThat(thing.getString(predicateIRI)).isEqualTo("hi");
    }
}

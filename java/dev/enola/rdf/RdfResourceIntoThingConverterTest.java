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
package dev.enola.rdf;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.context.TLC;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.datatype.DatatypeRepositoryBuilder;
import dev.enola.thing.Thing;
import dev.enola.thing.io.ResourceIntoThingConverters;

import org.junit.Test;

public class RdfResourceIntoThingConverterTest {

    @Test
    public void convert() {
        try (var ctx = TLC.open()) {
            ctx.push(DatatypeRepository.class, new DatatypeRepositoryBuilder().build());
            ResourceIntoThingConverters<Thing> c = new ResourceIntoThingConverters<>();
            var thing = c.convert(new ClasspathResource("picasso.ttl")).getFirst().build();
            assertThat(thing.iri()).isEqualTo("http://example.enola.dev/Dalí");
        }
    }
}

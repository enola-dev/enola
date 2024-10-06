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
package dev.enola.thing;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.thing.java2.TBF;

import org.junit.Before;
import org.junit.Test;

public abstract class ThingTester {

    // TODO Move some of the generic tests from ImmutableThingTest up here

    protected static final String THING_IRI = "https://example.org/thing";
    protected static final String PREDICATE_IRI = "https://example.org/predicate";

    protected abstract TBF getThingBuilderFactory();

    private Thing.Builder<Thing> thingBuilder;

    @Before
    public void setUp() {
        thingBuilder = getThingBuilderFactory().create();
    }

    @Test(expected = IllegalStateException.class)
    public void noIRI() {
        thingBuilder.build();
    }

    @Test
    public void empty() {
        thingBuilder.iri(THING_IRI);
        var thing = thingBuilder.build();
        assertThat(thing.getString(PREDICATE_IRI)).isNull();
    }
}

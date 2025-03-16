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
package dev.enola.thing.impl;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.thing.KIRI;
import dev.enola.thing.Thing;
import dev.enola.thing.ThingTester;
import dev.enola.thing.java.TBF;

import org.junit.Test;

import java.util.List;

public class MutableThingTest extends ThingTester {

    // TODO Once ImmutableThing.Builder extends (or is) Builder2, move this up to ThingTester

    @Override
    protected TBF getThingBuilderFactory() {
        return MutableThing.FACTORY;
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void add() {
        thingBuilder.iri(THING_IRI);
        var thingBuilder2 = (Thing.Builder2) thingBuilder;
        thingBuilder2.add(PREDICATE_IRI, "a");
        thingBuilder2.add(PREDICATE_IRI, "b");
        var thing = thingBuilder2.build();
        assertThat(thing.get(PREDICATE_IRI, Iterable.class)).containsExactly("a", "b");
        assertThat(thing.isIterable(PREDICATE_IRI)).isTrue();
        assertThat(thing.isOrdered(PREDICATE_IRI)).isFalse();
    }

    @Test
    public void addAll() {
        thingBuilder.iri(THING_IRI);
        var thingBuilder2 = (Thing.Builder2) thingBuilder;
        thingBuilder2.addAll(PREDICATE_IRI, List.of("a", "b"));
        var thing = thingBuilder2.build();
        assertThat(thing.get(PREDICATE_IRI, Iterable.class)).containsExactly("a", "b");
    }

    @Test
    public void addAllWithDatatype() {
        thingBuilder.iri(THING_IRI);
        var thingBuilder2 = (Thing.Builder2) thingBuilder;
        thingBuilder2.addAll(
                PREDICATE_IRI, List.of("https://vorburger.ch"), KIRI.SCHEMA.URL_DATATYPE);
        var thing = thingBuilder2.build();
        assertThat(thing.get(PREDICATE_IRI, Iterable.class))
                .containsExactly("https://vorburger.ch");
        assertThat(thing.datatype(PREDICATE_IRI)).isEqualTo(KIRI.SCHEMA.URL_DATATYPE);
    }

    @Test
    public void addToSingle() {
        thingBuilder.iri(THING_IRI);
        var thingBuilder2 = (Thing.Builder2<?>) thingBuilder;
        thingBuilder2.set(PREDICATE_IRI, "a");
        thingBuilder2.add(PREDICATE_IRI, "b");
        var thing = thingBuilder2.build();
        assertThat(thing.get(PREDICATE_IRI, Iterable.class)).containsExactly("a", "b");
    }

    @Test
    public void addAllToSingle() {
        thingBuilder.iri(THING_IRI);
        var thingBuilder2 = (Thing.Builder2<?>) thingBuilder;
        thingBuilder2.set(PREDICATE_IRI, "a");
        thingBuilder2.addAll(PREDICATE_IRI, List.of("b", "c"));
        var thing = thingBuilder2.build();
        assertThat(thing.get(PREDICATE_IRI, Iterable.class)).containsExactly("a", "b", "c");
    }
}

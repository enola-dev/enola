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
import dev.enola.thing.java2.TBF;

import org.junit.Test;

import java.util.List;

public class MutableThingTest extends ThingTester {

    @Override
    protected TBF getThingBuilderFactory() {
        return new TBF() {
            @Override
            @SuppressWarnings({"rawtypes", "unchecked"})
            public <T extends Thing, B extends Thing.Builder<?>> B create(
                    Class<B> builderInterface, Class<T> thingInterface) {
                if (builderInterface.equals(Thing.Builder.class)
                        && thingInterface.equals(Thing.class)) return (B) new MutableThing(3);
                else
                    throw new IllegalArgumentException(
                            "This implementation does not support "
                                    + builderInterface
                                    + " and "
                                    + thingInterface);
            }
        };
    }

    // TODO Once ImmutableThing.Builder extends (or is) Builder2, move this up to ThingTester

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
}

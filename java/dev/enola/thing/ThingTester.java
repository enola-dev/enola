/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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

import com.google.common.collect.ImmutableSet;

import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.model.Datatypes;
import dev.enola.thing.impl.IImmutableThing;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.TBF;
import dev.enola.thing.repo.ThingRepositoriesTest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.List;

public abstract class ThingTester {

    private static final String THING_IRI = "https://example.org/thing";
    private static final String PREDICATE_IRI = "https://example.org/predicate";

    private Thing.Builder<IImmutableThing> thingBuilder;

    protected abstract TBF getThingBuilderFactory();

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
        assertThat(thing.datatypes()).isEmpty();
        assertThat(thing.predicateIRIs()).isEmpty();
    }

    @Test
    public void insertion() {
        thingBuilder.iri(THING_IRI);
        thingBuilder.set("b", "B");
        thingBuilder.set("a", "A");
        var thing = thingBuilder.build();
        assertThat(thing.predicateIRIs()).containsExactly("b", "a");
    }

    @Test
    public void datatype1() {
        thingBuilder.iri(THING_IRI);
        var value = "http://example.org/hi/{NUMBER}";
        var datatypeIRI = "https://enola.dev/enola:IRITemplate";
        thingBuilder.set(PREDICATE_IRI, value, datatypeIRI);
        var thing = thingBuilder.build();
        assertThat(thing.datatypes()).containsExactly(PREDICATE_IRI, datatypeIRI);
        assertThat(thing.datatype(PREDICATE_IRI)).isEqualTo(datatypeIRI);
    }

    @Test // TODO This is a mess - Literal should be removed!
    public void literal() {
        thingBuilder.iri(THING_IRI);
        var datatypeIRI = "http://www.w3.org/2001/XMLSchema#date";
        thingBuilder.set(PREDICATE_IRI, new Literal("2024-10-06", datatypeIRI));
        var thing = thingBuilder.build();
        assertThat(thing.getString(PREDICATE_IRI)).isEqualTo("2024-10-06");
        assertThat(thing.datatype(PREDICATE_IRI)).isEqualTo(datatypeIRI);
    }

    @Test(expected = IllegalArgumentException.class)
    // TODO This is a mess - Literal should be removed!
    public void literalAndDatatype() {
        thingBuilder.iri(THING_IRI);
        var datatypeIRI = "http://www.w3.org/2001/XMLSchema#date";
        thingBuilder.set(PREDICATE_IRI, new Literal("2024-10-06", datatypeIRI), datatypeIRI);
    }

    @Test
    // TODO Test if we can CLEAR a previously set value!
    public void setNullIsIgnored() {
        thingBuilder.iri(THING_IRI);
        thingBuilder.set(PREDICATE_IRI, null);
        var thing = thingBuilder.build();
        assertThat(thing.predicateIRIs()).isEmpty();
    }

    @Test
    public void setEmptyStringIsIgnored() {
        thingBuilder.iri(THING_IRI);
        thingBuilder.set(PREDICATE_IRI, "");
        var thing = thingBuilder.build();
        assertThat(thing.predicateIRIs()).isEmpty();
    }

    @Test
    @Ignore // No need (anymore), we're (now) preventing this using EP's @ImmutableTypeParameter
    public void setEmptyCollectionIsIgnored() {
        thingBuilder.iri(THING_IRI);
        // CANNOT: thingBuilder.set(PREDICATE_IRI, Set.of());
        // CANNOT: thingBuilder.addAll(PREDICATE_IRI, Set.of());
        var thing = thingBuilder.build();
        assertThat(thing.predicateIRIs()).isEmpty();
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void add() {
        thingBuilder.iri(THING_IRI);
        thingBuilder.add(PREDICATE_IRI, "a");
        thingBuilder.add(PREDICATE_IRI, "b");
        var thing = thingBuilder.build();
        assertThat(thing.get(PREDICATE_IRI, Iterable.class)).containsExactly("a", "b");
        assertThat(thing.isIterable(PREDICATE_IRI)).isTrue();
        assertThat(thing.isOrdered(PREDICATE_IRI)).isFalse();
        assertThat(thing.get(PREDICATE_IRI, Iterable.class)).isInstanceOf(ImmutableSet.class);
    }

    @Test
    public void addBuildAdd() {
        thingBuilder.iri(THING_IRI);
        thingBuilder.add(PREDICATE_IRI, "a");
        var thing1 = thingBuilder.build();
        var thingBuilder2 = thing1.copy();
        thingBuilder2.add(PREDICATE_IRI, "b");
        var thing2 = thingBuilder2.build();
        assertThat(thing2.get(PREDICATE_IRI, Iterable.class)).containsExactly("a", "b");
        assertThat(thing2.isIterable(PREDICATE_IRI)).isTrue();
        assertThat(thing2.isOrdered(PREDICATE_IRI)).isFalse();
        assertThat(thing2.get(PREDICATE_IRI, Iterable.class)).isInstanceOf(ImmutableSet.class);
    }

    @Test
    public void addAll() {
        thingBuilder.iri(THING_IRI);
        thingBuilder.addAll(PREDICATE_IRI, List.of("a", "b"));
        var thing = thingBuilder.build();
        assertThat(thing.get(PREDICATE_IRI, Iterable.class)).containsExactly("a", "b");
    }

    @Test
    public void addAllBuildAddAll() {
        thingBuilder.iri(THING_IRI);
        thingBuilder.addAll(PREDICATE_IRI, List.of("a", "b"));
        var thing1 = thingBuilder.build();
        var thingBuilder2 = thing1.copy();

        thingBuilder2.addAll(PREDICATE_IRI, List.of("c", "d"));
        var thing2 = thingBuilder2.build();
        assertThat(thing2.get(PREDICATE_IRI, Iterable.class)).containsExactly("a", "b", "c", "d");
    }

    @Test
    public void addAllEmpty() {
        thingBuilder.iri(THING_IRI);
        thingBuilder.addAll(PREDICATE_IRI, List.<String>of());
        var thing = thingBuilder.build();
        assertThat(thing.getOptional(PREDICATE_IRI, Iterable.class)).isEmpty();
    }

    @Test
    public void addAllWithDatatype() {
        thingBuilder.iri(THING_IRI);
        thingBuilder.addAll(
                PREDICATE_IRI, List.of("https://vorburger.ch"), KIRI.SCHEMA.URL_DATATYPE);
        var thing = thingBuilder.build();
        assertThat(thing.get(PREDICATE_IRI, Iterable.class))
                .containsExactly("https://vorburger.ch");
        assertThat(thing.datatype(PREDICATE_IRI)).isEqualTo(KIRI.SCHEMA.URL_DATATYPE);
    }

    @Test
    public void addToSingle() {
        thingBuilder.iri(THING_IRI);
        thingBuilder.set(PREDICATE_IRI, "a");
        thingBuilder.add(PREDICATE_IRI, "b");
        var thing = thingBuilder.build();
        assertThat(thing.get(PREDICATE_IRI, Iterable.class)).containsExactly("a", "b");
    }

    @Test
    public void addAllToSingle() {
        thingBuilder.iri(THING_IRI);
        thingBuilder.set(PREDICATE_IRI, "a");
        thingBuilder.addAll(PREDICATE_IRI, List.of("b", "c"));
        var thing = thingBuilder.build();
        assertThat(thing.get(PREDICATE_IRI, Iterable.class)).containsExactly("a", "b", "c");
    }

    @Test
    public void addOrderedToSingle() {
        thingBuilder.iri(THING_IRI);
        thingBuilder.set(PREDICATE_IRI, "x");
        thingBuilder.addOrdered(PREDICATE_IRI, "a");
        var thing = thingBuilder.build();
        assertThat(thing.get(PREDICATE_IRI, Iterable.class)).containsExactly("x", "a").inOrder();
    }

    @Test
    public void addThenAddOrdered() {
        thingBuilder.iri(THING_IRI);
        thingBuilder.add(PREDICATE_IRI, "x");
        thingBuilder.addOrdered(PREDICATE_IRI, "a");
        var thing = thingBuilder.build();
        assertThat(thing.get(PREDICATE_IRI, Iterable.class)).containsExactly("x", "a").inOrder();
    }

    @Test
    public void addToOrdered() {
        thingBuilder.iri(THING_IRI);
        thingBuilder.addOrdered(PREDICATE_IRI, "c");
        thingBuilder.addOrdered(PREDICATE_IRI, "b");
        thingBuilder.add(PREDICATE_IRI, "a");
        var thing = thingBuilder.build();
        assertThat(thing.get(PREDICATE_IRI, Iterable.class))
                .containsExactly("c", "b", "a")
                .inOrder();
    }

    @Test
    public void addOrderedAddAll() {
        thingBuilder.iri(THING_IRI);
        thingBuilder.addOrdered(PREDICATE_IRI, "b");
        thingBuilder.addOrdered(PREDICATE_IRI, "a");
        thingBuilder.addAll(PREDICATE_IRI, List.of("d", "c"));
        var thing = thingBuilder.build();
        assertThat(thing.get(PREDICATE_IRI, Iterable.class))
                .containsExactly("b", "a", "d", "c")
                .inOrder();
    }

    @Test
    public void addOrderedBuildAddOrdered() {
        thingBuilder.iri(THING_IRI);
        thingBuilder.addOrdered(PREDICATE_IRI, "a");
        thingBuilder.addOrdered(PREDICATE_IRI, "b");
        var thing1 = thingBuilder.build();

        var thingBuilder2 = thing1.copy();
        thingBuilder2.addOrdered(PREDICATE_IRI, "c");
        var thing2 = thingBuilder2.build();
        assertThat(thing2.get(PREDICATE_IRI, Iterable.class))
                .containsExactly("a", "b", "c")
                .inOrder();
    }

    @Test
    public void addAllOrdered() {
        thingBuilder.iri(THING_IRI);
        thingBuilder.addAllOrdered(PREDICATE_IRI, List.of("b", "a"));
        var thing = thingBuilder.build();
        assertThat(thing.get(PREDICATE_IRI, Iterable.class)).containsExactly("b", "a").inOrder();
    }

    @Test
    public void addAllOrderedBuildAddAllOrdered() {
        thingBuilder.iri(THING_IRI);
        thingBuilder.addAllOrdered(PREDICATE_IRI, List.of("b", "a"));
        var thing1 = thingBuilder.build();
        var thingBuilder2 = thing1.copy();
        thingBuilder2.addAllOrdered(PREDICATE_IRI, List.of("d", "c"));
        var thing2 = thingBuilder2.build();
        assertThat(thing2.get(PREDICATE_IRI, Iterable.class))
                .containsExactly("b", "a", "d", "c")
                .inOrder();
    }

    @Test
    public void addAllOrderedEmpty() {
        thingBuilder.iri(THING_IRI);
        thingBuilder.addAllOrdered(PREDICATE_IRI, List.<String>of());
        var thing = thingBuilder.build();
        assertThat(thing.getOptional(PREDICATE_IRI, Iterable.class)).isEmpty();
    }

    @Test
    public void hashCodeEquals() throws ConversionException, IOException {
        var testThing1 = ThingRepositoriesTest.testThing(thingBuilder);

        var testThing2Builder = ImmutableThing.builder();
        new ThingConverterInto().convertInto(testThing1, testThing2Builder);
        var testThing2 = testThing2Builder.build();

        assertThat(testThing1).isEqualTo(testThing2);
        assertThat(testThing1.hashCode()).isEqualTo(testThing2.hashCode());
    }

    @Test
    public void testToString() {
        var testThing = ThingRepositoriesTest.testThing(thingBuilder);
        var testThingToString = testThing.toString();

        assertThat(testThingToString).doesNotContain("@");
        assertThat(testThingToString).contains("example.com");
        assertThat(testThingToString).contains("hello");
    }

    @Test
    public void getAllAsString() {
        var testThing = ThingRepositoriesTest.testThing(thingBuilder);
        for (var predicateIRI : testThing.predicateIRIs()) {
            if (predicateIRI.equals("http://example.com/list")) continue;
            assertThat(testThing.get(predicateIRI, String.class)).isNotEmpty();
        }
    }

    @Test
    public void message() {
        var testThing = ThingRepositoriesTest.testThing(thingBuilder);
        assertThat(testThing.getString("http://example.com/message")).isEqualTo("hello");
    }

    @Test
    public void link() {
        var testThing = ThingRepositoriesTest.testThing(thingBuilder);
        assertThat(testThing.getString("http://example.com/link")).isEqualTo("http://example.com");
    }

    @Test
    public void langString() {
        var testThing = ThingRepositoriesTest.testThing(thingBuilder);
        assertThat(testThing.getString("http://example.com/mls")).isEqualTo("Saluton");
    }

    @Test
    public void literal2() {
        var testThing = ThingRepositoriesTest.testThing(thingBuilder);
        assertThat(testThing.datatype("http://example.com/lit")).isEqualTo("test:type");
        assertThat(testThing.getString("http://example.com/lit")).isEqualTo("k&ç#'");
    }

    @Test
    public void fileTimeAsInstant() {
        var instant = Instant.now();
        var p = "https://enola.dev/files/Node/createdAt";
        var thing =
                ImmutableThing.builder()
                        .iri("http://example.org")
                        .set(p, FileTime.from(instant), "https://enola.dev/FileTime")
                        .build();
        try (var ctx = TLC.open()) {
            ctx.push(DatatypeRepository.class, Datatypes.DTR);
            var actual = thing.get(p, Instant.class);
            assertThat(actual).isEqualTo(instant);
        }
    }
}

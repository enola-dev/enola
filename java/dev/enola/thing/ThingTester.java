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
package dev.enola.thing;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.thing.impl.IImmutableThing;
import dev.enola.thing.java.TBF;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Set;

public abstract class ThingTester {

    // TODO Move some of the generic tests from ImmutableThingTest up here

    protected static final String THING_IRI = "https://example.org/thing";
    protected static final String PREDICATE_IRI = "https://example.org/predicate";

    protected abstract TBF getThingBuilderFactory();

    protected Thing.Builder<IImmutableThing> thingBuilder;

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
    @Ignore // inOrder() is *NOT* guaranteed to be deterministic [anymore]
    public void insertionInOrder() {
        thingBuilder.iri(THING_IRI);
        thingBuilder.set("b", "B");
        thingBuilder.set("a", "A");
        var thing = thingBuilder.build();
        assertThat(thing.predicateIRIs()).containsExactly("b", "a").inOrder();
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
    public void setEmptyCollectionIsIgnored() {
        thingBuilder.iri(THING_IRI);
        thingBuilder.set(PREDICATE_IRI, Set.of());
        var thing = thingBuilder.build();
        assertThat(thing.predicateIRIs()).isEmpty();
    }
}

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
package dev.enola.thing.java.test;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.thing.java.test.TestThing.NUMBER_URI;

import dev.enola.model.xsd.Datatypes;
import dev.enola.thing.KIRI;
import dev.enola.thing.java.test.gen.ImmutableTestThing;

import org.junit.Ignore;
import org.junit.Test;

import java.time.Instant;

public class TestThingTest {

    final ImmutableTestThing thing =
            ImmutableTestThing.create("https://enola.dev/test1", "hello, world", 43);
    final ImmutableTestThing thing2 =
            ImmutableTestThing.create("https://enola.dev/test1", "hello, world", 43);
    final ImmutableTestThing bthing = // Thing from Builder with static setter methods
            ImmutableTestThing.builder()
                    .iri("https://enola.dev/test1")
                    .label("hello, world")
                    .number(43)
                    .build();
    final ImmutableTestThing dthing = // Thing from Builder with "dynamic" set()
            ImmutableTestThing.builder()
                    .iri("https://enola.dev/test1")
                    .set(KIRI.RDFS.LABEL, "hello, world")
                    .set(NUMBER_URI, 43)
                    .build();

    final String EXTRA = "https://enola.dev/another-predicate";
    final Instant INSTANT = Instant.parse("2023-10-26T15:29:31.123456-05:00");
    final ImmutableTestThing
            ething = // Thing from Builder with an EXTRA predicate, which is not a static field
            ImmutableTestThing.builder()
                            .iri("https://enola.dev/test1")
                            .label("hello, world")
                            .number(43)
                            .set(EXTRA, INSTANT, Datatypes.DATE_TIME.iri())
                            .build();

    final ImmutableTestThing
            mthing = // Thing without one of the static (ImmutableTestThing.NUMBER_URI predicate)
                    // fields set!
                    ImmutableTestThing.builder()
                            .iri("https://enola.dev/test1")
                            .label("hello, world")
                            // NO .number(43)
                            .set(EXTRA, INSTANT, Datatypes.DATE_TIME.iri())
                            .build();

    @Test
    public void iri() {
        assertThat(thing.iri()).isEqualTo("https://enola.dev/test1");
        assertThat(bthing.iri()).isEqualTo("https://enola.dev/test1");
        assertThat(dthing.iri()).isEqualTo("https://enola.dev/test1");
        assertThat(ething.iri()).isEqualTo("https://enola.dev/test1");
        assertThat(mthing.iri()).isEqualTo("https://enola.dev/test1");
    }

    @Test
    public void static_getters() {
        assertThat(thing.label()).isEqualTo("hello, world");
        assertThat(bthing.label()).isEqualTo("hello, world");
        assertThat(dthing.label()).isEqualTo("hello, world");
        assertThat(mthing.label()).isEqualTo("hello, world");

        assertThat(thing.number()).isEqualTo(43);
        assertThat(bthing.number()).isEqualTo(43);
        assertThat(dthing.number()).isEqualTo(43);
        assertThat(mthing.number()).isNull();
    }

    @Test
    public void predicateIRIs() {
        assertThat(thing.predicateIRIs()).containsExactly(KIRI.RDFS.LABEL, NUMBER_URI);
        assertThat(bthing.predicateIRIs()).containsExactly(KIRI.RDFS.LABEL, NUMBER_URI);
        assertThat(dthing.predicateIRIs()).containsExactly(KIRI.RDFS.LABEL, NUMBER_URI);
        assertThat(ething.predicateIRIs()).containsExactly(KIRI.RDFS.LABEL, NUMBER_URI, EXTRA);
        assertThat(mthing.predicateIRIs()).containsExactly(KIRI.RDFS.LABEL, EXTRA);
    }

    @Test
    public void thing_getters() {
        assertThat((String) thing.get(KIRI.RDFS.LABEL)).isEqualTo("hello, world");
        assertThat((String) bthing.get(KIRI.RDFS.LABEL)).isEqualTo("hello, world");
        assertThat((String) dthing.get(KIRI.RDFS.LABEL)).isEqualTo("hello, world");
        assertThat((String) mthing.get(KIRI.RDFS.LABEL)).isEqualTo("hello, world");

        assertThat((Integer) thing.get(NUMBER_URI)).isEqualTo(43);
        assertThat((Integer) bthing.get(NUMBER_URI)).isEqualTo(43);
        assertThat((Integer) dthing.get(NUMBER_URI)).isEqualTo(43);
        assertThat((Integer) mthing.get(NUMBER_URI)).isNull();

        assertThat((Integer) thing.get("n/a")).isNull();
        assertThat((Integer) bthing.get("n/a")).isNull();
        assertThat((Integer) dthing.get("n/a")).isNull();
        assertThat((Integer) mthing.get("n/a")).isNull();

        assertThat((Instant) ething.get(EXTRA)).isEqualTo(INSTANT);
        assertThat((Instant) mthing.get(EXTRA)).isEqualTo(INSTANT);
    }

    @Test
    public void thing_getters_asclass() {
        assertThat(thing.get(KIRI.RDFS.LABEL, String.class)).isEqualTo("hello, world");
        assertThat(thing.get(NUMBER_URI, Integer.class)).isEqualTo(43);
        assertThat(ething.get(EXTRA, String.class)).isEqualTo(INSTANT.toString());

        assertThat(thing.get("n/a", String.class)).isNull();
        assertThat(mthing.get(NUMBER_URI, Integer.class)).isNull();
    }

    @Test
    public void properties() {
        assertThat(thing.properties())
                .containsExactly(KIRI.RDFS.LABEL, "hello, world", NUMBER_URI, 43);
        assertThat(bthing.properties())
                .containsExactly(KIRI.RDFS.LABEL, "hello, world", NUMBER_URI, 43);
        assertThat(dthing.properties())
                .containsExactly(KIRI.RDFS.LABEL, "hello, world", NUMBER_URI, 43);
        assertThat(ething.properties())
                .containsExactly(KIRI.RDFS.LABEL, "hello, world", NUMBER_URI, 43, EXTRA, INSTANT);
        assertThat(mthing.properties())
                .containsExactly(KIRI.RDFS.LABEL, "hello, world", EXTRA, INSTANT);
    }

    @Test
    public void datatypes() {
        assertThat(thing.datatypes())
                .containsExactly(
                        KIRI.RDFS.LABEL,
                        dev.enola.datatype.Datatypes.STRING.iri(),
                        NUMBER_URI,
                        dev.enola.model.xsd.Datatypes.INT.iri());

        assertThat(bthing.datatypes())
                .containsExactly(
                        KIRI.RDFS.LABEL,
                        dev.enola.datatype.Datatypes.STRING.iri(),
                        NUMBER_URI,
                        dev.enola.model.xsd.Datatypes.INT.iri());

        assertThat(dthing.datatypes())
                .containsExactly(
                        KIRI.RDFS.LABEL,
                        dev.enola.datatype.Datatypes.STRING.iri(),
                        NUMBER_URI,
                        dev.enola.model.xsd.Datatypes.INT.iri());

        assertThat(ething.datatypes())
                .containsExactly(
                        KIRI.RDFS.LABEL,
                        dev.enola.datatype.Datatypes.STRING.iri(),
                        NUMBER_URI,
                        dev.enola.model.xsd.Datatypes.INT.iri(),
                        EXTRA,
                        Datatypes.DATE_TIME.iri());

        assertThat(mthing.datatypes())
                .containsExactly(
                        KIRI.RDFS.LABEL,
                        dev.enola.datatype.Datatypes.STRING.iri(),
                        EXTRA,
                        Datatypes.DATE_TIME.iri());
    }

    @Test
    public void datatype() {
        assertThat(thing.datatype(KIRI.RDFS.LABEL))
                .isEqualTo(dev.enola.datatype.Datatypes.STRING.iri());
        assertThat(bthing.datatype(KIRI.RDFS.LABEL))
                .isEqualTo(dev.enola.datatype.Datatypes.STRING.iri());
        assertThat(dthing.datatype(KIRI.RDFS.LABEL))
                .isEqualTo(dev.enola.datatype.Datatypes.STRING.iri());

        assertThat(thing.datatype(NUMBER_URI)).isEqualTo(dev.enola.model.xsd.Datatypes.INT.iri());
        assertThat(bthing.datatype(NUMBER_URI)).isEqualTo(dev.enola.model.xsd.Datatypes.INT.iri());
        assertThat(dthing.datatype(NUMBER_URI)).isEqualTo(dev.enola.model.xsd.Datatypes.INT.iri());

        assertThat(ething.datatype(EXTRA)).isEqualTo(Datatypes.DATE_TIME.iri());
    }

    @Test
    public void equals() {
        assertThat(thing).isEqualTo(thing2);
        assertThat(thing).isEqualTo(bthing);
        assertThat(thing).isEqualTo(dthing);
        assertThat(bthing).isEqualTo(dthing);
    }

    @Test
    @Ignore // TODO
    public void copy() {
        // TODO assertThat(thing.copy().build()).isEqualTo(thing);
        // TODO assertThat(ething.copy().build()).isEqualTo(ething);
        // TODO assertThat(mthing.copy().build()).isEqualTo(mthing);
    }
}

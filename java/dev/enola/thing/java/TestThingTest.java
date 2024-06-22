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
package dev.enola.thing.java;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.model.xsd.Datatypes;
import dev.enola.thing.KIRI;

import org.junit.Ignore;
import org.junit.Test;

import java.time.Instant;

public class TestThingTest {

    final TestThing thing = TestThing.create("https://enola.dev/test1", "hello, world", 43);
    final TestThing thing2 = TestThing.create("https://enola.dev/test1", "hello, world", 43);
    final TestThing bthing =
            TestThing.builder()
                    .iri("https://enola.dev/test1")
                    .label("hello, world")
                    .number(43)
                    .build();
    final TestThing dthing =
            TestThing.builder()
                    .iri("https://enola.dev/test1")
                    .set(KIRI.RDFS.LABEL, "hello, world")
                    .set(TestThing.NUMBER_URI, 43)
                    .build();

    final String EXTRA = "https://enola.dev/another-predicate";
    final Instant INSTANT = Instant.parse("2023-10-26T15:29:31.123456-05:00");
    final TestThing ething =
            TestThing.builder()
                    .iri("https://enola.dev/test1")
                    .label("hello, world")
                    .number(43)
                    .set(EXTRA, INSTANT, Datatypes.DATE_TIME.iri())
                    .build();

    @Test
    public void iri() {
        assertThat(thing.iri()).isEqualTo("https://enola.dev/test1");
        assertThat(bthing.iri()).isEqualTo("https://enola.dev/test1");
        assertThat(dthing.iri()).isEqualTo("https://enola.dev/test1");
        assertThat(ething.iri()).isEqualTo("https://enola.dev/test1");
    }

    @Test
    public void static_getters() {
        assertThat(thing.label()).isEqualTo("hello, world");
        assertThat(bthing.label()).isEqualTo("hello, world");
        assertThat(dthing.label()).isEqualTo("hello, world");

        assertThat(thing.number()).isEqualTo(43);
        assertThat(bthing.number()).isEqualTo(43);
        assertThat(dthing.number()).isEqualTo(43);
    }

    @Test
    public void predicateIRIs() {
        assertThat(thing.predicateIRIs()).containsExactly(KIRI.RDFS.LABEL, TestThing.NUMBER_URI);
        assertThat(bthing.predicateIRIs()).containsExactly(KIRI.RDFS.LABEL, TestThing.NUMBER_URI);
        assertThat(dthing.predicateIRIs()).containsExactly(KIRI.RDFS.LABEL, TestThing.NUMBER_URI);
        assertThat(ething.predicateIRIs())
                .containsExactly(KIRI.RDFS.LABEL, TestThing.NUMBER_URI, EXTRA);
    }

    @Test
    public void thing_getters() {
        assertThat((String) thing.get(KIRI.RDFS.LABEL)).isEqualTo("hello, world");
        assertThat((String) bthing.get(KIRI.RDFS.LABEL)).isEqualTo("hello, world");
        assertThat((String) dthing.get(KIRI.RDFS.LABEL)).isEqualTo("hello, world");

        assertThat((Integer) thing.get(TestThing.NUMBER_URI)).isEqualTo(43);
        assertThat((Integer) bthing.get(TestThing.NUMBER_URI)).isEqualTo(43);
        assertThat((Integer) dthing.get(TestThing.NUMBER_URI)).isEqualTo(43);

        assertThat((Integer) thing.get("n/a")).isNull();
        assertThat((Integer) bthing.get("n/a")).isNull();
        assertThat((Integer) dthing.get("n/a")).isNull();

        assertThat((Instant) ething.get(EXTRA)).isEqualTo(INSTANT);
    }

    @Test
    public void thing_getters_asclass() {
        assertThat(thing.get(KIRI.RDFS.LABEL, String.class)).isEqualTo("hello, world");
        assertThat(thing.get(TestThing.NUMBER_URI, Integer.class)).isEqualTo(43);
        assertThat(ething.get(EXTRA, String.class)).isEqualTo(INSTANT.toString());

        assertThat(thing.get("n/a", String.class)).isNull();
    }

    @Test
    public void properties() {
        assertThat(thing.properties())
                .containsExactly(KIRI.RDFS.LABEL, "hello, world", TestThing.NUMBER_URI, 43);
        assertThat(bthing.properties())
                .containsExactly(KIRI.RDFS.LABEL, "hello, world", TestThing.NUMBER_URI, 43);
        assertThat(dthing.properties())
                .containsExactly(KIRI.RDFS.LABEL, "hello, world", TestThing.NUMBER_URI, 43);
        assertThat(ething.properties())
                .containsExactly(
                        KIRI.RDFS.LABEL, "hello, world", TestThing.NUMBER_URI, 43, EXTRA, INSTANT);
    }

    @Test
    public void datatypes() {
        assertThat(thing.datatypes())
                .containsExactly(
                        KIRI.RDFS.LABEL,
                        dev.enola.datatype.Datatypes.STRING.iri(),
                        TestThing.NUMBER_URI,
                        dev.enola.model.xsd.Datatypes.INT.iri());

        assertThat(bthing.datatypes())
                .containsExactly(
                        KIRI.RDFS.LABEL,
                        dev.enola.datatype.Datatypes.STRING.iri(),
                        TestThing.NUMBER_URI,
                        dev.enola.model.xsd.Datatypes.INT.iri());

        assertThat(dthing.datatypes())
                .containsExactly(
                        KIRI.RDFS.LABEL,
                        dev.enola.datatype.Datatypes.STRING.iri(),
                        TestThing.NUMBER_URI,
                        dev.enola.model.xsd.Datatypes.INT.iri());

        assertThat(ething.datatypes())
                .containsExactly(
                        KIRI.RDFS.LABEL,
                        dev.enola.datatype.Datatypes.STRING.iri(),
                        TestThing.NUMBER_URI,
                        dev.enola.model.xsd.Datatypes.INT.iri(),
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

        assertThat(thing.datatype(TestThing.NUMBER_URI))
                .isEqualTo(dev.enola.model.xsd.Datatypes.INT.iri());
        assertThat(bthing.datatype(TestThing.NUMBER_URI))
                .isEqualTo(dev.enola.model.xsd.Datatypes.INT.iri());
        assertThat(dthing.datatype(TestThing.NUMBER_URI))
                .isEqualTo(dev.enola.model.xsd.Datatypes.INT.iri());

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
    }

    // TODO missing number - should not be in properties!
}

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
package dev.enola.thing.impl;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.model.Datatypes;
import dev.enola.thing.ThingConverterInto;
import dev.enola.thing.repo.ThingRepositoriesTest;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.time.Instant;

public class ImmutableThingTest {

    @Test
    public void hashCodeEquals() throws ConversionException, IOException {
        var testThing1 = ThingRepositoriesTest.TEST_THING;

        var testThing2Builder = ImmutableThing.builder();
        new ThingConverterInto().convertInto(testThing1, testThing2Builder);
        var testThing2 = testThing2Builder.build();

        assertThat(testThing1).isEqualTo(testThing2);
        assertThat(testThing1.hashCode()).isEqualTo(testThing2.hashCode());
    }

    @Test
    public void testToString() {
        var testThing = ThingRepositoriesTest.TEST_THING;
        var testThingToString = testThing.toString();

        assertThat(testThingToString).doesNotContain("@");
        assertThat(testThingToString).contains("example.com");
        assertThat(testThingToString).contains("hello");
    }

    @Test
    public void getAllAsString() {
        var testThing = ThingRepositoriesTest.TEST_THING;
        for (var predicateIRI : testThing.predicateIRIs()) {
            assertThat(testThing.get(predicateIRI, String.class)).isNotEmpty();
        }
    }

    @Test
    public void message() {
        var testThing = ThingRepositoriesTest.TEST_THING;
        assertThat(testThing.getString("http://example.com/message")).isEqualTo("hello");
    }

    @Test
    public void link() {
        var testThing = ThingRepositoriesTest.TEST_THING;
        assertThat(testThing.getString("http://example.com/link")).isEqualTo("http://example.com");
    }

    @Test
    public void langString() {
        var testThing = ThingRepositoriesTest.TEST_THING;
        assertThat(testThing.getString("http://example.com/mls")).isEqualTo("Saluton");
    }

    @Test
    public void literal() {
        var testThing = ThingRepositoriesTest.TEST_THING;
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

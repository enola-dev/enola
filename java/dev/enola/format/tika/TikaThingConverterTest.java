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
package dev.enola.format.tika;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.EmptyResource;
import dev.enola.thing.Thing;
import dev.enola.thing.repo.ThingsBuilder;
import dev.enola.thing.testlib.ThingsSubject;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class TikaThingConverterTest {

    @Test
    public void empty() throws IOException {
        var tb = new ThingsBuilder();
        var c = new TikaThingConverter(new EmptyResource.Provider());

        var r = c.convertInto(EmptyResource.EMPTY_URI, tb);
        assertThat(tb.builders()).isEmpty();
        assertThat(r).isFalse();
    }

    @Test
    public void html() throws IOException {
        check("test.html");
    }

    @Test
    public void png() throws IOException {
        check("test.png");
    }

    // TODO @Test public void jpeg() throws IOException {

    // TODO @Test public void tiff() throws IOException {

    // TODO @Test public void epubEBook() throws IOException {

    private void check(String classpath) throws IOException {
        var name = "classpath:/" + classpath;

        var tb = new ThingsBuilder();
        var c = new TikaThingConverter(new ClasspathResource.Provider());

        var r = c.convertInto(URI.create(name), tb);
        assertThat(r).isTrue();

        assertThat(tb.builders()).hasSize(1);
        var thing = tb.builders().iterator().next().build();
        checkThatAllPredicatesAreAbsoluteURIs(thing);

        ThingsSubject.assertThat(tb).isEqualTo(name + ".ttl");
    }

    private void checkThatAllPredicatesAreAbsoluteURIs(Thing thing) {
        for (var predicateIRI : thing.predicateIRIs()) {
            if (!URI.create(predicateIRI).isAbsolute())
                throw new IllegalArgumentException(predicateIRI);
        }
    }
}

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
package dev.enola.format.tika;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import dev.enola.common.context.testlib.EnolaTestTLCRules;
import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.EmptyResource;
import dev.enola.rdf.io.RdfMediaTypes;
import dev.enola.thing.Thing;
import dev.enola.thing.repo.ThingMemoryRepositoryROBuilder;
import dev.enola.thing.testlib.ThingsSubject;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.io.IOException;
import java.net.URI;

public class TikaThingConverterTest {

    @Rule public final SingletonRule r = $(MediaTypeProviders.set(new RdfMediaTypes()));

    @Rule public final TestRule tlcRule = EnolaTestTLCRules.TBF;

    @Test
    public void empty() throws IOException {
        var store = new ThingMemoryRepositoryROBuilder();
        var c = new TikaThingConverter(new EmptyResource.Provider());

        var r = c.convertInto(EmptyResource.EMPTY_URI, store);
        assertThat(store.listIRI()).isEmpty();
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

        var store = new ThingMemoryRepositoryROBuilder();
        var c = new TikaThingConverter(new ClasspathResource.Provider());

        var r = c.convertInto(URI.create(name), store);
        assertThat(r).isTrue();

        assertThat(store.listIRI()).hasSize(1);
        var thing = store.list().iterator().next();
        checkThatAllPredicatesAreAbsoluteURIs(thing);

        ThingsSubject.assertThat(store).isEqualTo(name + ".ttl");
    }

    private void checkThatAllPredicatesAreAbsoluteURIs(Thing thing) {
        for (var predicateIRI : thing.predicateIRIs()) {
            if (!URI.create(predicateIRI).isAbsolute())
                throw new IllegalArgumentException(predicateIRI);
        }
    }
}

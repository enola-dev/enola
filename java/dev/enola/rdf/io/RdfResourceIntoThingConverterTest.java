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
package dev.enola.rdf.io;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import dev.enola.common.context.TLC;
import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.*;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.datatype.DatatypeRepositoryBuilder;
import dev.enola.thing.Thing;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.ProxyTBF;
import dev.enola.thing.java.TBF;
import dev.enola.thing.java.test.TestSomething;
import dev.enola.thing.repo.ThingMemoryRepositoryROBuilder;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class RdfResourceIntoThingConverterTest {

    public @Rule SingletonRule r = $(MediaTypeProviders.set(new RdfMediaTypes()));

    DatatypeRepository datatypeRepository = new DatatypeRepositoryBuilder().build();
    ResourceProvider resourceProvider =
            new ResourceProviders(new ClasspathResource.Provider(), new FileResource.Provider());
    RdfResourceIntoThingConverter<Thing> c =
            new RdfResourceIntoThingConverter<>(resourceProvider, datatypeRepository);

    @Test
    public void picasso() throws IOException {
        var thing = convert(new ClasspathResource("picasso.ttl").uri()).iterator().next();
        assertThat(thing.iri()).isEqualTo("http://example.enola.dev/Dalí");
    }

    @Test
    public void emptyYAML() throws IOException {
        assertThat(convert(new ClasspathResource("empty.yaml").uri())).isEmpty();
    }

    @Test
    public void directory() throws IOException {
        assertThat(convert(URI.create("file:/tmp/"))).isEmpty();
    }

    @Test // Load testSomething.ttl and ensure it's an instance of TestSomething and not just Thing
    public void testSomething() throws IOException {
        var things = convert(new ClasspathResource("testSomething.ttl").uri());
        Thing thing = things.iterator().next();
        TestSomething testSomething = (TestSomething) thing;
        assertThat(testSomething.test()).isEqualTo("hello, world");
    }

    Iterable<Thing> convert(URI uri) throws IOException {
        // TODO Switch to using RdfLoader...
        try (var ctx = TLC.open().push(TBF.class, new ProxyTBF(ImmutableThing.FACTORY))) {
            var store = new ThingMemoryRepositoryROBuilder();
            var ignored = c.convertInto(uri, store);
            return store.list();
        }
    }
}

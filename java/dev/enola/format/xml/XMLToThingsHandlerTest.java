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
package dev.enola.format.xml;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import dev.enola.common.context.TLC;
import dev.enola.common.context.testlib.EnolaTestTLCRules;
import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.EmptyResource;
import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.common.xml.XmlMediaType;
import dev.enola.rdf.io.RdfMediaTypes;
import dev.enola.thing.repo.ThingMemoryRepositoryROBuilder;
import dev.enola.thing.repo.ThingRepositoryStore;
import dev.enola.thing.testlib.ThingsSubject;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.io.IOException;
import java.net.URI;

public class XMLToThingsHandlerTest {

    @Rule
    public final SingletonRule r =
            $(
                    MediaTypeProviders.set(
                            new MediaTypeProviders(new RdfMediaTypes(), new XmlMediaType())));

    @Rule public final TestRule tlcRule = EnolaTestTLCRules.TBF;

    ThingRepositoryStore store = new ThingMemoryRepositoryROBuilder();
    XmlThingConverter loader =
            new XmlThingConverter(
                    new ResourceProviders(
                            new ClasspathResource.Provider(), new EmptyResource.Provider()));

    @Test
    public void nonXML() throws IOException {
        assertThat(loader.convertInto(EmptyResource.EMPTY_URI, store)).isFalse();
        ThingsSubject.assertThat(store).hasOnlyEmptyThings();
    }

    @Test
    public void emptyXML() throws IOException {
        var emptyXmlURI = URI.create(EmptyResource.EMPTY_URI + "mediaType=text/xml");
        assertThat(loader.convertInto(emptyXmlURI, store)).isTrue();
        ThingsSubject.assertThat(store).hasOnlyEmptyThings();
    }

    @Test
    public void rootOnly() throws IOException {
        assertThat(loader.convertInto(URI.create("classpath:/root-only.xml"), store)).isTrue();
        ThingsSubject.assertThat(store).hasOnlyEmptyThings();
    }

    @Test
    public void greeting1attributeWithXmlNS() throws IOException {
        var from = URI.create("classpath:/greeting1-attribute-with-xmlns.xml");
        try (var ctx = TLC.open().push(XmlThingContext.ID, "classpath:/greeting1.xml")) {
            assertThat(loader.convertInto(from, store)).isTrue();
        }
        ThingsSubject.assertThat(store).isEqualTo("classpath:/greeting1.xml.ttl");
    }

    @Test
    public void greeting1attribute() throws IOException {
        var from = URI.create("classpath:/greeting1-attribute.xml");
        try (var ctx =
                TLC.open()
                        .push(XmlThingContext.ID, "classpath:/greeting1.xml")
                        .push(XmlThingContext.NS, "https://example.org")) {
            assertThat(loader.convertInto(from, store)).isTrue();
        }
        ThingsSubject.assertThat(store).isEqualTo("classpath:/greeting1.xml.ttl");
    }

    @Test
    public void greeting1nested() throws IOException {
        try (var ctx = TLC.open().push(XmlThingContext.ID, "classpath:/greeting1.xml")) {
            var from = URI.create("classpath:/greeting1-nested.xml");
            assertThat(loader.convertInto(from, store)).isTrue();
        }
        ThingsSubject.assertThat(store).isEqualTo("classpath:/greeting1.xml.ttl");
    }

    @Test
    public void greeting1nesteds() throws IOException {
        try (var ctx = TLC.open().push(XmlThingContext.NS, "https://example.org")) {
            var from = URI.create("classpath:/greeting1-nesteds.xml");
            assertThat(loader.convertInto(from, store)).isTrue();
        }
        ThingsSubject.assertThat(store).isEqualTo("classpath:/greeting1-nesteds.xml.ttl");
    }

    @Test
    @Ignore // TODO FIXME
    public void xhtml() throws IOException {
        assertThat(loader.convertInto(URI.create("classpath:/test.html.xml"), store)).isTrue();
        throw new IllegalStateException(store.toString());
        // TODO ThingsSubject.assertThat(thingsBuilder).isEqualTo("classpath:/test.html.xml.ttl");
    }
}

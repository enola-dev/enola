/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.infer.rdf;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import com.google.common.collect.ImmutableList;

import dev.enola.common.context.TLC;
import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.model.w3.rdf.Property;
import dev.enola.rdf.io.RdfMediaTypes;
import dev.enola.rdf.io.RdfResourceIntoThingConverter;
import dev.enola.thing.KIRI;
import dev.enola.thing.Link;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.io.Loader;
import dev.enola.thing.io.UriIntoThingConverters;
import dev.enola.thing.java.ProxyTBF;
import dev.enola.thing.java.TBF;
import dev.enola.thing.java.TypeToBuilder;
import dev.enola.thing.repo.AlwaysThingRepositoryStore;
import dev.enola.thing.repo.ThingMemoryRepositoryRW;
import dev.enola.thing.repo.ThingProvider;
import dev.enola.thing.repo.ThingRepositoryStore;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

public class RDFSTriggersIntegrationTest {

    @Rule public SingletonRule r = $(MediaTypeProviders.set(new RdfMediaTypes()));

    @Test
    public void rdfs() throws IOException {
        var trigger = new RDFSPropertyTrigger();
        ThingRepositoryStore repo = new ThingMemoryRepositoryRW(ImmutableList.of(trigger));
        repo = new AlwaysThingRepositoryStore(repo);
        trigger.setRepo(repo);

        var tbf = new ProxyTBF(ImmutableThing.FACTORY);
        try (var ctx = TLC.open().push(TBF.class, tbf).push(ThingProvider.class, repo)) {

            var rp = new ClasspathResource.Provider();
            var rdfConverter = new RdfResourceIntoThingConverter<>(rp, DatatypeRepository.EMPTY);
            var converters = new UriIntoThingConverters(rdfConverter);
            var loader = new Loader(converters);
            loader.load("classpath:/www.w3.org/rdf.ttl", repo);
            loader.load("classpath:/www.w3.org/rdf-schema.ttl", repo);

            assertThat(repo.listIRI())
                    .contains("http://www.w3.org/1999/02/22-rdf-syntax-ns#Property");
            var propertyThing = repo.get("http://www.w3.org/1999/02/22-rdf-syntax-ns#Property");
            assertThat(propertyThing.predicateIRIs()).contains("https://enola.dev/properties");
            var properties = propertyThing.get("https://enola.dev/properties", Iterable.class);
            assertThat(properties)
                    .containsExactly(
                            new Link("http://www.w3.org/2000/01/rdf-schema#subPropertyOf"),
                            new Link("http://www.w3.org/2000/01/rdf-schema#domain"),
                            new Link("http://www.w3.org/2000/01/rdf-schema#range"));
        }
    }

    @Test
    public void testSomethingBuilder() {
        var pair = TypeToBuilder.typeToBuilder(KIRI.RDF.PROPERTY);
        assertThat(pair.builderClass()).isEqualTo(Property.Builder.class);
        assertThat(pair.thingClass()).isEqualTo(Property.class);
    }
}

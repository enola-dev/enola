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
package dev.enola.thing.template;

import static com.google.common.truth.Truth.*;

import com.google.common.collect.ImmutableMap;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.datatype.DatatypeRepositoryBuilder;
import dev.enola.rdf.RdfResourceIntoThingConverter;
import dev.enola.thing.KIRI;
import dev.enola.thing.Link;
import dev.enola.thing.ThingMemoryRepositoryRW;
import dev.enola.thing.io.Loader;
import dev.enola.thing.io.ResourceIntoThingConverter;

import org.junit.Test;

import java.util.stream.Stream;

/**
 * {@link TemplateThingRepository} Test.
 *
 * <p>This only tests template functionality; as non-regression testing of the wrapping delegation
 * is covered in the {@link dev.enola.thing.ThingRepositoriesTest}.
 */
public class TemplateThingRepositoryTest {

    @Test
    public void greetingN() {
        DatatypeRepository dtr = new DatatypeRepositoryBuilder().build();
        ResourceIntoThingConverter ritc = new RdfResourceIntoThingConverter(dtr);
        var loader = new Loader(ritc);

        var store = new ThingMemoryRepositoryRW();
        var greetingN = new ClasspathResource("example.org/greetingN.ttl");
        loader.convertIntoOrThrow(Stream.of(greetingN), store);
        var repo = new TemplateThingRepository(store);

        var classIRI = "https://example.org/greeting";
        var templateIRI = "https://example.org/greeting{NUMBER}";
        var exampleIRI = "https://example.org/greeting42";
        var yoPropertyIRI = "https://example.org/yo";

        assertThat(repo.listIRI()).containsExactly(classIRI, templateIRI);

        // NB: We do not test/cover classIRI here - that's "as usual" (and tested elsewhere)

        var template = repo.get(templateIRI);
        assertThat(template.iri()).isEqualTo(templateIRI);
        assertThat(template.properties())
                .containsExactlyEntriesIn(
                        ImmutableMap.of(
                                KIRI.RDF.TYPE,
                                new Link(classIRI),
                                yoPropertyIRI,
                                new Link("http://example.org/hi/{NUMBER}")));

        var example = repo.get(exampleIRI);
        assertThat(example.iri()).isEqualTo(exampleIRI);
        assertThat(example.properties())
                .containsExactlyEntriesIn(
                        ImmutableMap.of(
                                KIRI.RDF.TYPE,
                                new Link(classIRI),
                                yoPropertyIRI,
                                new Link("http://example.org/hi/42")));
    }
}

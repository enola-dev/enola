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
package dev.enola.thing.template;

import static com.google.common.truth.Truth.*;

import static dev.enola.common.context.testlib.SingletonRule.$;

import com.google.common.collect.ImmutableMap;

import dev.enola.common.context.testlib.EnolaTestTLCRules;
import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.datatype.DatatypeRepositoryBuilder;
import dev.enola.rdf.io.RdfMediaTypes;
import dev.enola.rdf.io.RdfResourceIntoThingConverter;
import dev.enola.thing.KIRI;
import dev.enola.thing.Link;
import dev.enola.thing.io.Loader;
import dev.enola.thing.io.UriIntoThingConverters;
import dev.enola.thing.repo.ThingMemoryRepositoryRW;
import dev.enola.thing.repo.ThingRepositoriesTest;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.stream.Stream;

/**
 * {@link TemplateThingRepository} Test.
 *
 * <p>This only tests template functionality; as non-regression testing of the wrapping delegation
 * is covered in the {@link ThingRepositoriesTest}.
 */
public class TemplateThingRepositoryTest {

    public @Rule SingletonRule r = $(MediaTypeProviders.set(new RdfMediaTypes()));

    @Rule public final TestRule tlcRule = EnolaTestTLCRules.TBF;

    @Test
    public void greetingN() {
        var rp = new ClasspathResource.Provider();
        var dtr = new DatatypeRepositoryBuilder().build();
        var ritc = new UriIntoThingConverters(new RdfResourceIntoThingConverter<>(rp, dtr));
        var loader = new Loader(ritc);

        var classIRI = "https://example.org/greeting";
        var templateIRI = "https://example.org/greet/{NUMBER}";
        var exampleIRI = "https://example.org/greet/42";
        var yoPropertyIRI = "https://example.org/yo";

        var store = new ThingMemoryRepositoryRW();
        var greetingN = new ClasspathResource("example.org/greetingN.ttl");
        loader.convertIntoOrThrow(Stream.of(greetingN.uri()), store);
        assertThat(store.listIRI()).containsExactly(classIRI);
        assertThat(store.get(classIRI).datatype(yoPropertyIRI))
                .isEqualTo(KIRI.E.IRI_TEMPLATE_DATATYPE);

        var repo = new TemplateThingRepository(store);
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

        // TemplateService testing
        assertThat(repo.breakdown(yoPropertyIRI)).isEmpty();
        assertThat(repo.breakdown(classIRI)).isEmpty();
        Assert.assertThrows(IllegalArgumentException.class, () -> repo.breakdown(templateIRI));

        var breakdown = repo.breakdown(exampleIRI).get();
        assertThat(breakdown.variables()).containsExactly("NUMBER", "42");
        assertThat(breakdown.iriTemplate()).isEqualTo(templateIRI);
    }
}

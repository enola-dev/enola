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
package dev.enola.thing.testlib;

import static dev.enola.common.context.testlib.SingletonRule.$;

import dev.enola.common.context.testlib.EnolaTestTLCRules;
import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.rdf.io.RdfMediaTypes;
import dev.enola.thing.KIRI;
import dev.enola.thing.Link;
import dev.enola.thing.repo.EmptyThingsRepository;
import dev.enola.thing.repo.ThingMemoryRepositoryRW;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.io.IOException;

public class ThingsSubjectTest {

    @Rule
    public final SingletonRule r =
            $(MediaTypeProviders.set(new RdfMediaTypes(), new YamlMediaType()));

    @Rule public final TestRule tlcRule = EnolaTestTLCRules.TBF;

    @Test
    public void empty() throws IOException {
        var r = new EmptyThingsRepository();
        ThingsSubject.assertThat(r).isEqualTo("classpath:/empty.yaml");
    }

    @Test
    public void greeting1ttl() throws IOException {
        var repo = new ThingMemoryRepositoryRW();
        var builder =
                repo.getBuilder("https://example.org/greeting1")
                        .set("https://example.org/message", "hello, world");
        repo.store(builder.build());
        ThingsSubject.assertThat(repo).isEqualTo("classpath:/example.org/greeting1.ttl");
    }

    @Test
    public void greetingNttl() throws IOException {
        var repo = new ThingMemoryRepositoryRW();
        var builder =
                repo.getBuilder("https://example.org/greeting")
                        .set(KIRI.E.IRI_TEMPLATE_PROPERTY, "https://example.org/greet/{NUMBER}")
                        .set("https://enola.dev/example", new Link("https://example.org/greet/42"))
                        .set(
                                "https://example.org/yo",
                                "http://example.org/hi/{NUMBER}",
                                KIRI.E.IRI_TEMPLATE_DATATYPE)
                        .set(KIRI.RDF.TYPE, new Link(KIRI.RDFS.CLASS));
        repo.store(builder.build());
        ThingsSubject.assertThat(repo).isEqualTo("classpath:/example.org/greetingN.ttl");
    }
}

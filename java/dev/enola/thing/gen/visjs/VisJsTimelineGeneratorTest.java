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
package dev.enola.thing.gen.visjs;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;
import static dev.enola.common.io.testlib.ResourceSubject.assertThat;

import com.google.common.net.MediaType;

import dev.enola.common.context.TLC;
import dev.enola.common.context.testlib.EnolaTestTLCRules;
import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.context.testlib.TestTLCRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.MemoryResource;
import dev.enola.rdf.io.RdfLoader;
import dev.enola.rdf.io.RdfMediaTypes;
import dev.enola.thing.gen.LinkTransformer;
import dev.enola.thing.metadata.ThingMetadataProvider;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class VisJsTimelineGeneratorTest {

    @Rule public SingletonRule r = $(MediaTypeProviders.set(new RdfMediaTypes()));

    @Rule public TestTLCRule rlcRule = EnolaTestTLCRules.BASIC;

    // TODO @Test public void groupsAndItems() {}

    @Test
    public void html() throws IOException {
        var things = new RdfLoader().loadAtLeastOneThing(URI.create("classpath:/graph.ttl"));
        assertThat(things).isNotEmpty();
        var actual = new MemoryResource(MediaType.HTML_UTF_8);

        LinkTransformer linkTransformer = iri -> iri;
        var gen = new VisJsTimelineGenerator(TLC.get(ThingMetadataProvider.class), linkTransformer);
        gen.convertIntoOrThrow(things, actual);

        var expected = new ClasspathResource("dev/enola/thing/gen/visjs/timeline.expected.html");
        assertThat(actual).hasCharsEqualTo(expected);
    }
}

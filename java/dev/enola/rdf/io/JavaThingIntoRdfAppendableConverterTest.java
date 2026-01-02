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
package dev.enola.rdf.io;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.context.testlib.TestTLCRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.TBF;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class JavaThingIntoRdfAppendableConverterTest {

    public @Rule SingletonRule sr = $(MediaTypeProviders.set(new RdfMediaTypes()));
    public @Rule TestTLCRule tr = TestTLCRule.of(TBF.class, ImmutableThing.FACTORY);

    @Test
    public void turtleTTL() throws IOException {
        var uri = URI.create("classpath:/picasso.ttl");
        var loader = new RdfLoader(new ClasspathResource.Provider(), DatatypeRepository.EMPTY);
        var thing1 = loader.loadAtLeastOneThing(uri).iterator().next();
        assertThat(thing1.iri()).isEqualTo("http://example.enola.dev/Dalí");

        var stringBuilder = new StringBuilder();
        var converter = new JavaThingIntoRdfAppendableConverter(RdfMediaTypes.TURTLE);
        assertThat(converter.convertInto(thing1, stringBuilder)).isTrue();
        var turtleTTL = stringBuilder.toString();
        assertThat(turtleTTL).startsWith("@prefix schema: <https://schema.org/> .");
        assertThat(turtleTTL).endsWith("schema:birthDate \"1904-05-11\"^^schema:Date .\n");
        assertThat(turtleTTL).doesNotContain("http://ns.adobe.com");
        // TODO assertThat(turtleTTL).isEqualTo("...");
    }
}

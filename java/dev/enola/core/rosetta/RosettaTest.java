/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.core.rosetta;

import static com.google.common.net.MediaType.JSON_UTF_8;
import static com.google.common.net.MediaType.PLAIN_TEXT_UTF_8;
import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;
import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;
import static dev.enola.common.io.testlib.ResourceSubject.assertThat;
import static dev.enola.thing.gen.graphviz.GraphvizMediaType.GV;
import static dev.enola.thing.gen.graphviz.GraphvizResourceConverter.OUT_URI_QUERY_PARAMETER_FULL;

import dev.enola.common.context.TLC;
import dev.enola.common.context.testlib.EnolaTestTLCRules;
import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.common.io.resource.*;
import dev.enola.common.xml.XmlMediaType;
import dev.enola.common.yamljson.JSON;
import dev.enola.common.yamljson.YAML;
import dev.enola.data.iri.NamespaceConverter;
import dev.enola.data.iri.namespace.repo.NamespaceConverterWithRepository;
import dev.enola.data.iri.namespace.repo.NamespaceRepositoryEnolaDefaults;
import dev.enola.rdf.io.RdfLoader;
import dev.enola.rdf.io.RdfMediaTypes;
import dev.enola.thing.Thing;
import dev.enola.thing.gen.gexf.GexfMediaType;
import dev.enola.thing.gen.graphcommons.GraphCommonsMediaType;
import dev.enola.thing.gen.graphviz.GraphvizMediaType;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.io.ThingMediaTypes;
import dev.enola.thing.repo.ThingMemoryRepositoryROBuilder;
import dev.enola.thing.repo.ThingProvider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RosettaTest {

    // These intentionally only test some cases; more detailed tests
    // are done e.g. in YamlJsonTest and in ProtoIOTest.

    private static final ResourceProvider rp = new ClasspathResource.Provider();

    @Rule
    public SingletonRule r =
            $(
                    MediaTypeProviders.set(
                            new RdfMediaTypes(),
                            new GraphvizMediaType(),
                            new GraphCommonsMediaType(),
                            new GexfMediaType(),
                            new YamlMediaType(),
                            new XmlMediaType()));

    @Rule public final TestRule tlcRule = EnolaTestTLCRules.BASIC;

    private Rosetta rosetta;

    @Before
    public void before() {
        rosetta = new Rosetta(rp, new RdfLoader());
    }

    @Test
    public void testJsonToYaml() throws Exception {
        var in = StringResource.of("{\"value\":123}", JSON_UTF_8);
        var out = new MemoryResource(YAML_UTF_8);
        rosetta.convertInto(in, out);
        assertThat(out.charSource().read()).isEqualTo("{value: 123.0}\n");
    }

    @Test
    public void testYamlToJson() throws Exception {
        var in = StringResource.of("value: 123", YAML_UTF_8);
        var out = new MemoryResource(JSON_UTF_8);
        rosetta.convertInto(in, out);
        assertThat(out.charSource().read()).isEqualTo("{\"value\":123}");
    }

    @Test
    public void testChangeTextEncodingFromUtf8ToIso8859() throws Exception {
        var in = StringResource.of("hello, wörld", PLAIN_TEXT_UTF_8); // Note the umlaut!
        var out = new MemoryResource(PLAIN_TEXT_UTF_8.withCharset(StandardCharsets.ISO_8859_1));
        rosetta.convertInto(in, out);

        assertThat(in.charSource().read()).isEqualTo(out.charSource().read());
        assertThat(in.byteSource().read()).isNotEqualTo(out.byteSource().read());
        assertThat(in.byteSource().size()).isEqualTo(13);
        assertThat(out.byteSource().size()).isEqualTo(12);
    }

    @Test
    public void testChangeTextEncodingFromUtf8ToUtf16() throws Exception {
        var in = StringResource.of("hello, wörld", PLAIN_TEXT_UTF_8); // Note the umlaut!
        var out = new MemoryResource(PLAIN_TEXT_UTF_8.withCharset(StandardCharsets.UTF_16BE));
        rosetta.convertInto(in, out);

        assertThat(in.charSource().read()).isEqualTo(out.charSource().read());
        assertThat(in.byteSource().read()).isNotEqualTo(out.byteSource().read());
        assertThat(in.byteSource().size()).isEqualTo(13);
        assertThat(out.byteSource().size()).isEqualTo(24);
    }

    @Test
    public void testTurtleToThings() throws Exception {
        var in = new ClasspathResource("picasso.ttl");
        var out = new MemoryResource(ThingMediaTypes.THING_YAML_UTF_8);
        rosetta.convertInto(in, out);
        assertThat(YAML.read(out.charSource().read())).isNotEmpty();
        assertThat(out.byteSource().size()).isGreaterThan(800);
    }

    @Test
    public void testTurtleToJsonLd() throws Exception {
        var in = new ClasspathResource("picasso.ttl");
        var out = new MemoryResource(RdfMediaTypes.JSON_LD);
        rosetta.convertInto(in, out);

        assertThat(out.byteSource().size()).isGreaterThan(800);
    }

    @Test
    public void testJsonToTurtle() throws Exception {
        var in = rp.get("classpath:/picasso.json?context=classpath:/picasso-context.jsonld");
        var out = new MemoryResource(RdfMediaTypes.TURTLE);
        rosetta.convertInto(in, out);

        assertThat(out.byteSource().size()).isGreaterThan(500);
        assertThat(out.charSource().read()).contains("firstName> \"Salvador\"");
    }

    @Test
    public void testJsonToJsonld() throws Exception {
        var in = rp.get("classpath:/picasso.json?context=classpath:/picasso-context.jsonld");
        var out = new MemoryResource(RdfMediaTypes.JSON_LD);
        rosetta.convertInto(in, out);
        // This is to detect invalid JSON, which happened during the RDF4j 5.0.1 to 5.0.2 upgrade
        JSON.readObject(out.charSource().read());
        assertThat(out.charSource().read()).contains("firstName");
    }

    @Test
    public void testYamlToTurtle() throws Exception {
        var in = rp.get("classpath:/picasso.yaml?context=classpath:/picasso-context.jsonld");
        var out = new MemoryResource(RdfMediaTypes.TURTLE);
        rosetta.convertInto(in, out);

        assertThat(out.byteSource().size()).isGreaterThan(350);
        assertThat(out.charSource().read()).contains("firstName> \"Salvador\"");
    }

    @Test
    public void testXMLToTurtle() throws Exception {
        // TODO Make this test all "classpath:/**.xml"...
        var in = rp.get("classpath:/greeting1-nested.xml");
        var out = new MemoryResource(RdfMediaTypes.TURTLE);
        rosetta.convertIntoOrThrow(in, out);
        var ttl = out.charSource().read();
        assertThat(ttl).contains("\n<classpath:/");
        // This makes sure that namespace prefixes were used
        assertThat(ttl).contains("ex:message \"hello, world\"");
    }

    @Test
    public void testGexfAndGraphvizAndGraphCommons() throws Exception {
        var in = rp.get("classpath:/graph.ttl");
        try (var ctx = TLC.open()) {
            // This tests that StackedThingProvider in GraphvizGenerator works;
            // if it did not "shadow", then we would have an empty Salutation.
            Thing rdfsClass =
                    ImmutableThing.builder().iri("https://example.org/Salutation").build();
            var tp = new ThingMemoryRepositoryROBuilder().store(rdfsClass).build();
            ctx.push(ThingProvider.class, tp);

            var namespaceRepo = NamespaceRepositoryEnolaDefaults.INSTANCE;
            var namespaceConverter = new NamespaceConverterWithRepository(namespaceRepo);
            ctx.push(NamespaceConverter.class, namespaceConverter);

            // TODO GexfMediaTypeTest: checkRosettaConvert(in, "classpath:/graph.expected.gexf");
            var gexf = new MemoryResource(GexfMediaType.GEXF);
            rosetta.convertInto(in, gexf);
            assertThat(gexf)
                    .hasCharsEqualTo(rp.get("classpath:/graph.expected.gexf?charset=UTF-8"));

            // TODO checkRosettaConvert(in, "classpath:/graph.expected-full.gv?" +
            // OUT_URI_QUERY_PARAMETER_FULL + "=true");
            var gv = new MemoryResource(GV, OUT_URI_QUERY_PARAMETER_FULL + "=true");
            rosetta.convertInto(in, gv);
            assertThat(gv).hasCharsEqualTo(rp.get("classpath:/graph.expected-full.gv"));

            // TODO checkRosettaConvert(in, "classpath:/graph.expected-full.gv?"
            //  + OUT_URI_QUERY_PARAMETER_FULL + "=false");
            gv = new MemoryResource(GV, OUT_URI_QUERY_PARAMETER_FULL + "=false");
            rosetta.convertInto(in, gv);
            assertThat(gv).hasCharsEqualTo(rp.get("classpath:/graph.expected-short.gv"));

            checkRosettaConvert(in, "classpath:/graph.expected.graphcommons.json");
        }
    }

    void checkRosettaConvert(ReadableResource in, String expectedURL) throws IOException {
        var expected = rp.get(expectedURL);
        var actual = new MemoryResource(expected.mediaType());
        rosetta.convertInto(in, actual);
        assertThat(actual).hasCharsEqualTo(expected);
    }
}

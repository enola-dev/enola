/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2024 The Enola <https://enola.dev> Authors
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

import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PARAMETER_PROTO_MESSAGE;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTOBUF_TEXTPROTO_UTF_8;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.MemoryResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.StringResource;
import dev.enola.rdf.RdfMediaTypes;
import dev.enola.thing.io.ThingMediaTypes;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class RosettaTest {

    private static final ResourceProvider rp = new ClasspathResource.Provider();
    private static final Rosetta rosetta = new Rosetta(rp);

    // These intentionally only test some cases; more detailed tests are done e.g. in YamlJsonTest,
    // and in ProtoIOTest and (indirectly) EntityKindRepositoryTest, and other tests.

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
    public void testTextprotoToYaml() throws Exception {
        var in =
                new ClasspathResource(
                        "bar-abc-def.textproto",
                        PROTOBUF_TEXTPROTO_UTF_8.withParameter(
                                PARAMETER_PROTO_MESSAGE, "dev.enola.core.Entity"));
        var out = new MemoryResource(YAML_UTF_8);
        rosetta.convertInto(in, out);

        var expectedOut =
                StringResource.of(
                        """
                        id:
                          ns: demo
                          entity: bar
                          paths: [abc, def]
                        related:
                          one:
                            ns: demo
                            entity: baz
                            paths: [uvw]
                        link: {wiki:\
                         'https://en.wikipedia.org/w/index.php?fulltext=Search&search=def'}
                        """,
                        YAML_UTF_8);
        assertThat(out.charSource().read()).isEqualTo(expectedOut.charSource().read());
    }

    @Test
    public void testYamlToTextproto() throws Exception {
        var in =
                new ClasspathResource(
                        "bar-abc-def.yaml",
                        YAML_UTF_8.withParameter(PARAMETER_PROTO_MESSAGE, "dev.enola.core.Entity"));
        var out = new MemoryResource(PROTOBUF_TEXTPROTO_UTF_8);
        rosetta.convertInto(in, out);

        var expectedOut = new ClasspathResource("bar-abc-def.textproto");
        assertThat(out.charSource().read()).isEqualTo(expectedOut.charSource().read());
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
    public void testYamlToTurtle() throws Exception {
        var in = rp.get("classpath:/picasso.yaml?context=classpath:/picasso-context.jsonld");
        var out = new MemoryResource(RdfMediaTypes.TURTLE);
        rosetta.convertInto(in, out);

        assertThat(out.byteSource().size()).isGreaterThan(350);
        assertThat(out.charSource().read()).contains("firstName> \"Salvador\"");
    }
}

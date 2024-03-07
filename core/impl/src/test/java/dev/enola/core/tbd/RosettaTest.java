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
package dev.enola.core.tbd;

import static com.google.common.net.MediaType.JSON_UTF_8;
import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PARAMETER_PROTO_MESSAGE;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTOBUF_TEXTPROTO_UTF_8;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.MemoryResource;
import dev.enola.common.io.resource.StringResource;

import org.junit.Test;

public class RosettaTest {

    // These intentionally only tests 1 case; more detailed tests are done in YamlJsonTest,
    // and in ProtoIOTest and (indirectly) EntityKindRepositoryTest, and other tests.

    @Test
    public void testJsonToYaml() throws Exception {
        var in = StringResource.of("{\"value\":123}", JSON_UTF_8);
        var out = new MemoryResource(YAML_UTF_8);
        new Rosetta().convert(in, out);
        assertThat(out.charSource().read()).isEqualTo("{value: 123.0}\n");
    }

    @Test
    public void testYamlToJson() throws Exception {
        var in = StringResource.of("value: 123", YAML_UTF_8);
        var out = new MemoryResource(JSON_UTF_8);
        new Rosetta().convert(in, out);
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
        new Rosetta().convert(in, out);

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
        new Rosetta().convert(in, out);

        var expectedOut = new ClasspathResource("bar-abc-def.textproto", PROTOBUF_TEXTPROTO_UTF_8);
        assertThat(out.charSource().read()).isEqualTo(expectedOut.charSource().read());
    }
}

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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
package dev.enola.core;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.MemoryResource;
import dev.enola.common.io.resource.StringResource;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.common.protobuf.ProtobufMediaTypes;
import dev.enola.common.yamljson.YamlJson;
import dev.enola.core.meta.proto.EntityKinds;
import dev.enola.core.proto.Entity;

import org.junit.Test;

import java.io.IOException;

public class EntityTest {

    @Test
    public void testReadingEntities() throws IOException {
        ProtoIO.check("foo-abc.textproto", Entity.newBuilder());
        ProtoIO.check("bar-abc-def.textproto", Entity.newBuilder());
    }

    @Test
    public void testReadingEntityKinds() throws IOException {
        ProtoIO.check("demo-model.textproto", EntityKinds.newBuilder());
        ProtoIO.check("demo-model.yaml", EntityKinds.newBuilder());
    }

    @Test
    public void testConvertMetaModelToYAML() throws IOException {
        var io = new ProtoIO();
        var textprotoResource = new ClasspathResource("demo-model.textproto");
        var messageFromTextproto = io.read(textprotoResource, EntityKinds.newBuilder()).build();

        var jsonResource = new MemoryResource(ProtobufMediaTypes.PROTOBUF_JSON_UTF_8);
        io.convert(textprotoResource, EntityKinds.newBuilder(), jsonResource);
        var messageFromJSON = io.read(jsonResource, EntityKinds.newBuilder()).build();
        assertThat(messageFromJSON).isEqualTo(messageFromTextproto);

        var yamlResource = new MemoryResource(ProtobufMediaTypes.PROTOBUF_YAML_UTF_8);
        io.convert(textprotoResource, EntityKinds.newBuilder(), yamlResource);
        var messageFromYAML = io.read(yamlResource, EntityKinds.newBuilder()).build();
        assertThat(messageFromYAML).isEqualTo(messageFromTextproto);

        assertThat(messageFromYAML).isEqualTo(messageFromJSON);
    }

    @Test
    public void testConvertMetaModelToYAML_OLD() throws IOException {
        var metaModel =
                new ProtoIO()
                        .read(
                                new ClasspathResource("demo-model.textproto"),
                                EntityKinds.newBuilder(),
                                EntityKinds.class);

        var jsonResource = new MemoryResource(ProtobufMediaTypes.PROTOBUF_JSON_UTF_8);
        new ProtoIO().write(metaModel, jsonResource);
        var jsonText = jsonResource.charSource().read();

        var yamlText = YamlJson.jsonToYaml(jsonText);
        var jsonText2 = YamlJson.yamlToJson(yamlText);

        var jsonResource2 = new StringResource(jsonText2, ProtobufMediaTypes.PROTOBUF_JSON_UTF_8);
        var metaModel2 =
                new ProtoIO().read(jsonResource2, EntityKinds.newBuilder(), EntityKinds.class);

        assertThat(metaModel2).isEqualTo(metaModel);
    }
    // Transform demo-model.textproto to YAML in-memory, make sure they match (and vice versa)
}

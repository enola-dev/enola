/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.object.jackson;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import com.google.common.collect.ImmutableMap;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.common.io.object.ExampleRecord;
import dev.enola.common.io.object.ObjectReader;
import dev.enola.common.io.object.ObjectWriter;
import dev.enola.common.io.resource.DataResource;
import dev.enola.common.io.resource.MemoryResource;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JacksonObjectReaderWritersTest {

    // TODO Factor out test logic and reuse also for JSON etc.

    public @Rule SingletonRule r = $(MediaTypeProviders.set(new YamlMediaType()));

    @Test
    public void readSimplestYAML_toMap() throws IOException {
        var yaml1 = "string: hello, world";
        var resource = DataResource.of(yaml1, YamlMediaType.YAML_UTF_8);
        ObjectReader or = new YamlObjectReaderWriter();

        var example = or.read(resource, Map.class);
        assertThat(example).containsExactly("string", "hello, world");
    }

    @Test
    public void readSimplestYAML_toExampleClass() throws IOException {
        var yaml1 = "string: hello, world";
        var resource = DataResource.of(yaml1, YamlMediaType.YAML_UTF_8);
        ObjectReader or = new YamlObjectReaderWriter();

        var example = or.read(resource, ExampleRecord.class);
        assertThat(example.string()).isEqualTo("hello, world");
        assertThat(example.stringSet()).isEmpty();
        assertThat(example.stringList()).isEmpty();
        assertThat(example.example()).isNull();
    }

    @Test
    public void readComplexYAML_toExampleRecord() throws IOException {
        var yaml1 =
                """
                string: hello, world
                stringSet: [hello, world]
                stringList: [hello, world]
                example:
                  string: hey
                """;
        var resource = DataResource.of(yaml1, YamlMediaType.YAML_UTF_8);
        ObjectReader or = new YamlObjectReaderWriter();

        var example = or.read(resource, ExampleRecord.class);
        assertThat(example.string()).isEqualTo("hello, world");
        assertThat(example.stringSet()).containsExactly("hello", "world");
        assertThat(example.stringList()).containsExactly("hello", "world").inOrder();
        assertThat(example.example().string()).isEqualTo("hey");
    }

    @Test
    public void writeYAML_fromExampleRecord() throws IOException {
        ObjectWriter ow = new YamlObjectReaderWriter();

        var sr = new MemoryResource(YamlMediaType.YAML_UTF_8);
        var example = new ExampleRecord("hello, world", Set.of(), List.of(), null);
        assertThat(ow.write(example, sr)).isTrue();
        assertThat(sr.charSource().read()).isEqualTo("string: \"hello, world\"\n");

        sr = new MemoryResource(YamlMediaType.YAML_UTF_8);
        example = new ExampleRecord("hello world", Set.of(), List.of(), null);
        assertThat(ow.write(example, sr)).isTrue();
        assertThat(sr.charSource().read()).isEqualTo("string: hello world\n");
    }

    @Test
    public void writeYAML_fromMap() throws IOException {
        ObjectWriter ow = new YamlObjectReaderWriter();
        var sr = new MemoryResource(YamlMediaType.YAML_UTF_8);
        var map = ImmutableMap.of("string", "hello, world");
        assertThat(ow.write(map, sr)).isTrue();
        assertThat(sr.charSource().read()).isEqualTo("string: \"hello, world\"\n");
    }
}

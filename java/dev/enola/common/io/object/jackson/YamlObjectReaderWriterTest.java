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

import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;

import com.google.common.collect.ImmutableMap;

import dev.enola.common.io.object.ExamplePlainClass;
import dev.enola.common.io.object.ExampleRecord;
import dev.enola.common.io.object.ObjectReader;
import dev.enola.common.io.object.ObjectWriter;
import dev.enola.common.io.resource.DataResource;
import dev.enola.common.io.resource.MemoryResource;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class YamlObjectReaderWriterTest {

    // NB: Keep the very similar JsonObjectReaderWriterTest in sync with this!

    @Test
    public void readSimplestYAML_toMap() throws IOException {
        var yaml = "string: hello, world";
        var resource = DataResource.of(yaml, YAML_UTF_8);
        ObjectReader or = new YamlObjectReaderWriter();

        var example = or.read(resource, Map.class);
        assertThat(example).containsExactly("string", "hello, world");
    }

    @Test
    public void readSimplestYAML_toExampleClass() throws IOException {
        var yaml = "string: hello, world";
        var resource = DataResource.of(yaml, YAML_UTF_8);
        ObjectReader or = new YamlObjectReaderWriter();

        var example = or.read(resource, ExampleRecord.class);
        assertThat(example.string()).isEqualTo("hello, world");
        assertThat(example.stringSet()).isEmpty();
        assertThat(example.stringList()).isEmpty();
        assertThat(example.example()).isNull();
    }

    @Test
    @Ignore // TODO
    public void readStreamOfYAML_toExampleClassList() throws IOException {
        var yaml = "string: hello, world\n---string: saluton\n";
        var resource = DataResource.of(yaml, YAML_UTF_8);
        ObjectReader or = new YamlObjectReaderWriter();

        var examples = or.readAll(resource, ExampleRecord.class);
        assertThat(examples).hasSize(2);
        assertThat(examples.iterator().next().string()).isEqualTo("hello, world");
        assertThat(examples.iterator().next().string()).isEqualTo("saluton");
    }

    @Test
    public void readComplexYAML_toExampleRecord() throws IOException {
        var example = readComplexYAML_toExample(ExampleRecord.class);
        assertThat(example.string()).isEqualTo("hello, world");
        assertThat(example.stringSet()).containsExactly("hello", "world");
        assertThat(example.stringList()).containsExactly("hello", "world").inOrder();
        assertThat(example.example().string()).isEqualTo("hey");
        assertThat(example.defaultValue()).isEqualTo("hallo");
        assertThat(example.timestamp()).isEqualTo(Instant.parse("2016-05-18T06:51:25Z"));
        // TODO assertThat(example.isPrivate()).isTrue();
    }

    @Test
    public void readComplexYAML_toExamplePlainClass() throws IOException {
        var example = readComplexYAML_toExample(ExamplePlainClass.class);
        assertThat(example.string).isEqualTo("hello, world");
        assertThat(example.stringSet).containsExactly("hello", "world");
        assertThat(example.stringList).containsExactly("hello", "world").inOrder();
        assertThat(example.example.string()).isEqualTo("hey");
        assertThat(example.defaultValue).isEqualTo("hallo");
        assertThat(example.timestamp).isEqualTo(Instant.parse("2016-05-18T06:51:25Z"));
        // TODO assertThat(example.isPrivate).isTrue();
    }

    private <T> T readComplexYAML_toExample(Class<T> clazz) throws IOException {
        var yaml =
                """
                string: hello, world
                stringSet: [hello, world]
                stringList: [hello, world]
                example:
                  string: hey
                default: hallo
                timestamp: 1463554285
                ignoreUnknown: yolo
                """; // TODO private: 1
        var resource = DataResource.of(yaml, YAML_UTF_8);
        ObjectReader or = new YamlObjectReaderWriter();
        return or.read(resource, clazz);
    }

    @Test
    public void writeYAML_fromExampleRecord() throws IOException {
        ObjectWriter ow = new YamlObjectReaderWriter();

        var sr = new MemoryResource(YAML_UTF_8);
        var example =
                new ExampleRecord("hello, world", Set.of(), List.of(), null, null, null, null);
        assertThat(ow.write(example, sr)).isTrue();
        assertThat(sr.charSource().read()).isEqualTo("string: \"hello, world\"\n");

        sr = new MemoryResource(YAML_UTF_8);
        example = new ExampleRecord("hello world", Set.of(), List.of(), null, null, null, null);
        assertThat(ow.write(example, sr)).isTrue();
        assertThat(sr.charSource().read()).isEqualTo("string: hello world\n");
    }

    @Test
    public void writeYAML_fromMap() throws IOException {
        ObjectWriter ow = new YamlObjectReaderWriter();
        var sr = new MemoryResource(YAML_UTF_8);
        var map = ImmutableMap.of("string", "hello, world");
        assertThat(ow.write(map, sr)).isTrue();
        assertThat(sr.charSource().read()).isEqualTo("string: \"hello, world\"\n");
    }
}

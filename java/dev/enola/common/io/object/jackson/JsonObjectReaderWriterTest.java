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

import static com.google.common.net.MediaType.JSON_UTF_8;
import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;

import dev.enola.common.io.object.ExamplePlainClass;
import dev.enola.common.io.object.ExampleRecord;
import dev.enola.common.io.object.ObjectReader;
import dev.enola.common.io.object.ObjectWriter;
import dev.enola.common.io.resource.DataResource;
import dev.enola.common.io.resource.MemoryResource;

import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonObjectReaderWriterTest {

    // NB: Keep the very similar YamlObjectReaderWriterTest in sync with this!

    @Test
    public void readEmpty_toMap() throws IOException {
        var resource = DataResource.of("", JSON_UTF_8);
        ObjectReader or = new JsonObjectReaderWriter();

        var example = or.read(resource, Map.class);
        assertThat(example).isEmpty();
    }

    @Test
    public void readSimplestJSON_toMap() throws IOException {
        var json = "{ \"string\": \"hello, world\" }";
        var resource = DataResource.of(json, JSON_UTF_8);
        ObjectReader or = new JsonObjectReaderWriter();

        var example = or.read(resource, Map.class);
        assertThat(example).containsExactly("string", "hello, world");
    }

    @Test
    public void readSimplestJSON_toExampleClass() throws IOException {
        var json = "{ \"string\": \"hello, world\" }";
        var resource = DataResource.of(json, JSON_UTF_8);
        ObjectReader or = new JsonObjectReaderWriter();

        var example = or.read(resource, ExampleRecord.class);
        assertThat(example.string()).isEqualTo("hello, world");
        assertThat(example.stringSet()).isEmpty();
        assertThat(example.stringList()).isEmpty();
        assertThat(example.example()).isNull();
    }

    @Test
    public void readJsonArray_toExampleRecordList() throws IOException {
        var json = "[ { \"string\": \"hello, world\" }, { \"string\": \"saluton\" } ]";
        var resource = DataResource.of(json, JSON_UTF_8);
        ObjectReader or = new JsonObjectReaderWriter();

        var examples = or.readArray(resource, ExampleRecord.class);
        assertThat(examples).hasSize(2);
        var iterator = examples.iterator();
        assertThat(iterator.next().string()).isEqualTo("hello, world");
        assertThat(iterator.next().string()).isEqualTo("saluton");
    }

    @Test
    public void readComplexJSON_toExampleRecord() throws IOException {
        var example = readComplexJSON_toExample(ExampleRecord.class);
        assertThat(example.string()).isEqualTo("hello, world");
        assertThat(example.stringSet()).containsExactly("hello", "world");
        assertThat(example.stringList()).containsExactly("hello", "world").inOrder();
        assertThat(example.example().string()).isEqualTo("hey");
        assertThat(example.defaultValue()).isEqualTo("hallo");
        assertThat(example.timestamp()).isEqualTo(Instant.parse("2016-05-18T06:51:25Z"));
        // TODO assertThat(example.isPrivate()).isTrue();
    }

    @Test
    public void readComplexJSON_toExamplePlainClass() throws IOException {
        var example = readComplexJSON_toExample(ExamplePlainClass.class);
        assertThat(example.string).isEqualTo("hello, world");
        assertThat(example.stringSet).containsExactly("hello", "world");
        assertThat(example.stringList).containsExactly("hello", "world").inOrder();
        assertThat(example.example.string()).isEqualTo("hey");
        assertThat(example.defaultValue).isEqualTo("hallo");
        assertThat(example.timestamp).isEqualTo(Instant.parse("2016-05-18T06:51:25Z"));
        // TODO assertThat(example.isPrivate).isTrue();
    }

    private <T> T readComplexJSON_toExample(Class<T> clazz) throws IOException {
        var json =
                """
                { "string": "hello, world",
                "stringSet": ["hello", "world"],
                "stringList": ["hello", "world"],
                "example": { "string": "hey" },
                "default": "hallo",
                "timestamp": "1463554285"
                }
                """; // TODO "private": "1",
        // NO! "ignoreUnknown": "yolo"
        var resource = DataResource.of(json, JSON_UTF_8);
        ObjectReader or = new JsonObjectReaderWriter();
        return or.read(resource, clazz);
    }

    @Test
    public void writeJSON_fromExampleRecord() throws IOException {
        ObjectWriter ow = new JsonObjectReaderWriter();

        var sr = new MemoryResource(JSON_UTF_8);
        var example =
                new ExampleRecord(
                        "hello, world",
                        Set.of(),
                        List.of(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
        assertThat(ow.write(example, sr)).isTrue();
        assertThat(sr.charSource().read()).isEqualTo("{\"string\":\"hello, world\"}");

        sr = new MemoryResource(JSON_UTF_8);
        example =
                new ExampleRecord(
                        "hello world",
                        Set.of(),
                        List.of(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
        assertThat(ow.write(example, sr)).isTrue();
        assertThat(sr.charSource().read()).isEqualTo("{\"string\":\"hello world\"}");
    }

    @Test
    public void writeJSON_fromMap() throws IOException {
        ObjectWriter ow = new JsonObjectReaderWriter();
        var sr = new MemoryResource(JSON_UTF_8);
        var map = ImmutableMap.of("string", "hello, world");
        assertThat(ow.write(map, sr)).isTrue();
        assertThat(sr.charSource().read()).isEqualTo("{\"string\":\"hello, world\"}");
    }

    // Tests for JSONc support: https://github.com/enola-dev/enola/issues/1847

    @Test
    public void readJSON_withSingleLineComments() throws IOException {
        var json =
                """
                {
                  // This is a single-line comment
                  "string": "hello, world"
                }
                """;
        var resource = DataResource.of(json, JSON_UTF_8);
        ObjectReader or = new JsonObjectReaderWriter();

        var example = or.read(resource, Map.class);
        assertThat(example).containsExactly("string", "hello, world");
    }

    @Test
    public void readJSON_withMultiLineComments() throws IOException {
        var json =
                """
                {
                  /* This is a
                     multi-line comment */
                  "string": "hello, world"
                }
                """;
        var resource = DataResource.of(json, JSON_UTF_8);
        ObjectReader or = new JsonObjectReaderWriter();

        var example = or.read(resource, Map.class);
        assertThat(example).containsExactly("string", "hello, world");
    }

    @Test
    public void readJSON_withTrailingCommaInObject() throws IOException {
        var json =
                """
                {
                  "string": "hello, world",
                  "number": 42,
                }
                """;
        var resource = DataResource.of(json, JSON_UTF_8);
        ObjectReader or = new JsonObjectReaderWriter();

        var example = or.read(resource, Map.class);
        assertThat(example).containsExactly("string", "hello, world", "number", 42);
    }

    @Test
    public void readJSON_withTrailingCommaInArray() throws IOException {
        var json =
                """
                {
                  "stringList": ["hello", "world",]
                }
                """;
        var resource = DataResource.of(json, JSON_UTF_8);
        ObjectReader or = new JsonObjectReaderWriter();

        var example = or.read(resource, ExampleRecord.class);
        assertThat(example.stringList()).containsExactly("hello", "world").inOrder();
    }

    @Test
    public void readJSON_withCommentsAndTrailingCommas() throws IOException {
        var json =
                """
                {
                  // Name of the person
                  "string": "hello, world",
                  /* List of items */
                  "stringList": [
                    "hello",
                    "world", // trailing comma here
                  ],
                }
                """;
        var resource = DataResource.of(json, JSON_UTF_8);
        ObjectReader or = new JsonObjectReaderWriter();

        var example = or.read(resource, ExampleRecord.class);
        assertThat(example.string()).isEqualTo("hello, world");
        assertThat(example.stringList()).containsExactly("hello", "world").inOrder();
    }
}

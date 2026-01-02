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
package dev.enola.common.protobuf.schema;

import static com.google.common.truth.Truth.assertThat;

import static java.nio.charset.StandardCharsets.US_ASCII;

import com.google.common.io.Resources;

import dev.enola.common.yamljson.YamlJson;
import dev.enola.protobuf.test.TestSimple;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

public class MessageDescriptorToSchemaConverterTest {

    // TODO Add an actual JSON Schema parser, to validate gen. JSON

    // TODO Write out Proto instance as JSON, and validate that against Schema

    MessageDescriptorToSchemaConverter c = new MessageDescriptorToSchemaConverter();

    @Test
    public void EMPTY() {} // TODO Remove!

    @Test
    @Ignore
    public void testSimple() throws IOException {
        var testSimple = TestSimple.newBuilder().build();
        var schema = c.convert(testSimple);
        assertEquals(schema.toJson(), "TestSimple.schema.yaml");
    }

    private void assertEquals(String actualJSON, String expectedResourceYAML) throws IOException {
        var actualYAML = YamlJson.jsonToYaml(actualJSON);
        var expectedYAML = read(expectedResourceYAML);
        assertThat(actualYAML).isEqualTo(expectedYAML);
    }

    private String read(String resource) throws IOException {
        return Resources.toString(Resources.getResource(resource), US_ASCII);
    }
}

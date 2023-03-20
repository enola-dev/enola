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
package dev.enola.common.yamljson;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.yamljson.YamlJson.jsonToYaml;
import static dev.enola.common.yamljson.YamlJson.yamlToJson;

import org.junit.Test;

public class YamlJsonTest {

    @Test
    public void testYAMLToJSON() {
        assertThat(yamlToJson("value: 123")).isEqualTo("{\"value\":123}");
    }

    @Test
    public void testJSONToYaml() {
        assertThat(jsonToYaml("{\"value\":123}")).isEqualTo("{value: 123.0}\n");
        assertThat(jsonToYaml("{\"number\":123, \"text\":\"hello\"}"))
                .isEqualTo("{number: 123.0, text: hello}\n");
    }

    @Test
    public void testEmpty() {
        assertThat(jsonToYaml("")).isEmpty();
        assertThat(jsonToYaml("{}")).isEmpty();
        assertThat(jsonToYaml("[]")).isEmpty();

        assertThat(yamlToJson("")).isEmpty();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultiYAML() {
        yamlToJson("value: 123\n---\nvalue: 456");
    }
}

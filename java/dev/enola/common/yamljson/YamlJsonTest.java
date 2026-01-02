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
package dev.enola.common.yamljson;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.yamljson.YamlJson.jsonToYaml;
import static dev.enola.common.yamljson.YamlJson.yamlToJson;

import org.junit.Test;

public class YamlJsonTest {

    @Test
    public void testYAMLToJSON() {
        assertThat(yamlToJson("value: 123")).isEqualTo("{\"value\":123}");
        assertThat(yamlToJson("value: 123\n---\nhello: world"))
                .isEqualTo("[{\"value\":123},{\"hello\":\"world\"}]");
    }

    @Test
    public void testJSONToYaml() {
        // Maps
        assertThat(jsonToYaml("{\"value\":123}")).isEqualTo("{value: 123.0}\n");
        assertThat(jsonToYaml("{\"number\":123, \"text\":\"hello\"}"))
                .isEqualTo("{number: 123.0, text: hello}\n");

        // Arrays
        assertThat(jsonToYaml("[{\"value\":123}, {\"hello\":\"world\"}]"))
                .isEqualTo("- {value: 123.0}\n- {hello: world}\n");
    }

    @Test
    public void testEmpty() {
        assertThat(jsonToYaml("")).isEmpty();
        assertThat(jsonToYaml("{}")).isEmpty();
        assertThat(jsonToYaml("[]")).isEqualTo("[]\n");

        assertThat(yamlToJson("")).isEmpty();
    }

    @Test
    public void canonicalizeJSON() {
        assertThat(JSON.canonicalize(" {  'a':\n37}", false)).isEqualTo("{\"a\":37.0}");
        assertThat(JSON.canonicalize(" {\"b\":\"hi\", \"a\":37.0}", false))
                .isEqualTo("{\"a\":37.0,\"b\":\"hi\"}");
        assertThat(JSON.canonicalize("[{\"b\":\"hi\", \"a\":37.0}]", false))
                .isEqualTo("[{\"a\":37.0,\"b\":\"hi\"}]");
        assertThat(JSON.canonicalize("[{\"b\":\"hi\", \"a\":37}]", true))
                .isEqualTo("[\n  {\n    \"a\": 37.0,\n    \"b\": \"hi\"\n  }\n]");
        assertThat(JSON.canonicalize("[null, 0, 1]", false)).isEqualTo("[null,0.0,1.0]");
        assertThat(JSON.canonicalize("[ ]", false)).isEqualTo("[]");
        assertThat(JSON.canonicalize("", false)).isEqualTo("");
    } // TODO See also CanonicalizerTest
}

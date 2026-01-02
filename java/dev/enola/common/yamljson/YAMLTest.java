/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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

import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.common.io.resource.StringResource;

import org.junit.Test;

import java.io.IOException;

public class YAMLTest {

    // See YamlJsonTest for more tests.

    @Test
    public void readResource() throws IOException {
        var resource = StringResource.of("name: it", YamlMediaType.YAML_UTF_8);
        YAML.readSingleMap(resource, map -> assertThat(map).containsExactly("name", "it"));
    }

    @Test
    public void testEmpty() {
        assertThat(YAML.readSingleMap("")).isEmpty();
    }
}

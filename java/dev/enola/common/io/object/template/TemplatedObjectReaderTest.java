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
package dev.enola.common.io.object.template;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.common.io.object.ObjectReader;
import dev.enola.common.io.object.jackson.YamlObjectReaderWriter;
import dev.enola.common.io.resource.DataResource;

import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class TemplatedObjectReaderTest {

    ObjectReader reader = new TemplatedObjectReader(new YamlObjectReaderWriter());

    @Test
    public void empty() throws IOException {
        var map = reader.read(DataResource.of("", YamlMediaType.YAML_UTF_8), Map.class);
        assertThat(map).isEmpty();
    }

    @Test
    public void justPassThroughAsNoTemplatingNeeded() throws IOException {
        var map = reader.read(DataResource.of("hello: world", YamlMediaType.YAML_UTF_8), Map.class);
        assertThat(map).containsExactlyEntriesIn(Map.of("hello", "world"));
    }

    @Test
    public void selfReferential() throws IOException {
        // NOT var template = "world: Enola\nhello: {{world}}";
        var template = "world: Enola\nhello: \"{{world}}\"";
        var resource = DataResource.of(template, YamlMediaType.YAML_UTF_8);
        var map = reader.read(resource, Map.class);
        assertThat(map).hasSize(2);
        assertThat(map).containsEntry("world", "Enola");
        assertThat(map).containsEntry("hello", "Enola");
    }
}

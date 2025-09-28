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
package dev.enola.common.io.object.template;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.common.io.resource.DataResource;
import dev.enola.common.io.resource.MemoryResource;

import org.junit.Test;

import java.io.IOException;

public class TemplyTest {

    // TODO Remove; as replaced by TemplateResourceConverterTest?

    Temply temply = new Temply();

    @Test
    public void selfReferential() throws IOException {
        var template = "world: Enola\nhello: \"{{world}}\"";
        var in = DataResource.of(template, YamlMediaType.YAML_UTF_8);
        var out = new MemoryResource(YamlMediaType.YAML_UTF_8);
        temply.convertIntoOrThrow(in, out);
        assertThat(out.charSource().read()).isEqualTo("world: Enola\nhello: Enola\n");
    }
}

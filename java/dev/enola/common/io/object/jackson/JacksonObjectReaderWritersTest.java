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

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.common.io.object.Example;
import dev.enola.common.io.object.ObjectReader;
import dev.enola.common.io.object.ObjectWriter;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.MemoryResource;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

public class JacksonObjectReaderWritersTest {

    // TODO Factor out test logic and reuse also for JSON etc.

    public @Rule SingletonRule r = $(MediaTypeProviders.set(new YamlMediaType()));

    @Test
    public void readYAML() throws IOException {
        ObjectReader or = new YamlObjectReaderWriter();
        var example = or.read(new ClasspathResource("example.yaml"), Example.class);
        assertThat(example.text()).isEqualTo("hello, world");
    }

    @Test
    public void writeYAML() throws IOException {
        var example = new Example("hello, world");
        var sr = new MemoryResource(YamlMediaType.YAML_UTF_8);
        ObjectWriter ow = new YamlObjectReaderWriter();
        assertThat(ow.write(example, sr)).isTrue();
        assertThat(sr.charSource().read()).isEqualTo("---\ntext: \"hello, world\"\n");
    }
}

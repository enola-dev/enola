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
package dev.enola.common.template.tool;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;
import static dev.enola.common.template.handlebars.HandlebarsMediaType.HANDLEBARS;

import com.google.common.net.MediaType;

import dev.enola.common.io.resource.DataResource;
import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.common.io.resource.TestResource;
import dev.enola.common.template.handlebars.HandlebarsTemplateProvider;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TemplyMainTest {

    TemplyMain temply =
            new TemplyMain(
                    new ResourceProviders(new DataResource.Provider(), new TestResource.Provider()),
                    new HandlebarsTemplateProvider());

    @Test
    public void selfReferential() throws IOException {
        var template = "world: Enola\nhello: \"{{world}}\"";
        var in = DataResource.of(template, YAML_UTF_8);
        try (var out = TestResource.create(YAML_UTF_8)) {
            temply.run(List.of(), in.uri(), out.uri());
            assertThat(out.charSource().read()).isEqualTo("world: Enola\nhello: \"Enola\"");
        }
    }

    @Test
    public void simpleTemplate() throws IOException {
        var data = DataResource.of("hello: world", YAML_UTF_8);
        var template = DataResource.of("Not YAML... hello, {{hello}}", HANDLEBARS);
        try (var out = TestResource.create(MediaType.PLAIN_TEXT_UTF_8)) {
            temply.run(List.of(data.uri()), template.uri(), out.uri());
            assertThat(out.charSource().read()).isEqualTo("Not YAML... hello, world");
        }
    }

    @Test
    public void selfReferentialAndTemplate() throws IOException {
        var data = DataResource.of("world: Enola\nhello: \"{{world}}\"", YAML_UTF_8);
        var template = DataResource.of("Not YAML... {{world}} {{hello}}", HANDLEBARS);
        try (var out = TestResource.create(MediaType.PLAIN_TEXT_UTF_8)) {
            temply.run(List.of(data.uri()), template.uri(), out.uri());
            assertThat(out.charSource().read()).isEqualTo("Not YAML... Enola Enola");
        }
    }

    @Test
    public void csv() throws IOException {
        var csv = DataResource.of("name,age\nAlice,30\nBob,25\n", MediaType.CSV_UTF_8);
        var template =
                DataResource.of("Not YAML...{{#each row}} {{name}} {{age}}{{/each}}", HANDLEBARS);
        try (var out = TestResource.create(MediaType.PLAIN_TEXT_UTF_8)) {
            temply.run(List.of(csv.uri()), template.uri(), out.uri());
            assertThat(out.charSource().read()).isEqualTo("Not YAML... Alice 30 Bob 25");
        }
    }
}

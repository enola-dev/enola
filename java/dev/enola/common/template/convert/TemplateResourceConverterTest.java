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
package dev.enola.common.template.convert;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.common.io.resource.DataResource;
import dev.enola.common.io.resource.TestResource;
import dev.enola.common.template.handlebars.HandlebarsMediaType;
import dev.enola.common.template.handlebars.HandlebarsTemplateProvider;

import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class TemplateResourceConverterTest {

    /*
    public @Rule SingletonRule r =
            SingletonRule.$(
                    MediaTypeProviders.set(
                            // TODO ? new HandlebarsMediaType(),
                            // TODO ? new YamlMediaType(),
                            // TODO ? new StandardMediaTypes()
                            ));
    */

    @Test
    public void simpleHandlebarTemplateWithProgrammaticData() throws IOException {
        var converter = new TemplateResourceConverter(new HandlebarsTemplateProvider());
        converter.putAll(Map.of("world", "Enola"));
        var in = DataResource.of("hello: {{world}}", HandlebarsMediaType.HANDLEBARS);
        try (var out = TestResource.create(YamlMediaType.YAML_UTF_8)) {
            converter.convertIntoOrThrow(in, out);
            assertThat(out.charSource().read()).isEqualTo("hello: Enola");
        }
    }
}

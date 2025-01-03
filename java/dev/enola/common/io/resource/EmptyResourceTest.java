/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2025 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.resource;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.mediatype.YamlMediaType;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class EmptyResourceTest {

    public @Rule SingletonRule r = $(MediaTypeProviders.set(new YamlMediaType()));

    @Test
    public void testEmptyResource() throws IOException {
        var e = new EmptyResource(YamlMediaType.YAML_UTF_8);
        assertThat(e.byteSource().isEmpty()).isTrue();
        assertThat(e.charSource().isEmpty()).isTrue();
        assertThat(e.mediaType()).isEqualTo(YamlMediaType.YAML_UTF_8);
        assertThat(e.uri())
                .isEqualTo(URI.create("empty:?mediaType=application%2Fyaml%3Bcharset%3Dutf-8"));
    }
}

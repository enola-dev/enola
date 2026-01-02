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
package dev.enola.common.io.resource;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.net.MediaType;

import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.mediatype.YamlMediaType;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class EmptyResourceTest {

    // public @Rule SingletonRule r = $(MediaTypeProviders.set(new YamlMediaType()));

    @Test
    public void testEmptyResource() throws IOException {
        var r = new EmptyResource(YamlMediaType.YAML_UTF_8);
        assertThat(r.byteSource().isEmpty()).isTrue();
        assertThat(r.charSource().isEmpty()).isTrue();
        assertThat(r.mediaType()).isEqualTo(YamlMediaType.YAML_UTF_8);
        assertThat(r.uri())
                .isEqualTo(URI.create("empty:?mediaType=application%2Fyaml%3Bcharset%3Dutf-8"));
    }

    @Test
    public void testEmptyResourceURL() throws IOException {
        var r = new EmptyResource.Provider().getResource(EmptyResource.EMPTY_URI);
        assertThat(r.byteSource().isEmpty()).isTrue();
        assertThat(r.charSource().isEmpty()).isTrue();
        assertThat(r.mediaType()).isEqualTo(MediaType.OCTET_STREAM);
        assertThat(r.uri()).isEqualTo(EmptyResource.EMPTY_URI);
        assertThat(r.mediaType().charset()).isAbsent();
    }

    @Test
    public void testEmptyUtf8TextResourceURL() throws IOException {
        var r = new EmptyResource.Provider().getResource(EmptyResource.EMPTY_TEXT_URI);
        assertThat(r.byteSource().isEmpty()).isTrue();
        assertThat(r.charSource().isEmpty()).isTrue();
        assertThat(r.mediaType())
                .isEqualTo(MediaType.OCTET_STREAM.withCharset(StandardCharsets.UTF_8));
        assertThat(r.uri()).isEqualTo(EmptyResource.EMPTY_TEXT_URI);
        assertThat(r.mediaType().charset()).hasValue(StandardCharsets.UTF_8);
        assertThat(URIs.getFilename(r.uri())).isNotEmpty();
    }
}

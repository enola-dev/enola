/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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
package dev.enola.common.canonicalize;

import static com.google.common.truth.Truth.assertThat;

import static org.junit.Assert.assertThrows;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.base.Charsets;
import com.google.common.net.MediaType;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.EmptyResource;
import dev.enola.common.io.resource.MemoryResource;
import dev.enola.common.io.resource.StringResource;

import org.junit.Test;

import java.io.IOException;

public class CanonicalizerTest {

    @Test
    public void unknown() throws IOException {
        var in = new EmptyResource(MediaType.MICROSOFT_WORD);
        var out = new MemoryResource(MediaType.ANY_TYPE);
        assertThrows(
                IllegalArgumentException.class, () -> Canonicalizer.canonicalize(in, out, false));
    }

    @Test
    public void emptyJSON() throws IOException {
        var in = new EmptyResource(MediaType.JSON_UTF_8.withoutParameters());
        var out = new MemoryResource(MediaType.ANY_TYPE);
        Canonicalizer.canonicalize(in, out, false);
        assertThat(out.byteSource().read()).isEmpty(); // NOT .isEqualTo("{}".getBytes(UTF_8));
    }

    @Test
    public void simpleJSON() throws IOException {
        var in = StringResource.of(" {\"b\":\"hi\", \"a\":37.0}", MediaType.JSON_UTF_8);
        var out = new MemoryResource(MediaType.ANY_TYPE);
        Canonicalizer.canonicalize(in, out, false);
        assertThat(out.byteSource().read()).isEqualTo("{\"a\":37.0,\"b\":\"hi\"}".getBytes(UTF_8));
    }

    @Test
    public void canonicalJSON_is_UTF8() throws IOException {
        var in =
                StringResource.of(
                        "{\"b\":\"hi\"}", MediaType.JSON_UTF_8.withCharset(Charsets.UTF_16));
        var out = new MemoryResource(MediaType.ANY_TYPE);
        Canonicalizer.canonicalize(in, out, false);
        assertThat(out.byteSource()).isNotEqualTo(in.byteSource());
        assertThat(in.charSource().read()).isEqualTo(new String(out.byteSource().read(), UTF_8));
        assertThat(out.byteSource().size()).isLessThan(in.byteSource().size());
        // NB: We cannot change the out.mediaType()
    }

    @Test
    public void rfc8785() throws IOException {
        var in = new ClasspathResource("canonicalize.json", MediaType.JSON_UTF_8);
        var out = new MemoryResource(MediaType.JSON_UTF_8);
        Canonicalizer.canonicalize(in, out, false);

        var expected = new ClasspathResource("canonicalize.json.expected", MediaType.JSON_UTF_8);
        assertThat(out.charSource().read()).isEqualTo(expected.charSource().read());
    }
}

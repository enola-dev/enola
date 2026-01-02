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
package dev.enola.common.canonicalize;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;
import static dev.enola.common.io.testlib.ResourceSubject.assertThat;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.net.MediaType;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.context.testlib.TestTLCRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.mediatype.StandardMediaTypes;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.EmptyResource;
import dev.enola.common.io.resource.MemoryResource;
import dev.enola.common.io.resource.StringResource;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CanonicalizerTest {

    public @Rule SingletonRule r = $(MediaTypeProviders.set());

    @Rule
    public TestTLCRule rule =
            TestTLCRule.of(
                    MediaTypeProviders.class, new MediaTypeProviders(new StandardMediaTypes()));

    private final Canonicalizer canonicalizer = new Canonicalizer(new ClasspathResource.Provider());

    @Test
    public void unknown() throws IOException {
        var in = new EmptyResource(MediaType.MICROSOFT_WORD);
        var out = new MemoryResource(MediaType.MICROSOFT_WORD);
        canonicalizer.canonicalize(in, out, false);
        assertThat(out).hasCharsEqualTo(in);
    }

    @Test
    public void emptyJSON() throws IOException {
        var in = new EmptyResource(MediaType.JSON_UTF_8.withoutParameters());
        var out = new MemoryResource(MediaType.ANY_TYPE);
        canonicalizer.canonicalize(in, out, false);
        assertThat(out.byteSource().read()).isEmpty(); // NOT .isEqualTo("{}".getBytes(UTF_8));
    }

    @Test
    public void simpleJSON() throws IOException {
        var in = StringResource.of(" {\"b\":\"hi\", \"a\":37.0}", MediaType.JSON_UTF_8);
        var out = new MemoryResource(MediaType.ANY_TYPE);
        canonicalizer.canonicalize(in, out, false);
        assertThat(out.byteSource().read()).isEqualTo("{\"a\":37.0,\"b\":\"hi\"}".getBytes(UTF_8));
    }

    @Test
    public void canonicalJSON_is_UTF8() throws IOException {
        var in =
                StringResource.of(
                        "{\"b\":\"hi\"}",
                        MediaType.JSON_UTF_8.withCharset(StandardCharsets.UTF_16));
        var out = new MemoryResource(MediaType.ANY_TYPE);
        canonicalizer.canonicalize(in, out, false);
        assertThat(out.byteSource()).isNotEqualTo(in.byteSource());
        assertThat(in.charSource().read()).isEqualTo(new String(out.byteSource().read(), UTF_8));
        assertThat(out.byteSource().size()).isLessThan(in.byteSource().size());
        // NB: We cannot change the out.mediaType()
    }

    @Test
    public void rfc8785() throws IOException {
        var in = new ClasspathResource("canonicalize.json");
        var out = new MemoryResource(MediaType.JSON_UTF_8);
        canonicalizer.canonicalize(in, out, false);

        var expected = new ClasspathResource("canonicalize.json.expected", MediaType.JSON_UTF_8);
        assertThat(out.charSource().read()).isEqualTo(expected.charSource().read());
    }

    @Test
    public void jsonld() throws IOException {
        var md = MediaType.parse("application/ld+json").withCharset(UTF_8);
        var in = new ClasspathResource("canonicalize.jsonld", md);
        var out = new MemoryResource(MediaType.JSON_UTF_8);
        canonicalizer.canonicalize(in, out, true);

        var expected = new ClasspathResource("canonicalize.jsonld.expected", MediaType.JSON_UTF_8);
        assertThat(out.charSource().read()).isEqualTo(expected.charSource().read());
    }
}

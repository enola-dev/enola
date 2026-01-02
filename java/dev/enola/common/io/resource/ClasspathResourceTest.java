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

import static com.google.common.net.MediaType.*;
import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTOBUF_TEXTPROTO_UTF_8;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTO_UTF_8;

import static org.junit.Assert.assertThrows;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.io.Resources;
import com.google.common.net.MediaType;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.common.protobuf.ProtobufMediaTypes;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Optional;

public class ClasspathResourceTest {

    public @Rule SingletonRule r =
            $(
                    MediaTypeProviders.SINGLETON.set(
                            new MediaTypeProviders(new YamlMediaType(), new ProtobufMediaTypes())));

    private ReadableResource check(
            String path, MediaType expectedMediaType, Optional<Charset> expectedCharset)
            throws IOException {
        var resource = new ClasspathResource(path);
        assertThat(resource.mediaType()).isEqualTo(expectedMediaType);
        assertThat(resource.mediaType().charset().toJavaUtil()).isEqualTo(expectedCharset);
        return resource;
    }

    protected void checkText(
            String name,
            MediaType expectedMediaType,
            Optional<Charset> expectedCharset,
            String expectedContent)
            throws IOException {
        var resource = check(name, expectedMediaType, expectedCharset);
        assertThat(resource.charSource().read()).isEqualTo(expectedContent);
    }

    protected void checkText(
            String name,
            MediaType expectedMediaType,
            Optional<Charset> expectedCharset,
            long expectedSize)
            throws IOException {
        var resource = check(name, expectedMediaType, expectedCharset);
        assertThat(resource.byteSource().size()).isEqualTo(expectedSize);
    }

    protected void checkBinary(String name, MediaType expectedMediaType, long expectedSize)
            throws IOException {
        var resource = check(name, expectedMediaType, Optional.empty());
        assertThat(resource.byteSource().size()).isEqualTo(expectedSize);
        assertThrows(IllegalStateException.class, () -> resource.charSource());
    }

    @Test
    public void testResources() throws IOException {
        checkBinary("empty", OCTET_STREAM, 0);
        checkBinary("test-random-binary", OCTET_STREAM, 7);

        checkBinary("empty.png", PNG, 0);
        checkBinary("test.png", PNG, 3435);

        checkText("test.json", JSON_UTF_8, Optional.of(UTF_8), "{}\n");
        checkText("test.proto", PROTO_UTF_8, Optional.of(UTF_8), 749);
        checkText("test.textproto", PROTOBUF_TEXTPROTO_UTF_8, Optional.of(UTF_8), 753);

        checkText(
                "test-hello-ascii.txt",
                PLAIN_TEXT_UTF_8.withCharset(UTF_8),
                Optional.of(UTF_8),
                "hello, world\n");
        checkText(
                "test-french.txt",
                PLAIN_TEXT_UTF_8.withCharset(UTF_8),
                Optional.of(UTF_8),
                "Ça va?\n");
        checkText(
                "test-emoji.txt",
                PLAIN_TEXT_UTF_8.withCharset(UTF_8),
                Optional.of(UTF_8),
                "🕵🏾‍♀️\n");

        var md = "# Markdown\n\n❤️\n";
        checkText(
                "test.md",
                MediaType.create("text", "markdown").withCharset(UTF_8),
                Optional.of(UTF_8),
                md);
        var resource = new UrlResource(Resources.getResource("test.md"));
        assertThat(resource.charSource().read()).isEqualTo(md);
    }

    @Test
    public void testQueryParameters() throws IOException {
        var iri = "classpath:/test-french.txt?arg=val";
        var uri = URI.create(iri);
        var rp = new ClasspathResource.Provider();
        var r = rp.getResource(uri);
        assertThat(r.charSource().read()).isEqualTo("Ça va?\n");
        assertThat(r.mediaType()).isEqualTo(PLAIN_TEXT_UTF_8);
        assertThat(r.uri().getQuery()).isEqualTo(uri.getQuery());
        assertThat(r.uri().getQuery()).isNotEmpty();
    }

    @Test
    public void viaProvider() throws IOException {
        var r = new ClasspathResource("test.json");
        var rp = new ClasspathResource.Provider();
        assertThat(rp.getResource(r.uri()).charSource().read()).isEqualTo("{}\n");
    }

    @Test
    public void uriWithParameterMediaType() throws IOException {
        // JSON always works (because it's "standard"), it's YAML (which is "custom") which did not
        var uri = URI.create("classpath:/picasso.yaml?context=classpath:/picasso-context.jsonld");
        var r = new ClasspathResource(uri);
        assertThat(r.mediaType()).isEqualTo(YamlMediaType.YAML_UTF_8);
    }
}

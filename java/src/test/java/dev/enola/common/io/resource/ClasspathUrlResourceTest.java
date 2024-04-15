/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2024 The Enola <https://enola.dev> Authors
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

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.net.MediaType.*;
import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTOBUF_TEXTPROTO_UTF_8;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTO_UTF_8;

import static org.junit.Assert.assertThrows;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.common.net.MediaType;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

public class ClasspathUrlResourceTest {

    private ReadableResource check(
            String name, MediaType expectedMediaType, Optional<Charset> expectedCharset)
            throws IOException {
        var resource = new UrlResource(Resources.getResource(name));
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
                PLAIN_TEXT_UTF_8.withCharset(Charsets.UTF_8),
                Optional.of(Charsets.UTF_8),
                "hello, world\n");
        checkText(
                "test-french.txt",
                PLAIN_TEXT_UTF_8.withCharset(Charsets.UTF_8),
                Optional.of(Charsets.UTF_8),
                "Ça va?\n");
        checkText(
                "test-emoji.txt",
                PLAIN_TEXT_UTF_8.withCharset(Charsets.UTF_8),
                Optional.of(Charsets.UTF_8),
                "🕵🏾‍♀️\n");

        var md = "# Markdown\n\n❤️\n";
        checkText(
                "test.md",
                MediaType.create("text", "markdown").withCharset(Charsets.UTF_8),
                Optional.of(Charsets.UTF_8),
                md);
        var resource = new UrlResource(Resources.getResource("test.md"), UTF_8);
        assertThat(resource.charSource().read()).isEqualTo(md);
    }
}

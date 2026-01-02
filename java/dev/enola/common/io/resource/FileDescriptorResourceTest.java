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

import static com.google.common.net.MediaType.OCTET_STREAM;
import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;
import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_16BE;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

public class FileDescriptorResourceTest {

    public @Rule SingletonRule r = $(MediaTypeProviders.set());

    @Test
    public void testSTDOUTwithoutCharsetNorMediaType() throws IOException {
        var FD1 = new FileDescriptorResource(URI.create("fd:1"));
        FD1.byteSink().write(new byte[] {1, 2, 3});
        assertThat(FD1.mediaType().charset()).hasValue(Charset.defaultCharset());
        assertThat(FD1.mediaType().withoutParameters()).isEqualTo(OCTET_STREAM.withoutParameters());
        FD1.charSink().write("hello");
    }

    @Test
    public void testSTDOUTwithCharsetWithoutMediaType() throws IOException {
        var FD1 = new FileDescriptorResource(URI.create("fd:1?charset=ASCII"));
        FD1.byteSink().write(new byte[] {1, 2, 3});
        FD1.charSink().write("hello");
        assertThat(FD1.mediaType().charset()).hasValue(US_ASCII);
    }

    @Test
    public void testSTDOUTwithMediaTypeWithoutCharset() throws IOException {
        var FD1 = new FileDescriptorResource(URI.create("fd:1?mediaType=application/yaml"));
        assertThat(FD1.mediaType()).isEqualTo(YAML_UTF_8);
    }

    @Test
    public void testSTDOUTwithMediaTypeWithCharsetInMediaType() throws IOException {
        var FD1 =
                new FileDescriptorResource(
                        URI.create("fd:1?mediaType=application/yaml;charset=utf-16be"));
        assertThat(FD1.mediaType()).isEqualTo(YAML_UTF_8.withCharset(UTF_16BE));
    }

    @Test
    public void testSTDOUTwithMediaTypeWithCharsetInSeparateQueryParameter() throws IOException {
        var FD1 =
                new FileDescriptorResource(
                        URI.create("fd:1?charset=ASCII&mediaType=application/yaml"));
        assertThat(FD1.mediaType()).isEqualTo(YAML_UTF_8.withCharset(US_ASCII));
    }

    @Test
    public void testSTDOUTwithMediaTypeWithCharsetInBoth() throws IOException {
        var FD1 =
                new FileDescriptorResource(
                        URI.create(
                                "fd:1?charset=ASCII&mediaType=application/yaml;charset=utf-16be"));
        assertThat(FD1.mediaType()).isEqualTo(YAML_UTF_8.withCharset(US_ASCII));
    }
}

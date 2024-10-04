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

import static com.google.common.net.MediaType.*;
import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;
import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTOBUF_TEXTPROTO_UTF_8;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTO_UTF_8;

import static java.net.URI.create;

import com.google.common.net.MediaType;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.mediatype.MediaTypesTest;
import dev.enola.common.io.mediatype.TestMediaType;
import dev.enola.common.io.mediatype.YamlMediaType;
import dev.enola.common.protobuf.ProtobufMediaTypes;

import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class MediaTypeDetectorTest {

    public @Rule SingletonRule r =
            $(
                    MediaTypeProviders.set(
                            new YamlMediaType(), new ProtobufMediaTypes(), new TestMediaType()));

    MediaTypeDetector md = new MediaTypeDetector();

    @Test
    public void testDetect() {
        var NADA = URI.create("nada:it");
        assertThat(md.detect(null, null, NADA)).isEqualTo(OCTET_STREAM);
        assertThat(md.detect("content/unknown", null, NADA)).isEqualTo(OCTET_STREAM);
        assertThat(md.detect("application/test", null, NADA))
                .isEqualTo(MediaType.parse("application/test"));
        assertThat(md.detect("application/test-alternative", null, NADA))
                .isEqualTo(MediaType.parse("application/test"));
        assertThat(md.detect("text/plain", "ascii", NADA))
                .isEqualTo(PLAIN_TEXT_UTF_8.withCharset(StandardCharsets.US_ASCII));

        assertThat(md.detect("application/octet-stream", null, create("hello.txt")))
                .isEqualTo(OCTET_STREAM);

        assertThat(md.detect(null, null, new File("hello.txt").toURI()))
                .isEqualTo(PLAIN_TEXT_UTF_8);
        assertThat(md.detect(null, null, new File("hello.json").toURI())).isEqualTo(JSON_UTF_8);

        assertThat(md.detect(null, null, create("bad-URI-without-scheme")))
                .isEqualTo(MediaType.OCTET_STREAM);
    }

    // TODO Rewrite all of above in this new style (to test the public API, instead of the
    // implementation)

    @Test
    public void emptyOctetStream() {
        assertThat(EmptyResource.INSTANCE.mediaType()).isEqualTo(OCTET_STREAM);
    }

    @Test // Test that TestMediaTypes was correctly registered
    public void testTest() {
        var r = new EmptyResource(create("whatever:something.test")); // drop charset!
        assertThat(r.mediaType()).isEqualTo(MediaTypesTest.TEST);
    }

    @Test
    public void testProto() {
        var r = new EmptyResource(new File("hello.proto").toURI()); // drop charset!
        assertThat(r.mediaType()).isEqualTo(PROTO_UTF_8);
    }

    @Test
    public void testTextproto() {
        var r = new EmptyResource(new File("hello.textproto").toURI()); // drop charset!
        assertThat(r.mediaType()).isEqualTo(PROTOBUF_TEXTPROTO_UTF_8);
    }

    @Test
    public void testTextYAML() {
        // A text/plain with *.yaml is still application/yaml
        var r =
                new EmptyResource(
                        create("http://server/hello.yaml"),
                        PLAIN_TEXT_UTF_8.withoutParameters()); // drop charset!

        assertThat(r.mediaType()).isEqualTo(YAML_UTF_8);
    }

    @Test
    public void testEmptyYAML() {
        // Empty .YAML is UTF-8
        var r = new EmptyResource(YamlMediaType.YAML_UTF_8.withoutParameters()); // drop charset!
        assertThat(r.mediaType()).isEqualTo(YAML_UTF_8);
    }

    @Test
    public void testNoHeaderYAML() throws IOException {
        // A .YAML without header and just some ASCII is still UTF-8
        var text = "hello: world";
        var r = new MemoryResource(YamlMediaType.YAML_UTF_8.withoutParameters()); // drop charset!
        r.byteSink().write(text.getBytes(StandardCharsets.US_ASCII));
        assertThat(r.mediaType()).isEqualTo(YAML_UTF_8);
    }

    @Test
    public void testURI() {
        var uri = create("file:/tmp/test/picasso.yaml?context=file:test/picasso-context.jsonld");
        var r = new EmptyResource(uri);
        assertThat(r.mediaType()).isEqualTo(YAML_UTF_8);
    }

    // TODO Add missing test coverage for the BOM detection from YamlMediaType
}

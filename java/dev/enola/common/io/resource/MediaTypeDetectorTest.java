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
import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTOBUF_TEXTPROTO_UTF_8;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTO_UTF_8;

import static java.net.URI.create;

import com.google.common.net.MediaType;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.*;
import dev.enola.common.protobuf.ProtobufMediaTypes;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class MediaTypeDetectorTest {

    public @Rule SingletonRule r =
            $(
                    MediaTypeProviders.set(
                            new YamlMediaType(),
                            new ProtobufMediaTypes(),
                            new TestMediaType(),
                            new StandardMediaTypes()));

    @Test
    public void testOverwrite() {
        var md = new MediaTypeDetector();
        var NADA = URI.create("nada:it");
        var UNKNOWN = parse("content/unknown");
        assertThat(md.overwrite(NADA, parse("application/test")))
                .isEqualTo(parse("application/test"));
        assertThat(md.overwrite(NADA, parse("text/plain").withCharset(StandardCharsets.US_ASCII)))
                .isEqualTo(PLAIN_TEXT_UTF_8.withCharset(StandardCharsets.US_ASCII));

        assertThat(md.overwrite(create("hello.txt"), parse("application/octet-stream")))
                .isEqualTo(PLAIN_TEXT_UTF_8);

        assertThat(md.overwrite(create("bad-URI-without-scheme"), OCTET_STREAM))
                .isEqualTo(OCTET_STREAM);
    }

    // TODO Rewrite all of above in this new style (to test the public API, instead implementation)

    @Test
    public void emptyOctetStream() {
        assertThat(EmptyResource.INSTANCE.mediaType()).isEqualTo(OCTET_STREAM);
    }

    @Test
    public void testTXT() {
        var r = new EmptyResource(create("whatever:hello.txt"));
        assertThat(r.mediaType()).isEqualTo(PLAIN_TEXT_UTF_8);
    }

    @Test
    public void testJSON() {
        var r = new EmptyResource(create("whatever:hello.json"));
        assertThat(r.mediaType()).isEqualTo(JSON_UTF_8);
    }

    @Test // Test that TestMediaTypes was correctly registered
    public void testTest() {
        var r = new EmptyResource(create("whatever:something.test")); // drop charset!
        assertThat(r.mediaType()).isEqualTo(MediaTypesTest.TEST);
    }

    @Test
    @Ignore // Intentionally not implemented; it's up to the caller to normalize()
    public void testTestAlternative() {
        MediaType TEST_ALT = parse("application/test-alternative");
        var r = new EmptyResource(create("whatever:something.test"), TEST_ALT);
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
    public void testYAML() {
        var r = new EmptyResource(create("http://server/hello.yaml"));
        assertThat(r.mediaType()).isEqualTo(YAML_UTF_8);
    }

    @Test
    @Ignore // TODO This is an invalid test... rewrite it elsewhere.
    // If a caller of an EmptyResource constructor says its TEXT, then it is that!
    // What this meant to test is that if a HTTP server says something is TEXT, then
    // that may be wrong, and we should detect if it may be YAML; but that goes elsewhere.
    public void testTextYAML() {
        // A text/plain with *.yaml is still application/yaml
        var r = new EmptyResource(create("http://server/hello.yaml"), PLAIN_TEXT_UTF_8);
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

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
package dev.enola.common.io.mediatype;

import static com.google.common.net.MediaType.*;
import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTOBUF_TEXTPROTO_UTF_8;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTO_UTF_8;

import static java.net.URI.create;

import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import dev.enola.common.io.resource.EmptyResource;
import dev.enola.common.io.resource.MemoryResource;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MediaTypeDetectorTest {
    MediaTypeDetector md = new MediaTypeDetector();

    @Test
    public void testDetect() {
        assertThat(md.detect(null, null, null)).isEqualTo(OCTET_STREAM);
        assertThat(md.detect("content/unknown", null, null)).isEqualTo(OCTET_STREAM);
        assertThat(md.detect("application/test", null, null))
                .isEqualTo(MediaType.parse("application/test"));
        assertThat(md.detect("application/test-alternative", null, null))
                .isEqualTo(MediaType.parse("application/test"));
        assertThat(md.detect("text/plain", "ascii", null))
                .isEqualTo(PLAIN_TEXT_UTF_8.withCharset(StandardCharsets.US_ASCII));

        assertThat(md.detect("text/plain", null, create("http://server/hello.yaml")))
                .isEqualTo(YAML_UTF_8);

        assertThat(md.detect("application/octet-stream", null, create("hello.txt")))
                .isEqualTo(OCTET_STREAM);

        assertThat(md.detect(null, null, new File("hello.txt").toURI()))
                .isEqualTo(PLAIN_TEXT_UTF_8);
        assertThat(md.detect(null, null, new File("hello.json").toURI())).isEqualTo(JSON_UTF_8);

        assertThat(md.detect(null, null, new File("hello.proto").toURI())).isEqualTo(PROTO_UTF_8);
        assertThat(md.detect(null, null, new File("hello.textproto").toURI()))
                .isEqualTo(PROTOBUF_TEXTPROTO_UTF_8);

        // Test that TestMediaTypes was correctly registered
        assertThat(md.detect(null, null, create("whatever:something.test")))
                .isEqualTo(MediaTypesTest.TEST);

        // TODO Assert.assertThrows() ?
        md.detect(null, null, create("bad-URI-without-scheme"));
    }

    // TODO Rewrite more from above in this new style (to test BOM sniffing)

    @Test
    public void testEmptyYML() {
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
        assertThat(md.detect(uri, ByteSource.empty())).isEqualTo(YAML_UTF_8);
    }

    // TODO Add mising test coverage for the BOM detection from YamlMediaType
}

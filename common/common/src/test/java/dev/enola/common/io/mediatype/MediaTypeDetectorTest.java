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

import com.google.common.base.Charsets;
import com.google.common.net.MediaType;

import org.junit.Test;

import java.io.File;
import java.net.URI;

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
                .isEqualTo(PLAIN_TEXT_UTF_8.withCharset(Charsets.US_ASCII));

        assertThat(md.detect("application/octet-stream", null, URI.create("hello.txt")))
                .isEqualTo(OCTET_STREAM);

        assertThat(md.detect(null, null, new File("hello.txt").toURI()))
                .isEqualTo(PLAIN_TEXT_UTF_8);
        assertThat(md.detect(null, null, new File("hello.json").toURI())).isEqualTo(JSON_UTF_8);
        assertThat(md.detect(null, null, new File("hello.yaml").toURI())).isEqualTo(YAML_UTF_8);

        assertThat(md.detect(null, null, new File("hello.proto").toURI())).isEqualTo(PROTO_UTF_8);
        assertThat(md.detect(null, null, new File("hello.textproto").toURI()))
                .isEqualTo(PROTOBUF_TEXTPROTO_UTF_8);

        // Assert.assertThrows()
        md.detect(null, null, URI.create("bad-URI-without-scheme"));
    }
}

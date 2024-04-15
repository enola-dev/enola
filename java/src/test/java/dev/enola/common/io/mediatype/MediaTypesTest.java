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

import static com.google.common.truth.Truth.assertThat;

import com.google.common.base.Charsets;
import com.google.common.net.MediaType;

import org.junit.Test;

public class MediaTypesTest {

    @Test
    public void testParse() {
        assertThat(MediaTypes.parse("application/test")).isEqualTo(TestMediaTypes.TEST);
    }

    @Test
    public void testNormalizeMediaTypesParse() {
        assertThat(MediaTypes.parse("application/test-alternative")).isEqualTo(TestMediaTypes.TEST);
    }

    @Test
    public void testNormalizeMediaTypeParse() {
        assertThat(MediaTypes.normalize(MediaType.parse("application/test-alternative")))
                .isEqualTo(TestMediaTypes.TEST);
    }

    @Test
    public void testNormalizeMediaTypesParseWithCharsetParameter() {
        var alternative = MediaTypes.parse("application/test-alternative");
        var alternativeWithCharset =
                MediaTypes.normalize(alternative.withCharset(Charsets.UTF_16BE));
        assertThat(alternativeWithCharset)
                .isEqualTo(TestMediaTypes.TEST.withCharset(Charsets.UTF_16BE));
    }

    @Test
    public void testNormalizeMediaTypeParseWithCharsetParameter() {
        var alternative = MediaType.parse("application/test-alternative");
        assertThat(MediaTypes.normalize(alternative)).isEqualTo(TestMediaTypes.TEST);
        assertThat(MediaTypes.normalize(alternative.withCharset(Charsets.UTF_16BE)))
                .isEqualTo(TestMediaTypes.TEST.withCharset(Charsets.UTF_16BE));
    }

    @Test
    public void testToString() {
        var mediaType = TestMediaTypes.TEST.withCharset(Charsets.UTF_16BE);
        assertThat(mediaType.toString()).isEqualTo("application/test; charset=utf-16be");
    }

    @Test
    public void testParseWithCharset() {
        var expected = TestMediaTypes.TEST.withCharset(Charsets.UTF_16BE);

        // https://www.ietf.org/rfc/rfc2045.txt format:
        assertThat(MediaTypes.parse("application/test-alternative; charset=utf-16be"))
                .isEqualTo(expected);

        // RFC-inspired format, but without that ugly space, works as well:
        assertThat(MediaTypes.parse("application/test-alternative;charset=utf-16be"))
                .isEqualTo(expected);
    }
}

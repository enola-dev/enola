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
package dev.enola.common.io.mediatype;

import static com.google.common.net.MediaType.JSON_UTF_8;
import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.net.MediaType;

import dev.enola.common.context.testlib.SingletonRule;

import org.junit.Rule;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class MediaTypesTest {

    public @Rule SingletonRule r = $(MediaTypeProviders.set(new TestMediaType()));

    @Test
    public void testParse() {
        assertThat(MediaTypes.parse("application/test")).isEqualTo(TEST);
    }

    @Test
    public void testNormalizeMediaTypesParse() {
        assertThat(MediaTypes.parse("application/test-alternative")).isEqualTo(TEST);
    }

    @Test
    public void testNormalizeMediaTypeParse() {
        assertThat(MediaTypes.normalize(MediaType.parse("application/test-alternative")))
                .isEqualTo(TEST);
    }

    @Test
    public void testNormalizeMediaTypesParseWithCharsetParameter() {
        var alternative = MediaTypes.parse("application/test-alternative");
        var alternativeWithCharset =
                MediaTypes.normalize(alternative.withCharset(StandardCharsets.UTF_16BE));
        assertThat(alternativeWithCharset).isEqualTo(TEST.withCharset(StandardCharsets.UTF_16BE));
    }

    @Test
    public void testNormalizeMediaTypeParseWithCharsetParameter() {
        var alternative = MediaType.parse("application/test-alternative");
        assertThat(MediaTypes.normalize(alternative)).isEqualTo(TEST);
        assertThat(MediaTypes.normalize(alternative.withCharset(StandardCharsets.UTF_16BE)))
                .isEqualTo(TEST.withCharset(StandardCharsets.UTF_16BE));
    }

    @Test
    public void testToString() {
        var mediaType = TEST.withCharset(StandardCharsets.UTF_16BE);
        assertThat(mediaType.toString()).isEqualTo("application/test; charset=utf-16be");
    }

    @Test
    public void testParseWithCharset() {
        var expected = TEST.withCharset(StandardCharsets.UTF_16BE);

        // https://www.ietf.org/rfc/rfc2045.txt format:
        assertThat(MediaTypes.parse("application/test-alternative; charset=utf-16be"))
                .isEqualTo(expected);

        // RFC-inspired format, but without that ugly space, works as well:
        assertThat(MediaTypes.parse("application/test-alternative;charset=utf-16be"))
                .isEqualTo(expected);
    }

    @Test
    public void toIRI() {
        assertThat(MediaTypes.toIRI(MediaType.PLAIN_TEXT_UTF_8.withoutParameters()))
                .isEqualTo("https://enola.dev/mediaType/text/plain");
        assertThat(MediaTypes.toIRI(MediaType.PLAIN_TEXT_UTF_8))
                .isEqualTo("https://enola.dev/mediaType/text/plain?charset=utf-8");
        assertThat(MediaTypes.toIRI(MediaType.PLAIN_TEXT_UTF_8.withParameter("x", "y")))
                .isEqualTo("https://enola.dev/mediaType/text/plain?charset=utf-8&x=y");
        assertThat(MediaTypes.toIRI(MediaType.parse("application/dita+xml; format=concept")))
                .isEqualTo("https://enola.dev/mediaType/application/dita/xml?format=concept");
    }

    @Test
    public void toStringWithoutSpaces() {
        assertThat(MediaTypes.toStringWithoutSpaces(MediaType.PLAIN_TEXT_UTF_8))
                .isEqualTo("text/plain;charset=utf-8");

        var mt2 = JSON_UTF_8.withParameter("page", "21").withParameter("x", "y");
        assertThat(MediaTypes.toStringWithoutSpaces(mt2))
                .isEqualTo("application/json;charset=utf-8;page=21;x=y");
    }

    // TODO use example/test instead of application/test (and rename accordingly everywhere...)
    // https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types#example

    public static final MediaType TEST = TestMediaType.TEST;

    @VisibleForTesting static final MediaType TEST_ALTERNATIVE = TestMediaType.TEST_ALTERNATIVE;
}

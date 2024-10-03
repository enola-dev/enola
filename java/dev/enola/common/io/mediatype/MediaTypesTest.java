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

import com.google.auto.service.AutoService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.net.MediaType;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

@AutoService(MediaTypeProvider.class)
public class MediaTypesTest implements MediaTypeProvider {

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

    // TODO use example/test instead of application/test (and rename accordingly everywhere...)
    // https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types#example

    public static final MediaType TEST = MediaType.create("application", "test");

    @VisibleForTesting
    static final MediaType TEST_ALTERNATIVE = MediaType.create("application", "test-alternative");

    @Override
    public Map<MediaType, Set<MediaType>> knownTypesWithAlternatives() {
        return ImmutableMap.of(TEST, Sets.newHashSet(TEST_ALTERNATIVE));
    }

    @Override
    public Multimap<String, MediaType> extensionsToTypes() {
        return ImmutableMultimap.of("test", TEST);
    }
}

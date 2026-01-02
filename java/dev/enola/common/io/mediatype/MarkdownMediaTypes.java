/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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

import static com.google.common.net.MediaType.create;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.net.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

/**
 * The "text/markdown" media type, as per <a href="https://www.rfc-editor.org/rfc/rfc7763.html">RFC
 * 7763</a> (and <a href="https://www.rfc-editor.org/rfc/rfc7764.html">RFC 7764</a>).
 */
public class MarkdownMediaTypes implements MediaTypeProvider {

    // TODO Distinguish https://commonmark.org from GFH et al. via a variant parameter; see
    // https://www.iana.org/assignments/markdown-variants/markdown-variants.xhtml

    private static final String VARIANT = "variant";

    // NB: Due to https://github.com/bazel-contrib/rules_jvm_external/issues/1343, we cannot
    //   directly use Guava's MediaType.MD_UTF_8 here! :( But working around is easy enough:
    public static final MediaType MARKDOWN_UTF_8 =
            MediaType.create("text", "markdown").withCharset(StandardCharsets.UTF_8);

    /**
     * <a href="https://www.iana.org/assignments/markdown-variants/CommonMark">CommonMark</a>
     * variant, see <a href="https://commonmark.org">CommonMark.org</a>.
     */
    public static final MediaType COMMON_MARKDOWN_UTF_8 =
            MARKDOWN_UTF_8.withParameter(VARIANT, "CommonMark");

    /**
     * <a href="https://www.iana.org/assignments/markdown-variants/GFM">GitHub Flavored Markdown
     * (GFM)</a>.
     */
    public static final MediaType GFM_MARKDOWN_UTF_8 = MARKDOWN_UTF_8.withParameter(VARIANT, "GFM");

    @Override
    public Multimap<String, MediaType> extensionsToTypes() {
        return ImmutableMultimap.of(".md", MARKDOWN_UTF_8);
    }

    @Override
    public Map<MediaType, Set<MediaType>> knownTypesWithAlternatives() {
        return ImmutableMap.of(MARKDOWN_UTF_8, ImmutableSet.of(create("text", "x-markdown")));
    }
}

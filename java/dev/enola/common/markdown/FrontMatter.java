/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.markdown;

import static dev.enola.common.jackson.ObjectMappers.YAML_LENIENT;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.google.common.annotations.VisibleForTesting;

import dev.enola.common.MoreStrings;

import org.commonmark.ext.front.matter.YamlFrontMatterVisitor;
import org.commonmark.node.Node;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/** Parsed Markdown YAML Front Matter metadata. */
record FrontMatter(
        @Nullable String title,
        @Nullable String description,
        @JsonAnySetter @JsonAnyGetter Map<String, Object> other) {

    @VisibleForTesting static final FrontMatter EMPTY = new FrontMatter(null, null, Map.of());

    FrontMatter {
        title = MoreStrings.trimToNull(title);
        description = MoreStrings.normalizeWhitespace(description);
    }

    /**
     * Extracts {@link FrontMatter} from a CommonMark {@link Node} document.
     *
     * <p>Always returns a non-null {@link FrontMatter} (possibly {@link #EMPTY}).
     */
    static FrontMatter from(@Nullable Node document) {
        if (document == null) return EMPTY;
        String rawFrontMatter = YamlFrontMatterVisitor.readRawContent(document);
        if (rawFrontMatter == null || rawFrontMatter.isBlank()) return EMPTY;
        try {
            var frontMatter = YAML_LENIENT.readValue(rawFrontMatter, FrontMatter.class);
            return frontMatter != null ? frontMatter : EMPTY;

        } catch (Exception e) {
            return EMPTY;
        }
    }

    /**
     * Extracts {@link FrontMatter} from raw Markdown text. Always returns a non-null {@link
     * FrontMatter} (possibly {@link #EMPTY}).
     */
    static FrontMatter from(@Nullable String markdown) {
        if (markdown == null || markdown.isBlank()) return EMPTY;
        Node document = Markdown.PARSER.parse(markdown);
        return from(document);
    }
}

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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

import static com.google.common.net.MediaType.HTML_UTF_8;

import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;

import com.google.common.collect.ImmutableMap;

import java.io.IOException;

/**
 * MarkdownResource is a {@link MultipartResource} which separates "Front Matter" (as {@link #FRONT}
 * part, typically YAML structured data) and Markdown content (as {@link #BODY}) from the base
 * resource.
 *
 * <p>The "frontmatter" is anything between 2 "---" lines at the very start of the file (if present,
 * it's optional) and the "body" is everything that follows. Any HTML comment before the frontmatter
 * is stripped (this is useful e.g. to ignore license headers).
 *
 * <p>This is very common de-facto standard format used by many Markdown tools. It may (TBC)
 * originally have been <a href="https://jekyllrb.com/docs/front-matter/">introduced by Jekyll</a>.
 */
public class MarkdownResource extends RegexMultipartResource {

    public static final String FIRST_COMMENT = "comment";
    public static final String FRONT = "frontmatter";
    public static final String BODY = "body";

    private static final PartsDef PARTS =
            new PartsDef(
                    "(?s)^(?<"
                            + FIRST_COMMENT
                            + "><!--.*-->)?(\r?\n)*(===\r?\n(?<"
                            + FRONT
                            + ">.*)===\r?\n)?(\r?\n)*(?<"
                            + BODY
                            + ">.*)$",
                    ImmutableMap.of(
                            FIRST_COMMENT,
                            HTML_UTF_8.withoutParameters(),
                            FRONT,
                            YAML_UTF_8.withoutParameters()));

    public MarkdownResource(ReadableResource baseResource) throws IOException {
        super(baseResource, PARTS);
    }
}

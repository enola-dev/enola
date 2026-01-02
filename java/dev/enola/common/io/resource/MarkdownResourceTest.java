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
package dev.enola.common.io.resource;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.io.mediatype.MarkdownMediaTypes.MARKDOWN_UTF_8;
import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;
import static dev.enola.common.io.resource.MarkdownResource.FRONT;
import static dev.enola.common.io.resource.MarkdownResource.MARKDOWN;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class MarkdownResourceTest {

    String COMMENT =
            """
            <!--
            SPDX-License-Identifier: Apache-2.0

            Copyright ...

            ...
            -->

            """;

    String FRONTMATTER =
            """
            ---
            title: First Blog post!
            ---

            """;

    String MD =
            """
            # Thaw Blough!

            **It rocks...**
            """;

    @Test
    public void commentFrontmatterMarkdown() throws IOException {
        var r = new MarkdownResource(StringResource.of(COMMENT + FRONTMATTER + MD, MARKDOWN_UTF_8));
        var f = r.part(FRONT);
        var b = r.part(MARKDOWN);

        assertThat(b.charSource().read()).isEqualTo("# Thaw Blough!\n\n**It rocks...**\n");
        assertThat(f.charSource().read()).isEqualTo("title: First Blog post!\n");

        assertThat(r.mediaType()).isEqualTo(MARKDOWN_UTF_8);
        assertThat(f.mediaType()).isEqualTo(YAML_UTF_8);
        assertThat(b.mediaType()).isEqualTo(MARKDOWN_UTF_8);

        // TODO FIXME Broken this when simplifying...
        // check(f.uri(), r.uri(), FRONT);
        // check(b.uri(), r.uri(), BODY);
    }

    private void check(URI uri, URI base, String fragment) {
        assertThat(uri.getFragment()).isEqualTo(fragment);
        assertThat(uri.toString()).startsWith(base.toString());
    }

    @Test
    public void frontmatterAndMarkdownButNoComment() throws IOException {
        var r = new MarkdownResource(StringResource.of(FRONTMATTER + MD, MARKDOWN_UTF_8));
        var f = r.part(FRONT);
        var b = r.part(MARKDOWN);

        assertThat(b.charSource().read()).isEqualTo("# Thaw Blough!\n\n**It rocks...**\n");
        assertThat(f.charSource().read()).isEqualTo("title: First Blog post!\n");
    }

    @Test
    public void onlyMarkdown() throws IOException {
        var r = new MarkdownResource(StringResource.of(MD, MARKDOWN_UTF_8));
        assertThat(r.part(MARKDOWN).charSource().read())
                .isEqualTo("# Thaw Blough!\n\n**It rocks...**\n");
        assertThat(r.part(FRONT).charSource().read()).isEmpty();
    }

    @Test
    public void onlyFrontmatter() throws IOException {
        var r = new MarkdownResource(StringResource.of(FRONTMATTER, MARKDOWN_UTF_8));
        assertThat(r.part(MARKDOWN).charSource().read()).isEmpty();
        assertThat(r.part(FRONT).charSource().read()).isEqualTo("title: First Blog post!\n");
    }

    @Test
    public void empty() throws IOException {
        var r = new MarkdownResource(new EmptyResource(MARKDOWN_UTF_8));
        assertThat(r.part(MARKDOWN).charSource().read()).isEmpty();
        assertThat(r.part(FRONT).charSource().read()).isEmpty();
        assertThat(r.parts()).hasSize(3);
    }
}

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

import static com.google.common.truth.Truth.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class MarkdownTest {

    @Test
    void md2mdGeneratesIndexesAndResolvesLinks(@TempDir Path tempDir) throws IOException {
        Path input = Files.createDirectory(tempDir.resolve("docs"));
        Path sub = Files.createDirectories(input.resolve("topic"));

        Files.writeString(
                sub.resolve("topic.md"),
                """
                ---
                type: Concept
                description: Overview of the topic.
                ---

                # Overarching Topic

                See details in [[detail]].
                """);
        Files.writeString(
                sub.resolve("detail.md"),
                """
                # Detail Page

                Reference back to [[topic]].
                """);
        Files.writeString(sub.resolve("image.svg"), "<svg></svg>");

        Path output = tempDir.resolve("out_md");
        Markdown.md2md(input, output);

        // Verify root index
        Path rootIndex = output.resolve("index.md");
        assertThat(Files.exists(rootIndex)).isTrue();
        String rootContent = Files.readString(rootIndex);
        assertThat(rootContent).contains("# Knowledge Wiki");
        assertThat(rootContent).contains("[Topic](topic/index.md)");

        // Verify sub index
        Path subIndex = output.resolve("topic/index.md");
        assertThat(Files.exists(subIndex)).isTrue();
        String subIndexContent = Files.readString(subIndex);
        assertThat(subIndexContent).contains("# Topic");
        assertThat(subIndexContent).contains("[Overarching Topic](topic.md)");
        assertThat(subIndexContent).contains("[Detail Page](detail.md)");

        // Verify resolved topic.md
        String topicMd = Files.readString(output.resolve("topic/topic.md"));
        assertThat(topicMd).contains("[Detail Page](detail.md)");

        // Verify resolved detail.md
        String detailMd = Files.readString(output.resolve("topic/detail.md"));
        assertThat(detailMd).contains("[Overarching Topic](topic.md)");

        // Verify asset copied
        assertThat(Files.exists(output.resolve("topic/image.svg"))).isTrue();
    }

    @Test
    void md2htmlGeneratesHtmlDirectlyFromSources(@TempDir Path tempDir) throws IOException {
        Path input = Files.createDirectory(tempDir.resolve("docs"));
        Path sub = Files.createDirectories(input.resolve("topic"));

        Files.writeString(
                sub.resolve("topic.md"),
                """
                ---
                type: Concept
                description: Overview of the topic.
                ---

                # Overarching Topic

                See details in [[detail]].
                """);
        Files.writeString(
                sub.resolve("detail.md"),
                """
                # Detail Page

                Reference back to [[topic]].
                """);
        Files.writeString(sub.resolve("image.svg"), "<svg></svg>");

        Path output = tempDir.resolve("out_html");
        String editBaseUrl = "https://github.com/enola-dev/wiki/edit/main/docs/";
        Markdown.md2html(input, output, editBaseUrl);

        // Verify wiki.css and wiki.js copied
        assertThat(Files.exists(output.resolve("wiki.css"))).isTrue();
        assertThat(Files.exists(output.resolve("wiki.js"))).isTrue();
        assertThat(Files.exists(output.resolve("code.js"))).isTrue();
        assertThat(Files.exists(output.resolve("mermaid.js"))).isTrue();

        // Verify resolved Markdown files co-located with HTML
        assertThat(Files.exists(output.resolve("index.md"))).isTrue();
        assertThat(Files.exists(output.resolve("topic/index.md"))).isTrue();
        assertThat(Files.exists(output.resolve("topic/topic.md"))).isTrue();
        assertThat(Files.exists(output.resolve("topic/detail.md"))).isTrue();

        // Verify root index.html
        Path rootIndex = output.resolve("index.html");
        assertThat(Files.exists(rootIndex)).isTrue();
        String rootContent = Files.readString(rootIndex);
        assertThat(rootContent).contains("<title>Knowledge Wiki</title>");
        assertThat(rootContent)
                .contains("<link rel=\"alternate\" type=\"text/markdown\" href=\"index.md\">");
        assertThat(rootContent).contains("<link rel=\"stylesheet\" href=\"wiki.css\">");
        assertThat(rootContent).contains("<script type=\"module\" src=\"wiki.js\"></script>");
        assertThat(rootContent).contains("if (location.protocol === 'file:')");
        assertThat(rootContent)
                .contains(
                        "<a class=\"action-button md-button\"" + " href=\"index.md\">Markdown</a>");
        assertThat(rootContent)
                .contains(
                        """
                        <a class="action-button edit-button"\
                         href="https://github.com/enola-dev/wiki/new/main/docs?filename=README.md"\
                         target="_blank">Edit</a>\
                        """);
        assertThat(rootContent).contains("<a href=\"topic/index.html\">Topic</a>");

        // Verify sub index.html
        Path subIndex = output.resolve("topic/index.html");
        assertThat(Files.exists(subIndex)).isTrue();
        String subIndexContent = Files.readString(subIndex);
        assertThat(subIndexContent).contains("<title>Topic</title>");
        assertThat(subIndexContent)
                .contains("<link rel=\"alternate\" type=\"text/markdown\" href=\"index.md\">");
        assertThat(subIndexContent).contains("<link rel=\"stylesheet\" href=\"../wiki.css\">");
        assertThat(subIndexContent)
                .contains("<script type=\"module\" src=\"../wiki.js\"></script>");
        assertThat(subIndexContent).contains("if (location.protocol === 'file:')");
        assertThat(subIndexContent)
                .contains(
                        "<a class=\"action-button md-button\"" + " href=\"index.md\">Markdown</a>");
        assertThat(subIndexContent)
                .contains(
                        """
                        <a class="action-button edit-button"\
                         href="https://github.com/enola-dev/wiki/new/main/docs/topic?filename=README.md"\
                         target="_blank">Edit</a>\
                        """);
        assertThat(subIndexContent).contains("<a href=\"topic.html\">Overarching Topic</a>");
        assertThat(subIndexContent).contains("<a href=\"detail.html\">Detail Page</a>");

        // Verify topic.html
        String topicHtml = Files.readString(output.resolve("topic/topic.html"));
        assertThat(topicHtml).contains("<title>Overarching Topic</title>");
        assertThat(topicHtml)
                .contains("<link rel=\"alternate\" type=\"text/markdown\" href=\"topic.md\">");
        assertThat(topicHtml).contains("<link rel=\"stylesheet\" href=\"../wiki.css\">");
        assertThat(topicHtml)
                .contains(
                        "<a class=\"action-button md-button\"" + " href=\"topic.md\">Markdown</a>");
        assertThat(topicHtml)
                .contains(
                        """
                        <a class="action-button edit-button"\
                         href="https://github.com/enola-dev/wiki/edit/main/docs/topic/topic.md"\
                         target="_blank">Edit</a>\
                        """);
        assertThat(topicHtml).contains("<nav class=\"breadcrumbs\" aria-label=\"Breadcrumb\">");
        assertThat(topicHtml).contains("<a href=\"../index.html\">Home</a>");
        assertThat(topicHtml).contains("<a href=\"index.html\">Overarching Topic</a>");
        assertThat(topicHtml).contains("<a href=\"detail.html\">Detail Page</a>");

        // Verify detail.html
        String detailHtml = Files.readString(output.resolve("topic/detail.html"));
        assertThat(detailHtml).contains("<title>Detail Page</title>");
        assertThat(detailHtml)
                .contains("<link rel=\"alternate\" type=\"text/markdown\" href=\"detail.md\">");
        assertThat(detailHtml).contains("<link rel=\"stylesheet\" href=\"../wiki.css\">");
        assertThat(detailHtml)
                .contains(
                        "<a class=\"action-button md-button\""
                                + " href=\"detail.md\">Markdown</a>");
        assertThat(detailHtml)
                .contains(
                        """
                        <a class="action-button edit-button"\
                         href="https://github.com/enola-dev/wiki/edit/main/docs/topic/detail.md"\
                         target="_blank">Edit</a>\
                        """);
        assertThat(detailHtml).contains("<nav class=\"breadcrumbs\" aria-label=\"Breadcrumb\">");
        assertThat(detailHtml).contains("<a href=\"../index.html\">Home</a>");
        assertThat(detailHtml).contains("<a href=\"index.html\">Overarching Topic</a>");
        assertThat(detailHtml).contains("<a href=\"topic.html\">Overarching Topic</a>");

        // Verify asset copied
        assertThat(Files.exists(output.resolve("topic/image.svg"))).isTrue();
    }

    @Test
    void invalidDirectoryRejects(@TempDir Path tempDir) throws IOException {
        Path file = Files.writeString(tempDir.resolve("test.txt"), "abc");
        assertThrows(
                IllegalArgumentException.class, () -> Markdown.md2md(file, tempDir.resolve("out")));
        assertThrows(
                IllegalArgumentException.class,
                () -> Markdown.md2html(file, tempDir.resolve("out"), "https://example.com/edit/"));
    }

    @Test
    void canonicalizeDirectoryInParallel(@TempDir Path tempDir) throws IOException {
        Path input = Files.createDirectory(tempDir.resolve("docs"));
        Path sub = Files.createDirectories(input.resolve("topic"));
        Path hiddenDir = Files.createDirectories(input.resolve(".hidden"));

        Path file1 = input.resolve("root.md");
        Path file2 = sub.resolve("sub.md");
        Path asset = sub.resolve("asset.txt");
        Path hiddenFile = hiddenDir.resolve("hidden.md");

        String messy1 = "# Root\n\n1. one\n1. two\n\n";
        String messy2 = "## Topic\n\n__bold__ text  \t\n\n\n";
        String assetContent = "not markdown";
        String hiddenContent = "# Hidden\n\n1. one\n";

        Files.writeString(file1, messy1);
        Files.writeString(file2, messy2);
        Files.writeString(asset, assetContent);
        Files.writeString(hiddenFile, hiddenContent);

        Markdown.canonicalize(input);

        assertThat(Files.readString(file1)).isEqualTo("# Root\n\n1. one\n2. two\n");
        assertThat(Files.readString(file2)).isEqualTo("## Topic\n\n**bold** text\n");
        assertThat(Files.readString(asset)).isEqualTo(assetContent);
        assertThat(Files.readString(hiddenFile)).isEqualTo(hiddenContent);
    }

    @Test
    void canonicalizeSingleFile(@TempDir Path tempDir) throws IOException {
        var md = "# Title\n\n1. one\n1. two\n";
        String result = new MarkdownCanonicalizer().canonicalize(md);
        assertThat(result).isEqualTo("# Title\n\n1. one\n2. two\n");
    }

    @Test
    void canonicalizeSingleFilePath(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("test.md");
        Files.writeString(file, "# Title\n\n1. one\n1. two\n");
        Markdown.canonicalize(file);
        assertThat(Files.readString(file)).isEqualTo("# Title\n\n1. one\n2. two\n");
    }

    @Test
    void canonicalizeNonExistentPathRejects(@TempDir Path tempDir) {
        Path nonExistent = tempDir.resolve("does-not-exist");
        assertThrows(IllegalArgumentException.class, () -> Markdown.canonicalize(nonExistent));
    }
}

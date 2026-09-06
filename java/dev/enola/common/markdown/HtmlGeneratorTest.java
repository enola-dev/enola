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

class HtmlGeneratorTest {

    private final HtmlGenerator generator = new HtmlGenerator();

    @Test
    void toHtmlBasic() {
        var html = generator.toHtml("# Hello World\n\nThis is a paragraph.\n");
        assertThat(html)
                .isEqualTo(
                        "<h1 id=\"hello-world\">Hello World</h1>\n<p>This is a paragraph.</p>\n");
    }

    @Test
    void toHtmlWithFrontMatter() {
        var md =
                """
                ---
                type: Software
                title: Ignore in body
                ---
                # Real Heading

                Content
                """;
        var html = generator.toHtml(md);
        assertThat(html).isEqualTo("<h1 id=\"real-heading\">Real Heading</h1>\n<p>Content</p>\n");
    }

    @Test
    void toHtmlTable() {
        var md =
                """
                | Header 1 | Header 2 |
                | :--- | :--- |
                | Cell 1 | Cell 2 |
                """;
        var html = generator.toHtml(md);
        assertThat(html).contains("<table>");
        assertThat(html).contains("<thead>");
        assertThat(html).contains("<th align=\"left\">Header 1</th>");
        assertThat(html).contains("<tbody>");
        assertThat(html).contains("<td align=\"left\">Cell 1</td>");
    }

    @Test
    void toHtmlAutolink() {
        var html = generator.toHtml("Visit https://example.com directly.");
        assertThat(html).contains("<a href=\"https://example.com\">https://example.com</a>");
    }

    @Test
    void toHtmlTaskListItems() {
        var md =
                """
                - [ ] Pending task
                - [x] Completed task
                """;
        var html = generator.toHtml(md);
        assertThat(html).contains("<li><input type=\"checkbox\" disabled=\"\"> Pending task</li>");
        assertThat(html)
                .contains(
                        "<li><input type=\"checkbox\" disabled=\"\" checked=\"\"> Completed"
                                + " task</li>");
    }

    @Test
    void toHtmlMermaidDiagram() {
        var md =
                """
                ```mermaid
                flowchart TD
                    A --> B
                ```
                """;
        var html = generator.toHtml(md);
        assertThat(html).contains("<pre class=\"mermaid\">flowchart TD\n    A --&gt; B\n</pre>");
    }

    @Test
    void toHtmlPageWithMermaidDiagram() {
        var md =
                """
                # Diagram

                ```mermaid
                graph LR
                    A --> B
                ```
                """;
        var html = generator.toHtmlPage(md, "Diagram", "wiki.css", null);
        assertThat(html).contains("<pre class=\"mermaid\">graph LR\n    A --&gt; B\n</pre>");
        assertThat(html).contains("<script type=\"module\" src=\"wiki.js\"></script>");
        assertThat(html).contains("if (location.protocol === 'file:')");
        assertThat(html).contains("Please serve this page over HTTP instead of file://");
    }

    @Test
    void toHtmlCodeSyntax() {
        var md =
                """
                ```java
                System.out.println("Hello");
                ```
                """;
        var html = generator.toHtml(md);
        assertThat(html)
                .contains(
                        "<pre><code"
                            + " class=\"language-java\">System.out.println(&quot;Hello&quot;);\n"
                            + "</code></pre>");
    }

    @Test
    void toHtmlLinkRewriting() {
        var md =
                """
                [Local](other.md)
                [Local Anchor](other.md#section)
                [Deep](sub/dir/test.markdown)
                [Anchor](#top)
                [External](https://example.com/other.md)
                [Image](pic.png)
                """;
        var html = generator.toHtml(md);
        assertThat(html).contains("<a href=\"other.html\">Local</a>");
        assertThat(html).contains("<a href=\"other.html#section\">Local Anchor</a>");
        assertThat(html).contains("<a href=\"sub/dir/test.html\">Deep</a>");
        assertThat(html).contains("<a href=\"#top\">Anchor</a>");
        assertThat(html).contains("<a href=\"https://example.com/other.md\">External</a>");
        assertThat(html).contains("<a href=\"pic.png\">Image</a>");
    }

    @Test
    void toHtmlPageFromHeading() {
        var html = generator.toHtmlPage("# Page Title\n\nBody content");
        assertThat(html)
                .isEqualTo(
                        """
                        <!DOCTYPE html>
                        <html lang="en">
                        <head>
                        <meta charset="utf-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Page Title</title>
                        </head>
                        <body>
                        <h1 id="page-title">Page Title</h1>
                        <p>Body content</p>
                        </body>
                        </html>
                        """);
    }

    @Test
    void toHtmlPageWithFrontMatterTitle() {
        var md =
                """
                ---
                title: Front Matter Title
                ---
                # Heading

                Content
                """;
        var html = generator.toHtmlPage(md);
        assertThat(html).contains("<title>Front Matter Title</title>");
    }

    @Test
    void toHtmlPageWithEscapedTitle() {
        var html = generator.toHtmlPage("# Tom & Jerry <Fun>");
        assertThat(html).contains("<title>Tom &amp; Jerry &lt;Fun&gt;</title>");
    }

    @Test
    void toHtmlPageEmpty() {
        var html = generator.toHtmlPage("");
        assertThat(html).contains("<!DOCTYPE html>");
        assertThat(html).contains("<title></title>");
    }

    @Test
    void toHtmlPageWithCssAndEditUrl() {
        var html =
                generator.toHtmlPage(
                        "# Page Title\n\nBody content",
                        "Page Title",
                        "../../wiki.css",
                        "https://github.com/enola-dev/wiki/edit/main/docs/topic/page.md");
        assertThat(html)
                .isEqualTo(
                        """
                        <!DOCTYPE html>
                        <html lang="en">
                        <head>
                        <meta charset="utf-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Page Title</title>
                        <link rel="stylesheet" href="../../wiki.css">
                        <script type="module" src="../../wiki.js"></script>
                        <script>
                          if (location.protocol === 'file:') {
                            window.addEventListener('DOMContentLoaded', () => {
                              document.querySelectorAll('pre.mermaid').forEach(el => {
                                const msg = document.createElement('p');
                                msg.className = 'mermaid-file-warning';
                                msg.textContent = 'Please serve this page over HTTP instead of file://';
                                el.replaceWith(msg);
                              });
                            });
                          }
                        </script>
                        </head>
                        <body>
                        <div class="header-actions">
                          <a class="action-button edit-button" href="https://github.com/enola-dev/wiki/edit/main/docs/topic/page.md" target="_blank">Edit</a>
                        </div>
                        <h1 id="page-title">Page Title</h1>
                        <p>Body content</p>
                        </body>
                        </html>
                        """);
    }

    @Test
    void toHtmlPageWithMarkdownLink() {
        var html =
                generator.toHtmlPage(
                        "# Page Title\n\nBody content",
                        "Page Title",
                        "../../wiki.css",
                        "https://github.com/enola-dev/wiki/edit/main/docs/topic/page.md",
                        null,
                        "page.md");
        assertThat(html)
                .isEqualTo(
                        """
                        <!DOCTYPE html>
                        <html lang="en">
                        <head>
                        <meta charset="utf-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Page Title</title>
                        <link rel="alternate" type="text/markdown" href="page.md">
                        <link rel="stylesheet" href="../../wiki.css">
                        <script type="module" src="../../wiki.js"></script>
                        <script>
                          if (location.protocol === 'file:') {
                            window.addEventListener('DOMContentLoaded', () => {
                              document.querySelectorAll('pre.mermaid').forEach(el => {
                                const msg = document.createElement('p');
                                msg.className = 'mermaid-file-warning';
                                msg.textContent = 'Please serve this page over HTTP instead of file://';
                                el.replaceWith(msg);
                              });
                            });
                          }
                        </script>
                        </head>
                        <body>
                        <div class="header-actions">
                          <a class="action-button md-button" href="page.md">Markdown</a>
                          <a class="action-button edit-button" href="https://github.com/enola-dev/wiki/edit/main/docs/topic/page.md" target="_blank">Edit</a>
                        </div>
                        <h1 id="page-title">Page Title</h1>
                        <p>Body content</p>
                        </body>
                        </html>
                        """);
    }

    @Test
    void generateDirectory(@TempDir Path tempDir) throws IOException {
        var inDir = Files.createDirectory(tempDir.resolve("in"));
        var subInDir = Files.createDirectory(inDir.resolve("sub"));
        var deepInDir = Files.createDirectory(subInDir.resolve("deep"));
        var outDir = tempDir.resolve("out");

        Files.writeString(inDir.resolve("index.md"), "# Home\n\nWelcome to [Sub](sub/doc.md)");
        Files.writeString(subInDir.resolve("doc.md"), "# Sub Doc\n\nDetails");
        Files.writeString(subInDir.resolve("asset.txt"), "plain text asset");
        Files.writeString(deepInDir.resolve("deep.md"), "# Deep Doc\n\nDeep details");

        var baseUrl = "https://github.com/enola-dev/wiki/edit/main/docs/";
        generator.generate(inDir, outDir, baseUrl);

        // wiki.css and wiki.js generated in output root
        assertThat(Files.exists(outDir.resolve("wiki.css"))).isTrue();
        assertThat(Files.readString(outDir.resolve("wiki.css"))).contains(".header-actions");
        assertThat(Files.readString(outDir.resolve("wiki.css"))).contains(".md-button");
        assertThat(Files.readString(outDir.resolve("wiki.css"))).contains(".edit-button");
        assertThat(Files.exists(outDir.resolve("wiki.js"))).isTrue();
        assertThat(Files.exists(outDir.resolve("code.js"))).isTrue();
        assertThat(Files.exists(outDir.resolve("mermaid.js"))).isTrue();

        // Resolved Markdown files co-located with HTML
        assertThat(Files.exists(outDir.resolve("index.md"))).isTrue();
        assertThat(Files.exists(outDir.resolve("sub/doc.md"))).isTrue();
        assertThat(Files.exists(outDir.resolve("sub/deep/deep.md"))).isTrue();

        // Root index (from source index.md)
        var indexHtml = Files.readString(outDir.resolve("index.html"));
        assertThat(indexHtml).contains("<title>Home</title>");
        assertThat(indexHtml)
                .contains("<link rel=\"alternate\" type=\"text/markdown\" href=\"index.md\">");
        assertThat(indexHtml).contains("<link rel=\"stylesheet\" href=\"wiki.css\">");
        assertThat(indexHtml).contains("<script type=\"module\" src=\"wiki.js\"></script>");
        assertThat(indexHtml).contains("if (location.protocol === 'file:')");
        assertThat(indexHtml)
                .contains(
                        "<a class=\"action-button md-button\"" + " href=\"index.md\">Markdown</a>");
        assertThat(indexHtml)
                .contains(
                        "<a class=\"action-button edit-button\""
                            + " href=\"https://github.com/enola-dev/wiki/edit/main/docs/index.md\""
                            + " target=\"_blank\">Edit</a>");
        assertThat(indexHtml).contains("<a href=\"sub/doc.html\">Sub</a>");

        // Sub document
        var docHtml = Files.readString(outDir.resolve("sub/doc.html"));
        assertThat(docHtml).contains("<title>Sub Doc</title>");
        assertThat(docHtml)
                .contains("<link rel=\"alternate\" type=\"text/markdown\" href=\"doc.md\">");
        assertThat(docHtml).contains("<link rel=\"stylesheet\" href=\"../wiki.css\">");
        assertThat(docHtml).contains("<script type=\"module\" src=\"../wiki.js\"></script>");
        assertThat(docHtml).contains("if (location.protocol === 'file:')");
        assertThat(docHtml)
                .contains("<a class=\"action-button md-button\"" + " href=\"doc.md\">Markdown</a>");
        assertThat(docHtml)
                .contains(
                        "<a class=\"action-button edit-button\""
                            + " href=\"https://github.com/enola-dev/wiki/edit/main/docs/sub/doc.md\""
                            + " target=\"_blank\">Edit</a>");
        assertThat(docHtml).contains("<nav class=\"breadcrumbs\" aria-label=\"Breadcrumb\">");
        assertThat(docHtml).contains("<a href=\"../index.html\">Home</a>");
        assertThat(docHtml).contains("<a href=\"index.html\">Sub</a>");
        assertThat(docHtml).contains("<h1 id=\"sub-doc\">Sub Doc</h1>");

        // Deep document
        var deepHtml = Files.readString(outDir.resolve("sub/deep/deep.html"));
        assertThat(deepHtml).contains("<title>Deep Doc</title>");
        assertThat(deepHtml)
                .contains("<link rel=\"alternate\" type=\"text/markdown\" href=\"deep.md\">");
        assertThat(deepHtml).contains("<link rel=\"stylesheet\" href=\"../../wiki.css\">");
        assertThat(deepHtml).contains("<script type=\"module\" src=\"../../wiki.js\"></script>");
        assertThat(deepHtml).contains("if (location.protocol === 'file:')");
        assertThat(deepHtml)
                .contains(
                        "<a class=\"action-button md-button\"" + " href=\"deep.md\">Markdown</a>");
        assertThat(deepHtml)
                .contains(
                        "<a class=\"action-button edit-button\""
                            + " href=\"https://github.com/enola-dev/wiki/edit/main/docs/sub/deep/deep.md\""
                            + " target=\"_blank\">Edit</a>");
        assertThat(deepHtml).contains("<nav class=\"breadcrumbs\" aria-label=\"Breadcrumb\">");
        assertThat(deepHtml).contains("<a href=\"../../index.html\">Home</a>");
        assertThat(deepHtml).contains("<a href=\"../index.html\">Sub</a>");
        assertThat(deepHtml).contains("<span class=\"breadcrumb-separator\">&gt;</span>");
        assertThat(deepHtml).contains("<a href=\"index.html\">Deep Doc</a>");

        // Asset copied
        var assetTxt = Files.readString(outDir.resolve("sub/asset.txt"));
        assertThat(assetTxt).isEqualTo("plain text asset");
    }

    @Test
    void breadcrumbsMultiLevelHierarchy(@TempDir Path tempDir) throws IOException {
        var inDir = Files.createDirectory(tempDir.resolve("docs"));
        var compDir = Files.createDirectories(inDir.resolve("computer"));
        var aiDir = Files.createDirectories(compDir.resolve("ai"));
        var swDir = Files.createDirectories(aiDir.resolve("software"));
        var outDir = tempDir.resolve("out");

        Files.writeString(compDir.resolve("index.md"), "# Computer\n\nComputer overview");
        Files.writeString(aiDir.resolve("index.md"), "# Artificial Intelligence\n\nAI category");
        Files.writeString(swDir.resolve("index.md"), "# Software\n\nSoftware overview");
        Files.writeString(
                swDir.resolve("antigravity.md"), "# Antigravity\n\nAgentic AI coding assistant");

        var baseUrl = "https://github.com/enola-dev/wiki/edit/main/docs/";
        generator.generate(inDir, outDir, baseUrl);

        // Concept page antigravity.html
        var antiHtml = Files.readString(outDir.resolve("computer/ai/software/antigravity.html"));
        assertThat(antiHtml).contains("<nav class=\"breadcrumbs\" aria-label=\"Breadcrumb\">");
        assertThat(antiHtml).contains("<a href=\"../../../index.html\">Home</a>");
        assertThat(antiHtml).contains("<a href=\"../../index.html\">Computer</a>");
        assertThat(antiHtml).contains("<a href=\"../index.html\">Artificial Intelligence</a>");
        assertThat(antiHtml).contains("<a href=\"index.html\">Software</a>");
        // Verify breadcrumbs are before H1
        int breadcrumbsIdx = antiHtml.indexOf("class=\"breadcrumbs\"");
        int h1Idx = antiHtml.indexOf("<h1 id=\"antigravity\">Antigravity</h1>");
        assertThat(breadcrumbsIdx).isLessThan(h1Idx);

        // Category index page computer/ai/software/index.html (should have Home > Computer > AI
        // parents)
        var swIndexHtml = Files.readString(outDir.resolve("computer/ai/software/index.html"));
        assertThat(swIndexHtml).contains("<nav class=\"breadcrumbs\" aria-label=\"Breadcrumb\">");
        assertThat(swIndexHtml).contains("<a href=\"../../../index.html\">Home</a>");
        assertThat(swIndexHtml).contains("<a href=\"../../index.html\">Computer</a>");
        assertThat(swIndexHtml).contains("<a href=\"../index.html\">Artificial Intelligence</a>");
        // Should not link to software itself in breadcrumbs
        assertThat(swIndexHtml).doesNotContain("<a href=\"index.html\">Software</a>");

        // Top level index computer/index.html should have Home breadcrumb
        var compIndexHtml = Files.readString(outDir.resolve("computer/index.html"));
        assertThat(compIndexHtml).contains("<nav class=\"breadcrumbs\" aria-label=\"Breadcrumb\">");
        assertThat(compIndexHtml).contains("<a href=\"../index.html\">Home</a>");
    }

    @Test
    void resolveEditUrlSynthesizedCategoryNewFileLink(@TempDir Path tempDir) throws IOException {
        var inDir = Files.createDirectory(tempDir.resolve("in"));
        Files.createDirectory(inDir.resolve("auto_cat"));
        var baseUrl = "https://github.com/enola-dev/wiki/edit/main/docs/";

        // File is an index.md but neither README.md nor index.md exists on disk
        var url = HtmlGenerator.resolveEditUrl(baseUrl, Path.of("auto_cat/index.md"), inDir);
        assertThat(url)
                .isEqualTo(
                        "https://github.com/enola-dev/wiki/new/main/docs/auto_cat?filename=README.md");

        // Root synthesized index
        var rootUrl = HtmlGenerator.resolveEditUrl(baseUrl, Path.of("index.md"), inDir);
        assertThat(rootUrl)
                .isEqualTo("https://github.com/enola-dev/wiki/new/main/docs?filename=README.md");
    }

    @Test
    void generateDirectoryRejectsNonDirectory(@TempDir Path tempDir) throws IOException {
        var file = Files.writeString(tempDir.resolve("file.txt"), "hello");
        var outDir = tempDir.resolve("out");
        assertThrows(
                IllegalArgumentException.class,
                () -> generator.generate(file, outDir, "https://example.com/edit/"));
    }

    @Test
    void markdownMd2HtmlDelegates(@TempDir Path tempDir) throws IOException {
        var inDir = Files.createDirectory(tempDir.resolve("in"));
        var outDir = tempDir.resolve("out");
        Files.writeString(inDir.resolve("test.md"), "# Test\n\nContent");
        Markdown.md2html(inDir, outDir, "https://github.com/enola-dev/wiki/edit/main/docs/");
        assertThat(Files.readString(outDir.resolve("test.html"))).contains("<title>Test</title>");
        assertThat(Files.readString(outDir.resolve("test.html")))
                .contains("<link rel=\"alternate\" type=\"text/markdown\" href=\"test.md\">");
        assertThat(Files.readString(outDir.resolve("test.html")))
                .contains("class=\"action-button md-button\"");
        assertThat(Files.readString(outDir.resolve("test.html")))
                .contains("class=\"action-button edit-button\"");
        assertThat(Files.exists(outDir.resolve("test.md"))).isTrue();
        assertThat(Files.readString(outDir.resolve("test.md"))).contains("# Test\n\nContent");
    }
}

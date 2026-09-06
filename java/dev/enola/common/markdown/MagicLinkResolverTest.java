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

import com.google.common.net.MediaType;

import dev.enola.common.io.http.client.HttpGetter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

class MagicLinkResolverTest {

    private static final HttpGetter EMPTY_HTTP =
            url -> new HttpGetter.Response(MediaType.OCTET_STREAM, Stream.empty());

    @Test
    void resolveMagicalLinks() {
        var inputMdFiles =
                Map.of(
                        Path.of("one.md"),
                                """
                                # One
                                This is a [[two]] link.

                                Classical link: [two](two.md).
                                """,
                        Path.of("two.md"),
                                """
                                # Two
                                Another [[sub/three]] link.

                                [[bad]] link!
                                """,
                        Path.of("sub/three.md"),
                                """
                                # Three
                                No more links.\
                                """);
        var expectedOutputMdFiles =
                Map.of(
                        Path.of("one.md"),
                        """
                        # One
                        This is a [Two](two.md) link.

                        Classical link: [two](two.md).
                        """,
                        Path.of("two.md"),
                        """
                        # Two
                        Another [Three](sub/three.md) link.

                        [bad](bad.md) link!
                        """,
                        Path.of("sub/three.md"),
                        """
                        # Three
                        No more links.\
                        """);

        var actualOutputMdFiles = new MagicLinkResolver(EMPTY_HTTP).resolve(inputMdFiles);
        assertThat(actualOutputMdFiles).isEqualTo(expectedOutputMdFiles);
    }

    @Test
    void explicitLabelAndPipeTrick() {
        var input =
                Map.of(
                        Path.of("page.md"),
                                """
                                # Page
                                Explicit: [[other|Custom Name]]
                                Pipe trick: [[other|]]
                                Anchor: [[other#section]]
                                Local anchor: [[#heading]]
                                Inside inline code: `[[other]]`
                                Inside fenced code:
                                ```
                                [[other]]
                                ```
                                """,
                        Path.of("other.md"),
                                """
                                # Other Page
                                Content.
                                """);

        var expected =
                Map.of(
                        Path.of("page.md"),
                        """
                        # Page
                        Explicit: [Custom Name](other.md)
                        Pipe trick: [Other Page](other.md)
                        Anchor: [Other Page](other.md#section)
                        Local anchor: [#heading](#heading)
                        Inside inline code: `[[other]]`
                        Inside fenced code:
                        ```
                        [[other]]
                        ```
                        """,
                        Path.of("other.md"),
                        """
                        # Other Page
                        Content.
                        """);

        var actual = new MagicLinkResolver(EMPTY_HTTP).resolve(input);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void httpFetcherStub() {
        HttpGetter fakeHttp =
                url -> {
                    if ("https://example.com".equals(url)) {
                        return new HttpGetter.Response(
                                MediaType.HTML_UTF_8,
                                Stream.of(
                                        "<!DOCTYPE html>",
                                        "<html>",
                                        "<head><title>Example Domain</title></head>",
                                        "<body><p>Test</p></body>",
                                        "</html>"));
                    } else if ("https://example.com/remote.md".equals(url)) {
                        return new HttpGetter.Response(
                                MediaType.MD_UTF_8,
                                Stream.of(
                                        "# Remote Markdown Doc",
                                        "",
                                        "This is remote markdown content."));
                    } else if ("https://example.com/api/page-without-extension".equals(url)) {
                        return new HttpGetter.Response(
                                MediaType.HTML_UTF_8,
                                Stream.of("<head><title>Page Without Extension</title></head>"));
                    } else if ("https://example.com/api/doc-without-extension".equals(url)) {
                        return new HttpGetter.Response(
                                MediaType.MD_UTF_8,
                                Stream.of("# Doc Without Extension", "", "Some content."));
                    }
                    return new HttpGetter.Response(MediaType.OCTET_STREAM, Stream.empty());
                };

        var input =
                Map.of(
                        Path.of("index.md"),
                        """
                        # Index
                        Check [[https://example.com]] and [[https://example.com|Custom Domain]].
                        Remote MD: [[https://example.com/remote.md]] and [[https://example.com/remote.md|Custom MD]].
                        No ext HTML: [[https://example.com/api/page-without-extension]].
                        No ext MD: [[https://example.com/api/doc-without-extension]].
                        """);

        var expected =
                Map.of(
                        Path.of("index.md"),
                        """
                        # Index
                        Check [Example Domain](https://example.com) and [Custom Domain](https://example.com).
                        Remote MD: [Remote Markdown Doc](https://example.com/remote.md) and [Custom MD](https://example.com/remote.md).
                        No ext HTML: [Page Without Extension](https://example.com/api/page-without-extension).
                        No ext MD: [Doc Without Extension](https://example.com/api/doc-without-extension).
                        """);

        var actual = new MagicLinkResolver(fakeHttp).resolve(input);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void directoryProcessing(@TempDir Path tempDir) throws IOException {
        Path inputDir = tempDir.resolve("input");
        Path outputDir = tempDir.resolve("output");
        Files.createDirectories(inputDir.resolve("sub"));

        Files.writeString(
                inputDir.resolve("one.md"),
                """
                # Page One
                Link to [[two]].
                """);
        Files.writeString(
                inputDir.resolve("two.md"),
                """
                # Page Two
                Link to [[sub/three]].
                """);
        Files.writeString(
                inputDir.resolve("sub/three.md"),
                """
                # Page Three
                End.
                """);
        // Non-markdown asset file
        Files.writeString(inputDir.resolve("sub/asset.txt"), "hello non-md asset");
        Files.writeString(
                inputDir.resolve("sub/four.markdown"),
                """
                # Page Four
                Link to [[../one]].
                """);

        new MagicLinkResolver(EMPTY_HTTP).resolve(inputDir, outputDir);

        assertThat(Files.readString(outputDir.resolve("one.md")))
                .isEqualTo(
                        """
                        # Page One
                        Link to [Page Two](two.md).
                        """);
        assertThat(Files.readString(outputDir.resolve("two.md")))
                .isEqualTo(
                        """
                        # Page Two
                        Link to [Page Three](sub/three.md).
                        """);
        assertThat(Files.readString(outputDir.resolve("sub/three.md")))
                .isEqualTo(
                        """
                        # Page Three
                        End.
                        """);
        assertThat(Files.readString(outputDir.resolve("sub/four.markdown")))
                .isEqualTo(
                        """
                        # Page Four
                        Link to [Page One](../one.md).
                        """);
        assertThat(Files.readString(outputDir.resolve("sub/asset.txt")))
                .isEqualTo("hello non-md asset");
    }

    @Test
    void resolveMagicalLinksWithMarkdownExtension() {
        var inputMdFiles =
                Map.of(
                        Path.of("one.markdown"),
                        """
                        # One
                        Link to [[two.markdown]].
                        Link to [[sub/three.markdown]].
                        """,
                        Path.of("two.markdown"),
                        """
                        # Two
                        Content.
                        """,
                        Path.of("sub/three.markdown"),
                        """
                        Content without heading.
                        """);
        var expectedOutputMdFiles =
                Map.of(
                        Path.of("one.markdown"),
                        """
                        # One
                        Link to [Two](two.markdown).
                        Link to [three](sub/three.markdown).
                        """,
                        Path.of("two.markdown"),
                        """
                        # Two
                        Content.
                        """,
                        Path.of("sub/three.markdown"),
                        """
                        Content without heading.
                        """);

        var actualOutputMdFiles = new MagicLinkResolver(EMPTY_HTTP).resolve(inputMdFiles);
        assertThat(actualOutputMdFiles).isEqualTo(expectedOutputMdFiles);
    }

    @Test
    void resolveMagicalLinksWithYamlFrontmatter() {
        var inputMdFiles =
                Map.of(
                        Path.of("index.md"),
                        """
                        # Index
                        Link to [[cognee]].
                        Link to [[with-comment]].
                        Link to [[with-dots]].
                        Link to [[no-heading]].
                        Link to [[leading-newlines]].
                        """,
                        Path.of("cognee.md"),
                        """
                        ---
                        type: Software
                        resource: https://www.cognee.ai
                        generated: { by: reference_agent/gemini-3.7-flash, at: 2026-08-23T15:41:05Z }
                        tags:
                          - ai
                          - memory
                        sources:
                          - resource: https://docs.cognee.ai/
                        ---

                        # Cognee AI Memory

                        Cognee is...
                        """,
                        Path.of("with-comment.md"),
                        """
                        ---
                        # This is a comment in frontmatter
                        title: frontmatter-title
                        ---
                        # Real Heading

                        Body.
                        """,
                        Path.of("with-dots.md"),
                        """
                        ---
                        type: Concept
                        ...
                        # Dot Ended Frontmatter

                        Body.
                        """,
                        Path.of("no-heading.md"),
                        """
                        ---
                        type: Software
                        ---
                        Body without any heading.
                        """,
                        Path.of("leading-newlines.md"),
                        """

                        ---
                        type: Software
                        ---
                        # Heading After Newlines

                        Body.
                        """);

        var expectedOutputMdFiles =
                Map.of(
                        Path.of("index.md"),
                        """
                        # Index
                        Link to [Cognee AI Memory](cognee.md).
                        Link to [Real Heading](with-comment.md).
                        Link to [Dot Ended Frontmatter](with-dots.md).
                        Link to [no-heading](no-heading.md).
                        Link to [Heading After Newlines](leading-newlines.md).
                        """,
                        Path.of("cognee.md"),
                        """
                        ---
                        type: Software
                        resource: https://www.cognee.ai
                        generated: { by: reference_agent/gemini-3.7-flash, at: 2026-08-23T15:41:05Z }
                        tags:
                          - ai
                          - memory
                        sources:
                          - resource: https://docs.cognee.ai/
                        ---

                        # Cognee AI Memory

                        Cognee is...
                        """,
                        Path.of("with-comment.md"),
                        """
                        ---
                        # This is a comment in frontmatter
                        title: frontmatter-title
                        ---
                        # Real Heading

                        Body.
                        """,
                        Path.of("with-dots.md"),
                        """
                        ---
                        type: Concept
                        ...
                        # Dot Ended Frontmatter

                        Body.
                        """,
                        Path.of("no-heading.md"),
                        """
                        ---
                        type: Software
                        ---
                        Body without any heading.
                        """,
                        Path.of("leading-newlines.md"),
                        """

                        ---
                        type: Software
                        ---
                        # Heading After Newlines

                        Body.
                        """);

        var actualOutputMdFiles = new MagicLinkResolver(EMPTY_HTTP).resolve(inputMdFiles);
        assertThat(actualOutputMdFiles).isEqualTo(expectedOutputMdFiles);
    }

    @Test
    void httpFetcherWithYamlFrontmatter() {
        HttpGetter fakeHttp =
                url -> {
                    if ("https://example.com/remote-with-frontmatter.md".equals(url)) {
                        return new HttpGetter.Response(
                                MediaType.MD_UTF_8,
                                Stream.of(
                                        "---",
                                        "type: Software",
                                        "# Comment in YAML",
                                        "---",
                                        "",
                                        "# Remote Doc Title",
                                        "",
                                        "Content."));
                    } else if ("https://example.com/remote-fallback-frontmatter".equals(url)) {
                        return new HttpGetter.Response(
                                MediaType.OCTET_STREAM,
                                Stream.of(
                                        "---",
                                        "type: Software",
                                        "---",
                                        "# Fallback Doc Title",
                                        "Content."));
                    }
                    return new HttpGetter.Response(MediaType.OCTET_STREAM, Stream.empty());
                };

        var input =
                Map.of(
                        Path.of("index.md"),
                        """
                        # Index
                        Link: [[https://example.com/remote-with-frontmatter.md]]
                        Fallback: [[https://example.com/remote-fallback-frontmatter]]
                        """);

        var expected =
                Map.of(
                        Path.of("index.md"),
                        """
                        # Index
                        Link: [Remote Doc Title](https://example.com/remote-with-frontmatter.md)
                        Fallback: [Fallback Doc Title](https://example.com/remote-fallback-frontmatter)
                        """);

        var actual = new MagicLinkResolver(fakeHttp).resolve(input);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void magicalLinksInTable() {
        var input =
                Map.of(
                        Path.of("index.md"),
                        """
                        # Index

                        | Item | Target |
                        | :--- | :--- |
                        | First | [[item1]] |
                        | Second | [[item2\\|Custom Item 2]] |
                        """,
                        Path.of("item1.md"),
                        """
                        # Item 1
                        Details 1
                        """,
                        Path.of("item2.md"),
                        """
                        # Item 2
                        Details 2
                        """);

        var expected =
                Map.of(
                        Path.of("index.md"),
                        """
                        # Index

                        | Item | Target |
                        | :--- | :--- |
                        | First | [Item 1](item1.md) |
                        | Second | [Custom Item 2](item2.md) |
                        """,
                        Path.of("item1.md"),
                        """
                        # Item 1
                        Details 1
                        """,
                        Path.of("item2.md"),
                        """
                        # Item 2
                        Details 2
                        """);

        var actual = new MagicLinkResolver(EMPTY_HTTP).resolve(input);
        assertThat(actual).isEqualTo(expected);
    }
}

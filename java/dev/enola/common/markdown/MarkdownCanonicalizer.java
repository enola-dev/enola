/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.markdown.MarkdownRenderer;

/**
 * A utility class for canonicalizing Markdown content. Canonicalization involves standardizing
 * various formatting aspects to ensure consistency, leveraging an existing Markdown parser.
 *
 * @author Google Gemini 2.5 Flash on 2025-07-19
 */
class MarkdownCanonicalizer {

    private final Parser parser;
    private final MarkdownRenderer renderer;

    MarkdownCanonicalizer() {
        // Initialize the CommonMark parser and Markdown renderer.
        // The parser converts Markdown text into an Abstract Syntax Tree (AST).
        // The renderer converts the AST back into Markdown text, applying
        // CommonMark's standard formatting, which inherently canonicalizes many aspects.
        this.parser = Parser.builder().build();
        this.renderer = MarkdownRenderer.builder().build();
    }

    /**
     * Canonicalizes a given Markdown string by leveraging a CommonMark parser and renderer. This
     * process automatically handles many formatting inconsistencies (e.g., line endings, heading
     * spacing, list item spacing) by parsing to an AST and rendering back to a standardized
     * Markdown format. Additional regex-based canonicalization is applied for rules not covered by
     * the default renderer's output.
     *
     * @param markdownContent The original Markdown content as a string.
     * @return The canonicalized Markdown content.
     */
    public String canonicalize(String markdownContent) {
        if (markdownContent == null || markdownContent.isEmpty()) {
            return "";
        }

        // 1. Parse the Markdown content into an Abstract Syntax Tree (AST).
        Node document = parser.parse(markdownContent);

        // 2. Render the AST back to Markdown. The CommonMark renderer
        // will automatically normalize many aspects, such as:
        //    - Line endings to Unix-style (\n)
        //    - Consistent heading spacing (e.g., "# Heading")
        //    - Consistent list item spacing (e.g., "- Item")
        //    - Standardizing bold/italic markers (e.g., **bold** instead of __bold__)
        String canonicalized = renderer.render(document);

        // 3. Apply additional RegEx-based canonicalization for rules
        // that might not be fully covered or are specific preferences
        // beyond the CommonMark renderer's default output.

        // Remove trailing whitespace from each line:
        // (CommonMark renderer usually handles this, but good to double check.)
        canonicalized = canonicalized.replaceAll("(?m)[ \\t]+$", "");

        // Reduce multiple consecutive blank lines to a single blank line.
        // CommonMark renderer might leave more than one blank line between blocks,
        // so this ensures strict single blank lines.
        // Three or more newlines to two newlines (one blank line):
        canonicalized = canonicalized.replaceAll("\n{3,}", "\n\n");

        // Ensure a single blank line at the end of the document if it's not empty,
        // or remove it if it's excessive. Remove leading/trailing whitespace including blank lines
        // Ensure a single trailing newline if content exists:
        canonicalized = canonicalized.trim();
        if (!canonicalized.isEmpty()) {
            canonicalized += "\n";
        }

        return canonicalized;
    }
}

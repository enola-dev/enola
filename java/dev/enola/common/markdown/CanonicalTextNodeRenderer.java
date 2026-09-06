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

import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.markdown.MarkdownNodeRendererContext;
import org.commonmark.renderer.markdown.MarkdownWriter;
import org.commonmark.text.AsciiMatcher;
import org.commonmark.text.CharMatcher;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A custom {@link NodeRenderer} for {@link Text} nodes that avoids over-eager escaping of
 * characters like {@code &} and {@code >} in inline contexts, while still preserving required
 * escapes (e.g. for HTML entities or blockquote markers at line starts).
 */
class CanonicalTextNodeRenderer implements NodeRenderer {

    private static final Pattern ENTITY_PATTERN =
            Pattern.compile("&(?:[a-zA-Z][a-zA-Z0-9]{1,31}|#[0-9]{1,7}|#[xX][0-9a-fA-F]{1,6});");

    private final MarkdownWriter writer;
    private final AsciiMatcher textEscape;
    private final AsciiMatcher textEscapeInHeading;

    CanonicalTextNodeRenderer(MarkdownNodeRendererContext context) {
        this.writer = context.getWriter();
        this.textEscape =
                AsciiMatcher.builder()
                        .anyOf("[]<`*_\n\\")
                        .anyOf(context.getSpecialCharacters())
                        .build();
        this.textEscapeInHeading = AsciiMatcher.builder(textEscape).anyOf("#").build();
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        return Set.of(Text.class);
    }

    @Override
    public void render(Node node) {
        if (!(node instanceof Text text)) {
            return;
        }

        String literal = text.getLiteral();
        if (literal == null || literal.isEmpty()) {
            return;
        }

        CharMatcher escape =
                (node.getParent() instanceof Heading) ? textEscapeInHeading : textEscape;

        renderText(literal, escape);
    }

    private void renderText(String text, CharMatcher escape) {
        String[] lines = text.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                writer.line();
            }
            renderLine(lines[i], escape);
        }
    }

    private void renderLine(String line, CharMatcher escape) {
        if (line.isEmpty()) {
            return;
        }

        int start = 0;
        // If the line starts with '>', escape it so it doesn't create an accidental blockquote
        if (line.startsWith(">")) {
            writer.raw("\\>");
            start = 1;
        }

        Matcher matcher = ENTITY_PATTERN.matcher(line);
        while (matcher.find(start)) {
            if (matcher.start() > start) {
                writer.text(line.substring(start, matcher.start()), escape);
            }
            // Escape the ampersand of an entity reference literal so CommonMark doesn't decode it
            writer.raw("\\&");
            writer.text(line.substring(matcher.start() + 1, matcher.end()), escape);
            start = matcher.end();
        }

        if (start < line.length()) {
            writer.text(line.substring(start), escape);
        }
    }
}

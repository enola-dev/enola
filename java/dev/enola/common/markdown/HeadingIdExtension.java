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

import org.commonmark.Extension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.markdown.MarkdownNodeRendererContext;
import org.commonmark.renderer.markdown.MarkdownNodeRendererFactory;
import org.commonmark.renderer.markdown.MarkdownRenderer;

import java.util.Set;

public final class HeadingIdExtension
        implements Parser.ParserExtension,
                HtmlRenderer.HtmlRendererExtension,
                MarkdownRenderer.MarkdownRendererExtension {

    private HeadingIdExtension() {}

    public static Extension create() {
        return new HeadingIdExtension();
    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.postProcessor(new HeadingIdPostProcessor());
    }

    @Override
    public void extend(HtmlRenderer.Builder rendererBuilder) {
        rendererBuilder.nodeRendererFactory(context -> new HeadingIdHtmlNodeRenderer());
        rendererBuilder.attributeProviderFactory(context -> new HeadingIdAttributeProvider());
    }

    @Override
    public void extend(MarkdownRenderer.Builder rendererBuilder) {
        rendererBuilder.nodeRendererFactory(
                new MarkdownNodeRendererFactory() {
                    @Override
                    public NodeRenderer create(MarkdownNodeRendererContext context) {
                        return new HeadingIdMarkdownNodeRenderer(context);
                    }

                    @Override
                    public Set<Character> getSpecialCharacters() {
                        return Set.of();
                    }
                });
    }
}

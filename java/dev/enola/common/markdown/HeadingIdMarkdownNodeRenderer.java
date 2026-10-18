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

import org.commonmark.node.Node;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.markdown.MarkdownNodeRendererContext;
import org.commonmark.renderer.markdown.MarkdownWriter;

import java.util.Set;

class HeadingIdMarkdownNodeRenderer implements NodeRenderer {

    private final MarkdownWriter writer;

    HeadingIdMarkdownNodeRenderer(MarkdownNodeRendererContext context) {
        this.writer = context.getWriter();
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        return Set.of(HeadingIdNode.class);
    }

    @Override
    public void render(Node node) {
        if (node instanceof HeadingIdNode headingIdNode) {
            writer.raw(" {#" + headingIdNode.getId() + "}");
        }
    }
}

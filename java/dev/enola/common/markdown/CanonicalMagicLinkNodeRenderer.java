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

import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TableBody;
import org.commonmark.ext.gfm.tables.TableCell;
import org.commonmark.ext.gfm.tables.TableHead;
import org.commonmark.ext.gfm.tables.TableRow;
import org.commonmark.node.Node;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.markdown.MarkdownNodeRendererContext;
import org.commonmark.renderer.markdown.MarkdownWriter;

import java.util.Set;

class CanonicalMagicLinkNodeRenderer implements NodeRenderer {

    private final MarkdownWriter writer;

    CanonicalMagicLinkNodeRenderer(MarkdownNodeRendererContext context) {
        this.writer = context.getWriter();
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        return Set.of(MagicLinkNode.class);
    }

    @Override
    public void render(Node node) {
        if (node instanceof MagicLinkNode magicLinkNode) {
            String raw = magicLinkNode.getRaw();
            if (isInsideTable(node)) {
                raw = raw.replace("\\|", "|").replace("|", "\\|");
            }
            writer.raw("[[" + raw + "]]");
        }
    }

    private static boolean isInsideTable(Node node) {
        for (Node current = node.getParent(); current != null; current = current.getParent()) {
            if (current instanceof TableBlock
                    || current instanceof TableCell
                    || current instanceof TableRow
                    || current instanceof TableHead
                    || current instanceof TableBody) {
                return true;
            }
        }
        return false;
    }
}

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
import org.commonmark.renderer.markdown.MarkdownNodeRendererFactory;
import org.commonmark.renderer.markdown.MarkdownRenderer;
import org.commonmark.renderer.markdown.MarkdownWriter;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A custom {@link NodeRenderer} that renders {@link TableBlock} nodes with aligned, pretty-printed
 * columns and delimiter rows according to column alignments.
 */
class CanonicalTableNodeRenderer implements NodeRenderer {

    private final MarkdownWriter writer;
    private final MarkdownRenderer inlineRenderer;

    CanonicalTableNodeRenderer(MarkdownNodeRendererContext context) {
        this.writer = context.getWriter();
        this.inlineRenderer =
                MarkdownRenderer.builder()
                        .extensions(Markdown.RENDERER_EXTENSIONS)
                        .nodeRendererFactory(
                                new MarkdownNodeRendererFactory() {
                                    @Override
                                    public NodeRenderer create(
                                            MarkdownNodeRendererContext context) {
                                        return new CanonicalTextNodeRenderer(context);
                                    }

                                    @Override
                                    public Set<Character> getSpecialCharacters() {
                                        return Set.of();
                                    }
                                })
                        .nodeRendererFactory(
                                new MarkdownNodeRendererFactory() {
                                    @Override
                                    public NodeRenderer create(
                                            MarkdownNodeRendererContext context) {
                                        return new CanonicalMagicLinkNodeRenderer(context);
                                    }

                                    @Override
                                    public Set<Character> getSpecialCharacters() {
                                        return Set.of();
                                    }
                                })
                        .build();
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        return Set.of(
                TableBlock.class,
                TableHead.class,
                TableBody.class,
                TableRow.class,
                TableCell.class);
    }

    @Override
    public void render(Node node) {
        if (node instanceof TableBlock tableBlock) {
            renderTable(tableBlock);
        }
        // TableHead, TableBody, TableRow, TableCell are handled by renderTable
    }

    private void renderTable(TableBlock tableBlock) {
        var data = extractTableData(tableBlock);
        if (data.columnCount == 0) {
            return;
        }

        int[] colWidths = new int[data.columnCount];
        for (int col = 0; col < data.columnCount; col++) {
            int maxLen = 3;
            for (var row : data.headerRows) {
                if (col < row.size()) {
                    maxLen = Math.max(maxLen, row.get(col).length());
                }
            }
            for (var row : data.bodyRows) {
                if (col < row.size()) {
                    maxLen = Math.max(maxLen, row.get(col).length());
                }
            }
            colWidths[col] = maxLen;
        }

        writer.line();

        for (var headerRow : data.headerRows) {
            renderRow(headerRow, colWidths, data.alignments);
        }

        renderDelimiterRow(colWidths, data.alignments);

        for (var bodyRow : data.bodyRows) {
            renderRow(bodyRow, colWidths, data.alignments);
        }

        writer.block();
    }

    private void renderRow(
            List<String> rowCells,
            int[] colWidths,
            List<TableCell.@Nullable Alignment> alignments) {
        writer.raw("|");
        for (int col = 0; col < colWidths.length; col++) {
            var cell = (col < rowCells.size()) ? rowCells.get(col) : "";
            var alignment = (col < alignments.size()) ? alignments.get(col) : null;
            writer.raw(" " + padCell(cell, colWidths[col], alignment) + " |");
        }
        writer.line();
    }

    private void renderDelimiterRow(
            int[] colWidths, List<TableCell.@Nullable Alignment> alignments) {
        writer.raw("|");
        for (int col = 0; col < colWidths.length; col++) {
            var alignment = (col < alignments.size()) ? alignments.get(col) : null;
            writer.raw(" " + buildDelimiter(colWidths[col], alignment) + " |");
        }
        writer.line();
    }

    private String buildDelimiter(int width, TableCell.@Nullable Alignment alignment) {
        if (alignment == TableCell.Alignment.LEFT) {
            return ":" + "-".repeat(Math.max(2, width - 1));
        } else if (alignment == TableCell.Alignment.RIGHT) {
            return "-".repeat(Math.max(2, width - 1)) + ":";
        } else if (alignment == TableCell.Alignment.CENTER) {
            return ":" + "-".repeat(Math.max(1, width - 2)) + ":";
        } else {
            return "-".repeat(Math.max(3, width));
        }
    }

    private String padCell(String content, int width, TableCell.@Nullable Alignment alignment) {
        int pad = Math.max(0, width - content.length());
        if (alignment == TableCell.Alignment.RIGHT) {
            return " ".repeat(pad) + content;
        } else if (alignment == TableCell.Alignment.CENTER) {
            int leftPad = pad / 2;
            int rightPad = pad - leftPad;
            return " ".repeat(leftPad) + content + " ".repeat(rightPad);
        } else {
            return content + " ".repeat(pad);
        }
    }

    private TableData extractTableData(TableBlock tableBlock) {
        var data = new TableData();
        for (Node child = tableBlock.getFirstChild(); child != null; child = child.getNext()) {
            if (child instanceof TableHead head) {
                for (Node rowNode = head.getFirstChild();
                        rowNode != null;
                        rowNode = rowNode.getNext()) {
                    if (rowNode instanceof TableRow row) {
                        data.headerRows.add(collectRow(row, data));
                    }
                }
            } else if (child instanceof TableBody body) {
                for (Node rowNode = body.getFirstChild();
                        rowNode != null;
                        rowNode = rowNode.getNext()) {
                    if (rowNode instanceof TableRow row) {
                        data.bodyRows.add(collectRow(row, data));
                    }
                }
            } else if (child instanceof TableRow row) {
                data.bodyRows.add(collectRow(row, data));
            }
        }

        while (data.alignments.size() < data.columnCount) {
            data.alignments.add(null);
        }

        return data;
    }

    private List<String> collectRow(TableRow row, TableData data) {
        List<String> rowCells = new ArrayList<>();
        int col = 0;
        for (Node cellNode = row.getFirstChild(); cellNode != null; cellNode = cellNode.getNext()) {
            if (cellNode instanceof TableCell cell) {
                rowCells.add(renderCell(cell));
                if (col >= data.alignments.size()) {
                    data.alignments.add(cell.getAlignment());
                }
                col++;
            }
        }
        data.columnCount = Math.max(data.columnCount, rowCells.size());
        return rowCells;
    }

    private String renderCell(TableCell cell) {
        var sb = new StringBuilder();
        for (Node child = cell.getFirstChild(); child != null; child = child.getNext()) {
            sb.append(inlineRenderer.render(child));
        }
        return sb.toString().replace("\r", "").replace("\n", " ").trim();
    }

    private static class TableData {
        final List<List<String>> headerRows = new ArrayList<>();
        final List<List<String>> bodyRows = new ArrayList<>();
        final List<TableCell.@Nullable Alignment> alignments = new ArrayList<>();
        int columnCount = 0;
    }
}

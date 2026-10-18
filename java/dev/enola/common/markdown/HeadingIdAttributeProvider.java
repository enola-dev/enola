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

import org.commonmark.ext.heading.anchor.IdGenerator;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Code;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.renderer.html.AttributeProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

class HeadingIdAttributeProvider implements AttributeProvider {

    private final Map<String, Integer> idCounts;
    private final Set<String> assignedIds;

    HeadingIdAttributeProvider() {
        this.idCounts = new HashMap<>();
        this.assignedIds = new HashSet<>();
    }

    @Override
    public void setAttributes(Node node, String tagName, Map<String, String> attributes) {
        if (node instanceof Heading heading) {
            HeadingIdNode headingIdNode = findHeadingIdNode(heading);
            if (headingIdNode != null) {
                attributes.put("id", assignId(headingIdNode.getId()));
            } else {
                String headingText = extractHeadingText(heading);
                String baseSlug = IdGenerator.builder().build().generateId(headingText);
                attributes.put("id", assignId(baseSlug));
            }
        }
    }

    private String assignId(String rawId) {
        String base = rawId.toLowerCase(Locale.ROOT);
        if (base.isEmpty()) {
            base = "id";
        }
        if (assignedIds.add(base)) {
            idCounts.put(base, 1);
            return base;
        }
        int count = idCounts.getOrDefault(base, 1);
        String candidate;
        do {
            candidate = base + "-" + count;
            count++;
        } while (!assignedIds.add(candidate));
        idCounts.put(base, count);
        return candidate;
    }

    static HeadingIdNode findHeadingIdNode(Heading heading) {
        for (Node child = heading.getFirstChild(); child != null; child = child.getNext()) {
            if (child instanceof HeadingIdNode headingIdNode) {
                return headingIdNode;
            }
        }
        return null;
    }

    private static String extractHeadingText(Heading heading) {
        final List<String> wordList = new ArrayList<>();
        heading.accept(
                new AbstractVisitor() {
                    @Override
                    public void visit(Text text) {
                        wordList.add(text.getLiteral());
                    }

                    @Override
                    public void visit(Code code) {
                        wordList.add(code.getLiteral());
                    }
                });

        StringBuilder sb = new StringBuilder();
        for (String word : wordList) {
            sb.append(word);
        }
        return sb.toString().trim().toLowerCase(Locale.ROOT);
    }
}

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

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.PostProcessor;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HeadingIdPostProcessor implements PostProcessor {

    static final Pattern HEADING_ID_PATTERN =
            Pattern.compile("(?:\\s+|^)\\{#([a-zA-Z0-9_.:-]+)\\}\\s*$");

    @Override
    public Node process(Node node) {
        node.accept(
                new AbstractVisitor() {
                    @Override
                    public void visit(Heading heading) {
                        Node lastChild = heading.getLastChild();
                        if (lastChild instanceof Text textNode) {
                            String literal = textNode.getLiteral();
                            Matcher matcher = HEADING_ID_PATTERN.matcher(literal);
                            if (matcher.find()) {
                                String id = matcher.group(1).toLowerCase(Locale.ROOT);
                                String newLiteral =
                                        literal.substring(0, matcher.start()).stripTrailing();
                                if (newLiteral.isEmpty()) {
                                    textNode.unlink();
                                } else {
                                    textNode.setLiteral(newLiteral);
                                }
                                heading.appendChild(new HeadingIdNode(id));
                            }
                        }
                        super.visit(heading);
                    }
                });
        return node;
    }
}

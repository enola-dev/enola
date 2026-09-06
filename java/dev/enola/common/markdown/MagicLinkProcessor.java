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
import org.commonmark.node.Text;
import org.commonmark.parser.InlineParserContext;
import org.commonmark.parser.beta.LinkInfo;
import org.commonmark.parser.beta.LinkProcessor;
import org.commonmark.parser.beta.LinkResult;
import org.commonmark.parser.beta.Scanner;

class MagicLinkProcessor implements LinkProcessor {
    @Override
    public LinkResult process(LinkInfo linkInfo, Scanner scanner, InlineParserContext context) {
        Node prev = linkInfo.openingBracket().getPrevious();
        if (prev instanceof Text prevText
                && prevText.getLiteral().endsWith("[")
                && scanner.hasNext()
                && scanner.peek() == ']') {
            scanner.next(); // consume the second ']'
            if ("[".equals(prevText.getLiteral())) {
                prevText.unlink();
            } else {
                prevText.setLiteral(
                        prevText.getLiteral().substring(0, prevText.getLiteral().length() - 1));
            }
            String raw = linkInfo.text();
            MagicLinkNode node = new MagicLinkNode(raw);
            return LinkResult.replaceWith(node, scanner.position());
        }
        return LinkResult.none();
    }
}

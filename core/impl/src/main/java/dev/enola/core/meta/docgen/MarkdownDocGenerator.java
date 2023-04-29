/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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
package dev.enola.core.meta.docgen;

import com.google.common.base.Strings;

import dev.enola.core.IDs;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.ID;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class MarkdownDocGenerator {

    // TODO Convert this to using the (currently un-used) markdown.soy

    public static final String FOOTER =
            "_This model documentation was generated with ❤️ by"
                    + " [Enola.dev](https://www.enola.dev)_\n";

    private final Options options;

    public MarkdownDocGenerator(Options options) {
        this.options = options;
    }

    public void render(EntityKindRepository kinds, Appendable md) throws IOException {
        md.append("# Models\n");
        if (options.diagram.equals(Options.DiagramType.Mermaid)) {
            MermaidGenerator.renderMermaid(kinds, md);
        } else if (options.diagram.equals(Options.DiagramType.Graphviz)) {
            GraphvizGenerator.renderGraphviz(kinds, md);
        }
        for (var ek : kinds.list()) {
            render(ek, md);
        }
        md.append("\n---\n");
        md.append(FOOTER);
    }

    private void render(EntityKind ek, Appendable md) throws IOException {
        ID idWithoutPathArguments = IDs.withoutPath(ek.getId());
        String fqn = IDs.toPath(idWithoutPathArguments);
        List<String> pathArguments =
                ek.getId().getPathsList().stream().collect(Collectors.toList());

        md.append("\n## ");
        if (!Strings.isNullOrEmpty(ek.getEmoji())) {
            md.append(ek.getEmoji() + " ");
        }
        md.append("`" + fqn + "`");
        if (!Strings.isNullOrEmpty(ek.getLabel())) {
            md.append(" (" + ek.getLabel() + ")");
        }
        if (!Strings.isNullOrEmpty(ek.getLogoUrl())) {
            // TODO How / where to format the logo? Maybe just put it on a new line, AFTER heading?
            md.append("![Logo](" + ek.getLogoUrl() + ")");
        }
        md.append(" <a name=\"" + fqn + "\"></a>");
        md.append("\n\n");
        for (var pathArgument : pathArguments) {
            md.append("* " + pathArgument + "\n");
        }
        if (!Strings.isNullOrEmpty(ek.getDocUrl())) {
            md.append('\n');
            // TODO It would be cool to read MD and "inline" the first phrase...
            md.append("[See documentation...](" + ek.getDocUrl() + ")\n");
        }
        if (ek.getRelatedCount() > 0) {
            md.append('\n');
            md.append("### Related Entities\n\n");
            for (var related : ek.getRelatedMap().entrySet()) {
                var key = related.getKey();
                var label = related.getValue().getLabel();
                var description = related.getValue().getDescription();
                var id = related.getValue().getId();
                var idPath = IDs.toPath(id);
                md.append(
                        "* `"
                                + key
                                + "` _"
                                + label
                                + "_ ⇒ ["
                                + id.getEntity()
                                + "](#"
                                + idPath
                                + ")");
                if (!Strings.isNullOrEmpty(description)) {
                    md.append(" (" + description + ")");
                }
                md.append('\n');
            }
        }
        if (ek.getLinkCount() > 0) {
            md.append('\n');
            md.append("### Links\n\n");
            for (var link : ek.getLinkMap().entrySet()) {
                var key = link.getKey();
                var label = link.getValue().getLabel();
                var description = link.getValue().getDescription();
                var uri = link.getValue().getUriTemplate();
                md.append("* `" + key + "` _" + label + "_ ⇝ <" + uri + ">");
                if (!Strings.isNullOrEmpty(description)) {
                    md.append(" (" + description + ")");
                }
                md.append('\n');
            }
        }
    }
}

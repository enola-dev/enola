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
package dev.enola.core.docgen;

import com.google.common.base.Strings;

import dev.enola.core.IDs;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.ID;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class MarkdownDocGenerator { // TODO extends SoyGenerator {

    public static final String FOOTER =
            "_This model documentation was generated with ❤️ by"
                    + " [Enola.dev](https://www.enola.dev)_\n";

    private final Options options;

    // Using Protobufs in https://github.com/google/closure-templates (AKA "Soy"
    // was not straightforward, see https://github.com/google/closure-templates/issues/1300.
    // For now, this is therefore implemented with a sweet good old big fat StringBuffer#append()
    // TODO Possibly (re)convert this to using the (currently un-used) markdown.soy maybe later.

    public MarkdownDocGenerator(Options options) {
        // super("dev/enola/core/docgen/markdown.soy", "dev.enola.markdown.package");
        this.options = options;
    }
    /*
        @Override
        protected Iterable<? extends Descriptors.GenericDescriptor> protoDescriptors() {
            return ImmutableSet.of(ID.getDescriptor());
        }
    */
    public void render(EntityKindRepository kinds, Appendable md) throws IOException {
        // Map<String, ?> data = ImmutableMap.of("package", "test", "kinds",
        // kinds.list().stream().map(ek -> ek.getId()).collect(Collectors.toSet()));
        // renderer.setData(data).renderText(md).assertDone();

        md.append("# Models\n");
        if (options.diagram.equals(Options.DiagramType.Mermaid)) {
            MermaidGenerator.renderMermaid(kinds, md);
        } else if (options.diagram.equals(Options.DiagramType.Graphviz)) {
            throw new UnsupportedOperationException(
                    "TODO Please contribute https://github.com/enola-dev/enola/issues/97");
        }
        for (var ek : kinds.list()) {
            render(ek, md);
        }
        md.append("\n---\n");
        md.append(FOOTER);
    }

    private void render(EntityKind ek, Appendable md) throws IOException {
        ID idWithoutPathArguments = ID.newBuilder(ek.getId()).clearPaths().build();
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

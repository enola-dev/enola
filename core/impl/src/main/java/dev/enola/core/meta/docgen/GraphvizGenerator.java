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

import dev.enola.core.IDs;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.ID;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

class GraphvizGenerator {
    static void renderGraphviz(EntityKindRepository kinds, Appendable md) throws IOException {
        md.append("```\n");
        md.append("graphviz\n");
        md.append("digraph {\n");
        md.append("  graph [fontname = \"Handlee\"];\n");
        md.append("  node [fontname = \"Handlee\"];\n");
        md.append("  edge [fontname = \"Handlee\"];\n");
        for (var ek : kinds.list()) {
            renderGraphvizEntity(ek, md);
        }
        md.append("}\n");
        md.append("```\n");
    }

    // TODO: improve this quick and dirty first version
    private static void renderGraphvizEntity(EntityKind ek, Appendable md) throws IOException {
        ID idWithoutPathArguments = IDs.withoutPath(ek.getId());
        String fqn = IDs.toPath(idWithoutPathArguments);
        String name = StringUtil.capitalize(idWithoutPathArguments.getEntity());

        List<String> pathArguments =
                ek.getId().getPathsList().stream().collect(Collectors.toList());

        md.append("  \"" + name + "\"[\n");
        md.append("    fillcolor=\"#88ff0022\"\n");
        md.append(
                "    label=<<table border=\"0\" cellborder=\"1\" cellspacing=\"0\""
                        + " cellpadding=\"3\">\n");
        md.append("        <tr> <td port=\"name\" sides=\"ltr\"> <b>" + name + "</b></td> </tr>\n");
        // TODO add description
        // md.append("<tr> <td port="description" sides="ltr"> <b>"+name+"</b></td> </tr>")

        for (var pathArgument : pathArguments) {
            // TODO hardcoded emoji
            md.append(
                    "        <tr> <td port=\""
                            + pathArgument
                            + "\" align=\"left\"><br align=\"left\"/>"
                            + "&#129409;"
                            + pathArgument
                            + "<br align=\"left\"/></td> </tr>\n");
        }
        for (var linkKey : ek.getLinkMap().keySet()) {
            // TODO hardcoded emoji
            md.append(
                    "        <tr> <td port=\""
                            + linkKey
                            + "\" align=\"left\"><br align=\"left\"/>"
                            + "&#129409;"
                            + linkKey
                            + "<br align=\"left\"/></td> </tr>\n");
        }
        md.append("    </table>>\n");
        md.append("    shape=plain\n");
        md.append("  ]\n");

        for (var related : ek.getRelatedMap().entrySet()) {
            var key = related.getKey();
            var id = related.getValue().getId();
            md.append(
                    "    "
                            + "\""
                            + name
                            + "\":\""
                            + key
                            + "\""
                            + " -> "
                            + "\""
                            + StringUtil.capitalize(id.getEntity())
                            + "\":name"
                            + "[dir=forward label=\""
                            + key
                            + "\" color=\"#00440088\"];"
                            + "\n");
        }
    }
}

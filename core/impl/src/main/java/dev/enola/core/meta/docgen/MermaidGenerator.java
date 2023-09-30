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
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.ID;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

class MermaidGenerator {
    static void renderMermaid(Iterable<EntityKind> kinds, Appendable md) throws IOException {
        md.append("\n``` mermaid\nclassDiagram\n  direction RL\n");
        for (var ek : kinds) {
            renderMermaidEntity(ek, md);
        }
        md.append("```\n");
    }

    private static void renderMermaidEntity(EntityKind ek, Appendable md) throws IOException {
        ID idWithoutPathArguments = IDs.withoutPath(ek.getId());
        String fqn = IDs.toPath(idWithoutPathArguments);
        String name = StringUtil.capitalize(idWithoutPathArguments.getEntity());
        List<String> pathArguments =
                ek.getId().getPathsList().stream().collect(Collectors.toList());

        // This is kinda wrong... but "good enough" for v1; see
        // https://github.com/enola-dev/enola/issues/74. Later, we'll
        // https://github.com/enola-dev/enola/issues/89.
        md.append("  class " + name + "{\n");
        for (var pathArgument : pathArguments) {
            // Unicode for https://emojipedia.org/id-button/
            md.append("    \uD83C\uDD94 " + pathArgument + "\n");
        }
        for (var linkKey : ek.getLinkMap().keySet()) {
            // Unicode for https://emojipedia.org/link/
            md.append("    \uD83D\uDD17 " + linkKey + "\n");
        }
        // TODO Verbs? Like "drain()" etc.
        md.append("  }\n");

        md.append("  link " + name + " \"#" + fqn + "\"\n");

        for (var related : ek.getRelatedMap().entrySet()) {
            var key = related.getKey();
            var id = related.getValue().getId();
            // TODO Permit other kind of relationships, read from tags in Model; e.g. *-- instead --
            // https://github.com/enola-dev/enola/issues/91
            md.append(
                    "  "
                            + name
                            + " -- "
                            + StringUtil.capitalize(id.getEntity())
                            + " : "
                            + key
                            + "\n");
        }
    }
}

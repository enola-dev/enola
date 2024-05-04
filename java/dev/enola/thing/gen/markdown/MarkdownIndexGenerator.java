/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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
package dev.enola.thing.gen.markdown;

import com.google.common.collect.ImmutableMap;

import dev.enola.common.function.CheckedPredicate;
import dev.enola.common.io.metadata.Metadata;
import dev.enola.thing.template.TemplateService;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;

/** Generates a Markdown "index" page with links to details. */
class MarkdownIndexGenerator {

    // TODO Order things alphabetically by label!

    // TODO Group things, instead of ugly flag list; initially likely best by rdf:type.

    private final MarkdownLinkWriter linkWriter = new MarkdownLinkWriter();

    void generate(
            ImmutableMap<String, Metadata> metas,
            Writer writer,
            URI outputIRI,
            URI base,
            CheckedPredicate<String, IOException> isDocumentedIRI,
            TemplateService ts)
            throws IOException {

        writer.append("# Things\n\n");
        for (var entry : metas.entrySet()) {
            var iri = entry.getKey();
            var meta = entry.getValue();
            writer.append("* ");
            linkWriter.writeMarkdownLink(iri, meta, writer, outputIRI, base, isDocumentedIRI, ts);
            writer.append('\n');
        }
    }
}

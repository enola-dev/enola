/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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

import static dev.enola.thing.template.Templates.Format.HTML;
import static dev.enola.thing.template.Templates.Format.Star;

import dev.enola.common.function.CheckedPredicate;
import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.metadata.Metadata;
import dev.enola.thing.gen.EnolaDevKnownDocsProvider;
import dev.enola.thing.gen.KnownDocsProvider;
import dev.enola.thing.gen.Relativizer;
import dev.enola.thing.template.TemplateService;
import dev.enola.thing.template.Templates;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

class MarkdownLinkWriter {

    private final Templates.Format format;
    private final KnownDocsProvider kdp = new EnolaDevKnownDocsProvider();

    MarkdownLinkWriter(Templates.Format format) {
        this.format = format;
    }

    void writeMarkdownLink(
            Metadata meta,
            Appendable out,
            URI outputIRI,
            URI base,
            CheckedPredicate<String, IOException> isDocumentedIRI,
            TemplateService ts)
            throws IOException {
        writeMarkdownLink(meta.iri(), meta, out, outputIRI, base, isDocumentedIRI, ts);
    }

    void writeMarkdownLink(
            String iri,
            Metadata meta,
            Appendable out,
            URI outputIRI,
            URI base,
            CheckedPredicate<String, IOException> isDocumentedIRI,
            TemplateService ts)
            throws IOException {
        out.append('[');
        writeLabel(meta, out);
        out.append("](");

        if (!Templates.hasVariables(iri)) {
            iri =
                    ts.breakdown(iri)
                            .map(
                                    breakdown -> {
                                        var urlWithoutVars =
                                                Templates.dropVariableMarkers(
                                                        breakdown.iriTemplate());
                                        var vars = breakdown.variables();
                                        if (format.equals(HTML) || format.equals(Star)) {
                                            vars = addPrefix(vars, "var.");
                                        }
                                        return URIs.addQuery(urlWithoutVars, vars);
                                    })
                            .orElse(iri);
        }

        var href = rel(iri, outputIRI, base, isDocumentedIRI);
        if (href.isEmpty()) throw new IllegalStateException(iri);
        href = Templates.convertToAnotherFormat(href, format);
        out.append(href);
        out.append(')');
    }

    private Map<String, String> addPrefix(Map<String, String> map, String prefix) {
        var newMap = new HashMap<String, String>(map.size());
        for (var entry : map.entrySet()) {
            newMap.put(prefix + entry.getKey(), entry.getValue());
        }
        return newMap;
    }

    void writeLabel(Metadata md, Appendable out) throws IOException {
        if (!md.imageHTML().isEmpty()) {
            out.append(md.imageHTML());
            out.append(' ');
        }
        if (!md.curie().isEmpty()) {
            out.append('`');
            out.append(md.curie());
            out.append('`');
            if (!md.curie().endsWith(md.label())) out.append(' ');
        }
        if (!md.curie().endsWith(md.label())) {
            out.append(Templates.convertToAnotherFormat(md.label(), format));
        }
    }

    // TODO Move this into StaticSiteLinkTransformer (?)
    private String rel(
            String linkIRI,
            URI outputIRI,
            URI base,
            CheckedPredicate<String, IOException> isDocumentedIRI)
            throws IOException {
        if (!isDocumentedIRI.test(linkIRI)) {
            if (linkIRI.startsWith("file:"))
                return Relativizer.relativize(outputIRI, URI.create(linkIRI));
            else return kdp.get(linkIRI);
        } else {
            var woSchemeWithExtLinkedIRI = Relativizer.dropSchemeAddExtension(linkIRI, "md");
            var absoluteRelativeLinkedIRI = base.resolve(woSchemeWithExtLinkedIRI);
            return Relativizer.relativize(outputIRI, absoluteRelativeLinkedIRI);
        }
    }
}

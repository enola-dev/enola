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

class MarkdownLinkWriter {

    private KnownDocsProvider kdp = new EnolaDevKnownDocsProvider();

    void writeMarkdownLink(
            String iri,
            Metadata meta,
            Appendable out,
            URI outputIRI,
            URI base,
            CheckedPredicate<String, IOException> isDocumentedIRI,
            TemplateService ts)
            throws IOException {
        writeMarkdownLink(iri, meta, out, outputIRI, base, isDocumentedIRI, ts, "");
    }

    void writeMarkdownLink(
            String iri,
            Metadata meta,
            Appendable out,
            URI outputIRI,
            URI base,
            CheckedPredicate<String, IOException> isDocumentedIRI,
            TemplateService ts,
            String format)
            throws IOException {
        out.append('[');
        out.append(format);
        writeLabel(meta, out);
        out.append(format);
        out.append("](");
        if (!Templates.hasVariables(iri)) {
            iri =
                    ts.breakdown(iri)
                            .map(
                                    breakdown ->
                                            URIs.addQuery(
                                                    Templates.dropVariableMarkers(
                                                            breakdown.iriTemplate()),
                                                    breakdown.variables()))
                            .orElse(iri);
        }
        var href = rel(iri, outputIRI, base, isDocumentedIRI);
        if (href.isEmpty()) throw new IllegalStateException(iri);
        out.append(Templates.convertToMustache(href));
        out.append(')');
    }

    void writeLabel(Metadata md, Appendable out) throws IOException {
        if (!md.imageHTML().isEmpty()) {
            out.append(md.imageHTML());
            out.append(" ");
        }
        out.append(Templates.convertToMustache(md.label()));
    }

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
            var woSchemeWithExtLinkedIRI =
                    Relativizer.dropSchemeAddExtension(URI.create(linkIRI), "md");
            var absoluteRelativeLinkedIRI = base.resolve(woSchemeWithExtLinkedIRI);
            var relativeLinkedIRI = Relativizer.relativize(outputIRI, absoluteRelativeLinkedIRI);
            return relativeLinkedIRI;
        }
    }
}

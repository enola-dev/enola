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

import dev.enola.common.io.metadata.Metadata;
import dev.enola.thing.gen.Relativizer;

import java.io.IOException;
import java.net.URI;
import java.util.function.Predicate;

class MarkdownLinkWriter {

    void writeMarkdownLink(
            String iri,
            Metadata meta,
            Appendable out,
            URI outputIRI,
            URI base,
            Predicate<String> isDocumentedIRI)
            throws IOException {
        out.append('[');
        writeLabel(meta, out);
        out.append("](");
        var href = rel(iri, outputIRI, base, isDocumentedIRI);
        if (href.isEmpty()) throw new IllegalStateException(iri);
        out.append(href);
        out.append(')');
    }

    void writeLabel(Metadata md, Appendable out) throws IOException {
        if (!md.imageHTML().isEmpty()) {
            out.append(md.imageHTML());
            out.append(" ");
        }
        out.append(md.label());
    }

    private CharSequence rel(
            String linkIRI, URI outputIRI, URI base, Predicate<String> isDocumentedIRI) {
        if (!isDocumentedIRI.test(linkIRI)) {
            if (linkIRI.startsWith("file:"))
                return Relativizer.relativize(outputIRI, URI.create(linkIRI));
            else return linkIRI;
        } else {
            var relativeLinkedIRI = Relativizer.dropSchemeAddExtension(URI.create(linkIRI), "md");
            var absoluteRelativeLinkedIRI = base.resolve(relativeLinkedIRI);
            return Relativizer.relativize(outputIRI, absoluteRelativeLinkedIRI);
        }
    }
}

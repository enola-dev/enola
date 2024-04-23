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

import dev.enola.thing.gen.DocGenConstants;
import dev.enola.thing.gen.Relativizer;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.proto.Value;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.function.Predicate;

class MarkdownThingGenerator {

    // TODO Add Datatype support, with a a DatatypeRepository...

    void generate(
            Thing thing, Appendable out, URI outputIRI, URI base, Predicate<String> isDocumentedIRI)
            throws IOException {
        // TODO Title with MetadataProvider, *AFTER* it's been refactored
        out.append("# ");
        // TODO IRI under title, as <link>
        // TODO Make IRI path segments clickable?!
        out.append(thing.getIri());
        out.append("\n\n");

        write("", thing.getFieldsMap(), out, outputIRI, base, isDocumentedIRI);
    }

    private void write(
            String indent,
            Map<String, Value> properties,
            Appendable out,
            URI outputIRI,
            URI base,
            Predicate<String> isDocumentedIRI)
            throws IOException {
        for (var entry : properties.entrySet()) {
            var predicateIRI = entry.getKey();
            var object = entry.getValue();

            out.append(indent);
            out.append("* ");
            // TODO Use MetadataProvider (again), *AFTER* it's been refactored
            out.append(predicateIRI);
            out.append(" : ");

            write(indent, object, out, outputIRI, base, isDocumentedIRI);
        }
        out.append(DocGenConstants.FOOTER);
    }

    private void write(
            String indent,
            Value value,
            Appendable out,
            URI outputIRI,
            URI base,
            Predicate<String> isDocumentedIRI)
            throws IOException {
        switch (value.getKindCase()) {
            case LINK:
                var link = value.getLink();
                out.append("[");
                // TODO Use MetadataProvider (for the 3d time), *AFTER* it's been refactored
                out.append(link);
                out.append("](");
                out.append(rel(link, outputIRI, base, isDocumentedIRI));
                out.append(")\n");
                break;

            case STRING:
                out.append(value.getString());
                out.append('\n');
                break;

            case LITERAL:
                var literal = value.getLiteral();
                out.append(literal.getValue());
                out.append("(");
                // TODO Use MetadataProvider (for the 4th time), *AFTER* it's been refactored
                out.append(literal.getDatatype());
                out.append(")\n");
                break;

            case LANG_STRING:
                var langString = value.getLangString();
                out.append(langString.getText());
                out.append(" @ ");
                // TODO Print emoji of LANG flag - itself read from a Thing...
                out.append(langString.getLang());
                out.append('\n');
                break;

            case STRUCT:
                write(
                        indent + "    ",
                        value.getStruct().getFieldsMap(),
                        out,
                        outputIRI,
                        base,
                        isDocumentedIRI);
                out.append('\n');
                break;

            case LIST:
                var list = value.getList().getValuesList();
                out.append('\n');
                for (var element : list) {
                    out.append(indent);
                    out.append("  1. ");
                    write(indent, element, out, outputIRI, base, isDocumentedIRI);
                }
                break;

            default:
                throw new IllegalStateException("TODO: " + value);
        }
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

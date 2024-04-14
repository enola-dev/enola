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

import dev.enola.thing.Link;
import dev.enola.thing.Literal;
import dev.enola.thing.gen.Relativizer;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.proto.Value;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

class MarkdownThingGenerator {

    // TODO Add Datatype support, with a a DatatypeRepository...

    void generate(Thing thing, Appendable out, URI outputIRI, URI base) throws IOException {
        // TODO Use MetadataProvider, *AFTER* it's been refactored

        out.append("# ");
        // TODO Make IRI path segments clickable?!
        out.append(thing.getIri());
        out.append("\n\n");

        write("", thing.getFieldsMap(), out, outputIRI, base);
    }

    private void write(
            String indent, Map<String, Value> properties, Appendable out, URI outputIRI, URI base)
            throws IOException {
        for (var entry : properties.entrySet()) {
            var predicateIRI = entry.getKey();
            var object = entry.getValue();

            out.append(indent);
            out.append("* ");
            // TODO Use MetadataProvider (again), *AFTER* it's been refactored
            out.append(predicateIRI);
            out.append(" : ");

            write(indent, object, out, outputIRI, base);
        }
    }

    private void write(String indent, Object object, Appendable out, URI outputIRI, URI base)
            throws IOException {
        switch (object) {
            case Link link:
                out.append("[");
                // TODO Use MetadataProvider (for the 3d time), *AFTER* it's been refactored
                out.append(link.iri());
                out.append("](");
                out.append(rel(link.iri(), outputIRI, base));
                out.append(")");
                break;

            case Literal literal:
                out.append(literal.value());
                out.append("(");
                // TODO Use MetadataProvider (for the 4th time), *AFTER* it's been refactored
                out.append(literal.datatypeIRI());
                out.append(")");
                break;

            case Map<?, ?> properties:
                write(indent + "  ", properties, out, outputIRI, base);
                break;

            case List<?> list:
                throw new IllegalStateException("TODO Implement List..");

            default:
                out.append(object.toString());
        }
    }

    private CharSequence rel(String linkIRI, URI outputIRI, URI base) {
        var relativeLinkedIRI = Relativizer.relativize(URI.create(linkIRI), "md");
        var absoluteRelativeLinkedIRI = base.resolve(relativeLinkedIRI);
        var relativeToOutputIRI = outputIRI.relativize(absoluteRelativeLinkedIRI);
        return relativeToOutputIRI.toString();
    }
}

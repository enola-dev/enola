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
import dev.enola.common.io.metadata.Metadata;
import dev.enola.common.io.metadata.MetadataProvider;
import dev.enola.rdf.ProtoThingIntoJsonLdAppendableConverter;
import dev.enola.thing.gen.DocGenConstants;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.proto.Value;
import dev.enola.thing.template.TemplateService;
import dev.enola.thing.template.Templates;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/** Generates the Markdown showing details about one Thing. */
class MarkdownThingGenerator {

    private final Templates.Format format;
    private final MetadataProvider metadataProvider;
    private final MarkdownLinkWriter linkWriter;
    private final ProtoThingIntoJsonLdAppendableConverter jsonLdGenerator;

    MarkdownThingGenerator(Templates.Format format, MetadataProvider metadataProvider) {
        this.format = format;
        this.linkWriter = new MarkdownLinkWriter(format);
        this.metadataProvider = metadataProvider;
        this.jsonLdGenerator = new ProtoThingIntoJsonLdAppendableConverter();
    }

    Metadata generate(
            Thing thing,
            Appendable out,
            URI outputIRI,
            URI base,
            CheckedPredicate<String, IOException> isDocumentedIRI,
            TemplateService ts,
            boolean footer)
            throws IOException {
        var thingIRI = thing.getIri();
        out.append("# ");
        var meta = metadataProvider.get(thing, thingIRI);
        linkWriter.writeLabel(meta, out);

        var iri = Templates.convertToAnotherFormat(thingIRI, format);
        // NB: mkdocs does not render <file:///...> correctly, but [file:///...](file:///...) works:
        out.append("\n\n[");
        out.append(iri);
        out.append("](");
        out.append(iri);
        out.append(")\n\n");

        if (!meta.descriptionHTML().isEmpty()) {
            out.append(meta.descriptionHTML());
            out.append("\n\n");
        }

        write("", thing.getFieldsMap(), out, outputIRI, base, isDocumentedIRI, ts);

        if (footer) out.append(DocGenConstants.FOOTER);

        // Skip Template Things with IRIs such as https://example.org/greet/{NUMBER}
        if (!Templates.hasVariables(thing.getIri())) {
            out.append("\n");
            out.append("<script type=\"application/ld+json\">\n");
            jsonLdGenerator.convertIntoOrThrow(thing, out);
            out.append("\n</script>\n");
        }

        return meta;
    }

    private void write(
            String indent,
            Map<String, Value> properties,
            Appendable out,
            URI outputIRI,
            URI base,
            CheckedPredicate<String, IOException> isDocumentedIRI,
            TemplateService ts)
            throws IOException {
        for (var entry : properties.entrySet()) {
            var predicateIRI = entry.getKey();
            var object = entry.getValue();

            out.append(indent);
            out.append("* ");
            var meta = metadataProvider.get(predicateIRI);
            linkWriter.writeMarkdownLink(meta, out, outputIRI, base, isDocumentedIRI, ts);
            out.append(": ");

            write(indent, object, out, outputIRI, base, isDocumentedIRI, ts);
        }
    }

    private void write(
            String indent,
            Value value,
            Appendable out,
            URI outputIRI,
            URI base,
            CheckedPredicate<String, IOException> isDocumentedIRI,
            TemplateService ts)
            throws IOException {
        switch (value.getKindCase()) {
            case LINK:
                var link = value.getLink();
                var meta = metadataProvider.get(link);
                linkWriter.writeMarkdownLink(meta, out, outputIRI, base, isDocumentedIRI, ts);
                out.append('\n');
                break;

            case STRING:
                out.append(value.getString());
                out.append('\n');
                break;

            case LITERAL:
                var literal = value.getLiteral();
                out.append(literal.getValue());
                out.append(" _");
                var datatypeIRI = literal.getDatatype();
                var datatypeMeta = metadataProvider.get(datatypeIRI);
                linkWriter.writeMarkdownLink(
                        datatypeMeta, out, outputIRI, base, isDocumentedIRI, ts);
                out.append("_\n");
                break;

            case LANG_STRING:
                var langString = value.getLangString();
                out.append(langString.getText());
                out.append(" `@");
                // TODO Print emoji of LANG flag - itself read from a Thing...
                out.append(langString.getLang());
                out.append("`\n");
                break;

            case STRUCT:
                out.append('\n');
                write(
                        indent + "    ",
                        value.getStruct().getFieldsMap(),
                        out,
                        outputIRI,
                        base,
                        isDocumentedIRI,
                        ts);
                break;

            case LIST:
                var list = value.getList().getValuesList();
                out.append('\n');
                for (var element : list) {
                    out.append(indent);
                    out.append("    1. ");
                    write(indent, element, out, outputIRI, base, isDocumentedIRI, ts);
                }
                break;

            default:
                throw new IllegalStateException("TODO: " + value);
        }
    }
}

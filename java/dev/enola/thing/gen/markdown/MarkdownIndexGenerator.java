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

import static com.google.common.collect.Iterables.filter;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.errorprone.annotations.Immutable;

import dev.enola.common.function.CheckedPredicate;
import dev.enola.common.io.metadata.Metadata;
import dev.enola.common.io.metadata.MetadataProvider;
import dev.enola.common.tree.ImmutableTreeBuilder;
import dev.enola.data.ProviderFromIRI;
import dev.enola.thing.KIRI;
import dev.enola.thing.gen.DocGenConstants;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.proto.Value;
import dev.enola.thing.template.TemplateService;
import dev.enola.thing.template.Templates;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;

/** Generates a Markdown "index" page with links to details. */
class MarkdownIndexGenerator {

    // TODO Group by rdfs:subPropertyOf rdfs:subClassOf in the Tree

    private final MetadataProvider metadataProvider;
    private final ProviderFromIRI<Thing> thingProvider;
    private final MarkdownLinkWriter linkWriter;
    private final Iterable<Metadata> metas;
    private final boolean footer;

    public MarkdownIndexGenerator(
            Iterable<Metadata> metas,
            MetadataProvider metadataProvider,
            ProviderFromIRI<Thing> thingProvider,
            boolean footer,
            Templates.Format format) {
        this.metadataProvider = metadataProvider;
        this.thingProvider = thingProvider;
        this.metas = metas;
        this.footer = footer;
        this.linkWriter = new MarkdownLinkWriter(format);
    }

    void generate(Writer writer, URI outputIRI, URI base, TemplateService ts) throws IOException {
        var tree = new ImmutableTreeBuilder<ThingOrHeading>();
        var rootMetadata = new ThingOrHeading(null, new Metadata("fake:/", "☸", "", "Things", ""));
        tree.root(rootMetadata);

        for (var metadata : metas) {
            addToTree(tree, rootMetadata, metadata);
        }

        write(writer, tree, rootMetadata, 1, outputIRI, base, ts);

        if (footer) writer.append(DocGenConstants.FOOTER);
    }

    private void write(
            Writer writer,
            ImmutableTreeBuilder<ThingOrHeading> tree,
            ThingOrHeading node,
            int level,
            URI outputIRI,
            URI base,
            TemplateService ts)
            throws IOException {
        CheckedPredicate<String, IOException> isDocumentedIRI =
                uri -> thingProvider.get(uri) != null;

        for (int i = 0; i < level; i++) {
            writer.write('#');
        }
        writer.write(' ');
        if (!node.heading.iri().equals("/"))
            linkWriter.writeMarkdownLink(
                    node.heading.iri(), node.heading, writer, outputIRI, base, isDocumentedIRI, ts);
        else writer.append(node.heading.label());
        writer.append("\n\n");

        var successors = tree.successors(node);
        var thingMetas = filter(successors, thingOrHeading -> thingOrHeading.thing() != null);
        for (var thingOrHeading : thingMetas) {
            var thingMeta = thingOrHeading.thing;
            var iri = thingMeta.iri();
            writer.append("* ");
            linkWriter.writeMarkdownLink(
                    iri, thingMeta, writer, outputIRI, base, isDocumentedIRI, ts);
            writer.append('\n');
        }

        var headingMetas = filter(successors, thingOrHeading -> thingOrHeading.heading() != null);
        for (var thingOrHeading : headingMetas) {
            write(writer, tree, thingOrHeading, level + 1, outputIRI, base, ts);
        }
        writer.append("\n");
    }

    private void addToTree(
            ImmutableTreeBuilder<ThingOrHeading> tree, ThingOrHeading node, Metadata metadata) {

        var thingIRI = metadata.iri();
        var thing = thingProvider.get(thingIRI);

        ThingOrHeading parent;
        if (thing == null) {
            parent = tree.root();

        } else {
            var protoValue = thing.getFieldsMap().get(KIRI.RDF.TYPE);
            if (protoValue != null) {
                String typeIRI = getTypeIRI(protoValue);
                parent = new ThingOrHeading(null, metadataProvider.get(typeIRI));
                if (!Iterables.contains(tree.successors(node), parent)) {
                    tree.addChild(node, parent);
                }
            } else {
                parent = tree.root();
            }
        }

        tree.addChild(parent, new ThingOrHeading(metadata, null));
    }

    private static String getTypeIRI(Value protoValue) {
        String typeIRI = "";
        var list = protoValue.getList();
        if (list != null && !list.getValuesList().isEmpty()) {
            // Hack...
            typeIRI = list.getValuesList().get(0).getLink();
        } else if (protoValue.hasLink()) typeIRI = protoValue.getLink();
        // TODO It technically should never be a String... pre-Validation should catch this, later?
        else if (protoValue.hasString()) typeIRI = protoValue.getString();
        if (Strings.isNullOrEmpty(typeIRI))
            throw new IllegalArgumentException(protoValue.toString());
        return typeIRI;
    }

    @Immutable
    private record ThingOrHeading(@Nullable Metadata thing, @Nullable Metadata heading) {}
}

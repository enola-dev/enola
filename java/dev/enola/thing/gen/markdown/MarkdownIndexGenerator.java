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
import dev.enola.common.io.metadata.MetadataProvider;
import dev.enola.common.tree.ImmutableTreeBuilder;
import dev.enola.data.ProviderFromIRI;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.template.TemplateService;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;

/** Generates a Markdown "index" page with links to details. */
class MarkdownIndexGenerator {

    // TODO Group things, instead of ugly flag list; initially likely best by rdf:type.

    private final MetadataProvider metadataProvider;
    private final ProviderFromIRI<Thing> thingProvider;
    private final MarkdownLinkWriter linkWriter = new MarkdownLinkWriter();
    private final Iterable<Metadata> metas;

    public MarkdownIndexGenerator(
            Iterable<Metadata> metas,
            MetadataProvider metadataProvider,
            ProviderFromIRI<Thing> thingProvider) {
        this.metadataProvider = metadataProvider;
        this.thingProvider = thingProvider;
        this.metas = metas;
    }

    void generate(Writer writer, URI outputIRI, URI base, TemplateService ts) throws IOException {
        var tree = new ImmutableTreeBuilder<Metadata>();
        var rootMetadata = new Metadata("/", "☸", "", "Things", "");
        var noTypeMetadata = new Metadata("/NOTYPE", "", "", "No Type", "");
        tree.root(rootMetadata);
        // tree.addChild(rootMetadata, noTypeMetadata);

        for (var metadata : metas) {
            addToTree(tree, metadata, noTypeMetadata);
        }

        write(writer, tree, rootMetadata, 1, outputIRI, base, ts);
    }

    private void write(
            Writer writer,
            ImmutableTreeBuilder<Metadata> tree,
            Metadata node,
            int level,
            URI outputIRI,
            URI base,
            TemplateService ts)
            throws IOException {

        writer.append("\n");
        for (int i = 0; i < level; i++) {
            writer.write('#');
        }
        writer.write(' ');
        linkWriter.writeMarkdownLink(node.iri(), node, writer, outputIRI, base, uri -> true, ts);
        writer.append("\n\n");

        for (var metadata : tree.successors(node)) {
            var iri = metadata.iri();
            writer.append("* ");
            linkWriter.writeMarkdownLink(iri, metadata, writer, outputIRI, base, uri -> true, ts);
            writer.append('\n');

            if (tree.successors(metadata).iterator().hasNext()) {
                write(writer, tree, metadata, level + 1, outputIRI, base, ts);
            }
        }
    }

    private void addToTree(
            ImmutableTreeBuilder<Metadata> tree, Metadata metadata, Metadata noTypeMetadata) {
        tree.addChild(tree.root(), metadata);

        /*
        var thingIRI = metadata.iri();
        var thing = thingProvider.get(thingIRI);

        Metadata parent;
        if (thing == null) {
            parent = noTypeMetadata;
            if (!Iterables.contains(tree.successors(tree.root()), parent)) {
                tree.addChild(tree.root(), parent);
            }

        } else {
            var protoValue = thing.getFieldsMap().get(KIRI.RDF.TYPE);

            if (protoValue != null) {
                var typeIRI = protoValue.getLink();
                parent = metadataProvider.get(typeIRI);
                // if (!tree.successors(parent).iterator().hasNext()) {
                //    addToTree(tree, parent, noTypeMetadata);
                // }
            } else {
                parent = noTypeMetadata;
            }
        }

        if (!Iterables.contains(tree.successors(parent), metadata)) {
            tree.addChild(parent, metadata);
        }
        */
    }
}

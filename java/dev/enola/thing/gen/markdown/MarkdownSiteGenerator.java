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

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;

import dev.enola.common.context.TLC;
import dev.enola.common.function.CheckedPredicate;
import dev.enola.common.io.MoreFileSystems;
import dev.enola.common.io.metadata.Metadata;
import dev.enola.common.io.metadata.MetadataProvider;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.data.ProviderFromIRI;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.thing.KIRI;
import dev.enola.thing.gen.Relativizer;
import dev.enola.thing.gen.gexf.GexfGenerator;
import dev.enola.thing.gen.graphviz.GraphvizGenerator;
import dev.enola.thing.message.ThingAdapter;
import dev.enola.thing.metadata.ThingHierarchyProvider;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.template.TemplateService;
import dev.enola.thing.template.Templates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/** Generates a "site" of Markdown files, given some Things. */
public class MarkdownSiteGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(MarkdownSiteGenerator.class);

    static final String TYPES_MD = "index.md";
    static final String HIERARCHY_MD = "hierarchy.md";

    private final URI base;
    private final ResourceProvider rp;
    private final MarkdownThingGenerator mtg;
    private final MetadataProvider metadataProvider;
    private final DatatypeRepository datatypeRepository;
    private final Templates.Format format;
    private final GraphvizGenerator graphvizGenerator;
    private final GexfGenerator gexfGenerator;

    public MarkdownSiteGenerator(
            URI base,
            ResourceProvider rp,
            MetadataProvider metadataProvider,
            DatatypeRepository datatypeRepository,
            Templates.Format format) {
        this.base = base;
        // TODO Re-use the (newer) WritableResourcesProvider here...
        if (!MoreFileSystems.URI_SCHEMAS.contains(base.getScheme()))
            throw new IllegalArgumentException(
                    "Must pass an existing (!) directory as --output=file:... not: " + base);
        if (!base.toString().endsWith("/"))
            throw new IllegalArgumentException(
                    "Must pass an existing (!) directory which ends with '/' as --output=file:..."
                            + " not: "
                            + base);

        this.format = format;
        this.metadataProvider = metadataProvider;
        this.datatypeRepository = datatypeRepository;
        this.mtg = new MarkdownThingGenerator(format, metadataProvider);
        this.rp = rp;
        this.graphvizGenerator = new GraphvizGenerator(metadataProvider);
        this.gexfGenerator = new GexfGenerator(metadataProvider);
    }

    public void generate(
            Iterable<Thing> protoThings,
            ProviderFromIRI<Thing> thingProvider,
            CheckedPredicate<String, IOException> isDocumentedIRI,
            TemplateService ts,
            boolean generateIndexFile,
            boolean footer)
            throws IOException {

        // TODO Remove, if not needed, after all?!
        // TODO If needed anywhere else, factor out...
        // Like AlwaysThingProvider (but for Proto Thing)
        /*
                final var originalThingProvider = thingProvider;
                thingProvider =
                        new ProviderFromIRI<Thing>() {
                            @Override
                            public @Nullable Thing get(String iri) {
                                var thing = originalThingProvider.get(iri);
                                if (thing == null) return Thing.newBuilder().setIri(iri).build();
                                else return thing;
                            }
                        };
        */

        var javaThings = proto2java(protoThings);
        generateGEXF(javaThings);
        generateGraphviz(javaThings);

        var metas = ImmutableSortedSet.orderedBy(Metadata.IRI_Comparator);

        // TODO Do this multi-threaded, in parallel... (but BEWARE ImmutableMap not thread safe!)
        for (var thing : protoThings) {
            LOG.debug("Thing {}", thing);
            var thingIRI = thing.getIri();
            var relativeThingIRI = Relativizer.dropSchemeAddExtension(thingIRI, "md");
            var outputIRI = base.resolve(relativeThingIRI);
            LOG.info("Generating (base={}, thingIRI={}): {}", base, thingIRI, outputIRI);
            var outputResource = rp.getWritableResource(outputIRI);
            if (outputResource == null) {
                LOG.error("ResourceProvider cannot provide a WritableResource: {}", outputIRI);
                continue;
            }
            try (var writer = outputResource.charSink().openBufferedStream()) {
                var meta =
                        mtg.generate(thing, writer, outputIRI, base, isDocumentedIRI, ts, footer);
                metas.add(meta);
            }
        }

        // NB: This must be AFTER above (because metas gets populated above, first)
        if (generateIndexFile) {
            var typeParents = new ThingHierarchyProvider("By Type:", List.of(KIRI.RDF.TYPE));
            generateIndexMD(thingProvider, ts, footer, metas, typeParents, TYPES_MD);

            var allParents = new ThingHierarchyProvider();
            // TODO Fix grouping by rdfs:subPropertyOf rdfs:subClassOf in the Tree
            generateIndexMD(thingProvider, ts, footer, metas, allParents, HIERARCHY_MD);
        }
    }

    private void generateIndexMD(
            ProviderFromIRI<Thing> thingProvider,
            TemplateService ts,
            boolean footer,
            ImmutableSortedSet.Builder<Metadata> metas,
            ThingHierarchyProvider hierarchyProvider,
            String filename)
            throws IOException {
        var mig =
                new MarkdownIndexGenerator(
                        metas.build(),
                        metadataProvider,
                        hierarchyProvider,
                        thingProvider,
                        datatypeRepository,
                        footer,
                        format);
        // TODO When generating finer-grained per-domain sub-indexes, it should not overwrite
        // something like existing index pages which were already generated from RDF Turtle,
        // e.g. https://docs.enola.dev/models/www.w3.org/1999/02/22-rdf-syntax-ns/

        var indexURI = base.resolve(filename);
        var indexResource = rp.getWritableResource(indexURI);
        try (var writer = indexResource.charSink().openBufferedStream()) {
            mig.generate(writer, indexURI, base, ts);
        }
        LOG.info("Wrote {}", indexResource);
    }

    private Iterable<dev.enola.thing.Thing> proto2java(Iterable<Thing> protoThings) {
        var dtr = TLC.get(DatatypeRepository.class);
        var javaThings = new ArrayList<dev.enola.thing.Thing>(Iterables.size(protoThings));
        for (var protoThing : protoThings) {
            javaThings.add(new ThingAdapter(protoThing, dtr));
        }
        return javaThings;
    }

    private void generateGEXF(Iterable<dev.enola.thing.Thing> javaThings) throws IOException {
        var gexfOutputResource = rp.getWritableResource(base.resolve("graph.gexf"));
        gexfGenerator.convertIntoOrThrow(javaThings, gexfOutputResource);
    }

    private void generateGraphviz(Iterable<dev.enola.thing.Thing> javaThings) throws IOException {
        var graphVizOutputResource = rp.getWritableResource(base.resolve("graphviz.gv"));
        graphvizGenerator.convertIntoOrThrow(javaThings, graphVizOutputResource);
    }
}

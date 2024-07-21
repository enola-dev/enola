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

import dev.enola.common.function.CheckedPredicate;
import dev.enola.common.io.MoreFileSystems;
import dev.enola.common.io.metadata.Metadata;
import dev.enola.common.io.metadata.MetadataProvider;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.data.ProviderFromIRI;
import dev.enola.thing.gen.Relativizer;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.template.TemplateService;
import dev.enola.thing.template.Templates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

/** Generates a "site" of Markdown files, given some Things. */
public class MarkdownSiteGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(MarkdownSiteGenerator.class);

    private final URI base;
    private final ResourceProvider rp;
    private final MarkdownThingGenerator mtg;
    private final MetadataProvider metadataProvider;
    private final Templates.Format format;

    public MarkdownSiteGenerator(
            URI base,
            ResourceProvider rp,
            MetadataProvider metadataProvider,
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
        this.mtg = new MarkdownThingGenerator(format, metadataProvider);
        this.rp = rp;
    }

    public void generate(
            Iterable<Thing> things,
            ProviderFromIRI<Thing> thingProvider,
            CheckedPredicate<String, IOException> isDocumentedIRI,
            TemplateService ts,
            boolean generateIndexFile,
            boolean footer)
            throws IOException {

        var metas = ImmutableSortedSet.orderedBy(Metadata.IRI_Comparator);

        // TODO Do this multi-threaded, in parallel... (but BEWARE ImmutableMap not thread safe!)
        for (var thing : things) {
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

        var mig =
                new MarkdownIndexGenerator(
                        metas.build(), metadataProvider, thingProvider, footer, format);
        if (generateIndexFile) {
            // TODO When generating finer-grained per-domain sub-indexes, it should not overwrite
            // something like existing index pages which were already generated from RDF Turtle,
            // e.g. https://docs.enola.dev/models/www.w3.org/1999/02/22-rdf-syntax-ns/

            var indexURI = base.resolve("index.md");
            var indexResource = rp.getWritableResource(indexURI);
            try (var writer = indexResource.charSink().openBufferedStream()) {
                mig.generate(writer, indexURI, base, ts);
            }
        }
    }
}

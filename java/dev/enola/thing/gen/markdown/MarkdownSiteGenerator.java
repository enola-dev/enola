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

import com.google.common.collect.ImmutableMap;

import dev.enola.common.MoreIterables;
import dev.enola.common.io.MoreFileSystems;
import dev.enola.common.io.metadata.Metadata;
import dev.enola.common.io.metadata.MetadataProvider;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.thing.gen.Relativizer;
import dev.enola.thing.proto.Thing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.function.Predicate;

/** Generates a "site" of Markdown files, given some Things. */
public class MarkdownSiteGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(MarkdownSiteGenerator.class);

    private final URI base;
    private final ResourceProvider rp;
    private final MarkdownThingGenerator mtg;
    private final MarkdownIndexGenerator mig;

    public MarkdownSiteGenerator(URI base, ResourceProvider rp, MetadataProvider metadataProvider) {
        this.base = base;
        if (!MoreFileSystems.URI_SCHEMAS.contains(base.getScheme()))
            throw new IllegalArgumentException(
                    "Must pass an existing (!) directory as --output=file:... not: " + base);
        if (!base.toString().endsWith("/"))
            throw new IllegalArgumentException(
                    "Must pass an existing (!) directory which ends with '/' as --output=file:..."
                            + " not: "
                            + base);

        this.mtg = new MarkdownThingGenerator(metadataProvider);
        this.mig = new MarkdownIndexGenerator();
        this.rp = rp;
    }

    public void generate(Iterable<Thing> things, Predicate<String> isDocumentedIRI)
            throws IOException {

        ImmutableMap.Builder<String, Metadata> metas =
                ImmutableMap.builderWithExpectedSize(MoreIterables.sizeIfKnown(things).orElse(7));

        // TODO Do this multi-threaded, in parallel...
        for (var thing : things) {
            LOG.debug("Thing {}", thing);
            var thingIRI = thing.getIri();
            var relativeThingIRI = Relativizer.dropSchemeAddExtension(URI.create(thingIRI), "md");
            var outputIRI = base.resolve(relativeThingIRI);
            LOG.info("Generating (base={}, thingIRI={}): {}", base, thingIRI, outputIRI);
            var outputResource = rp.getWritableResource(outputIRI);
            try (var writer = outputResource.charSink().openBufferedStream()) {
                var meta = mtg.generate(thing, writer, outputIRI, base, isDocumentedIRI);
                metas.put(thingIRI, meta);
            }
        }

        // TODO When generating finer-grained per-domain sub-indexes, it should not overwrite
        // something like existing index pages which were already generated from RDF Turtle, e.g.
        // https://docs.enola.dev/models/www.w3.org/1999/02/22-rdf-syntax-ns/

        var indexURI = base.resolve("index.md");
        var indexResource = rp.getWritableResource(indexURI);
        try (var writer = indexResource.charSink().openBufferedStream()) {
            mig.generate(metas.build(), writer, indexURI, base, uri -> true);
        }
    }
}

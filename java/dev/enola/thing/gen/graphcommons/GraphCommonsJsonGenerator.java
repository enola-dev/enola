/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.thing.gen.graphcommons;

import static com.google.gson.FormattingStyle.PRETTY;
import static com.google.gson.Strictness.STRICT;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.hash.Hashing;
import com.google.common.io.CharStreams;
import com.google.gson.stream.JsonWriter;

import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.metadata.Metadata;
import dev.enola.thing.KIRI;
import dev.enola.thing.Thing;
import dev.enola.thing.gen.Colorizer;
import dev.enola.thing.gen.Orphanage;
import dev.enola.thing.gen.ThingsIntoAppendableConverter;
import dev.enola.thing.impl.OnlyIRIThing;
import dev.enola.thing.metadata.ThingMetadataProvider;
import dev.enola.thing.repo.StackedThingProvider;
import dev.enola.thing.repo.ThingProvider;

import java.io.IOException;

/**
 * Generator of JSON Format used by <a href="https://graphcommons.com">Graph Commons</a>.
 *
 * <p>@deprecated This does not actually work.
 */
@Deprecated
public class GraphCommonsJsonGenerator implements ThingsIntoAppendableConverter {

    // ============================================================================
    //   This does not actually work.
    //   The reason is that https://graphcommons.com expects every edgeType
    //   to match to exactly one sourceNodeTypeId & targetNodeTypeId; but that's
    //   not the case in RDF.
    //   To make this work, you probably would have to add "fake" edgeTypes...
    // ============================================================================

    // TODO Handle links inside blank nodes; see the other Network Graph Generators

    private final ThingMetadataProvider metadataProvider;

    public GraphCommonsJsonGenerator(ThingMetadataProvider metadataProvider) {
        this.metadataProvider = metadataProvider;
    }

    @Override
    public boolean convertInto(Iterable<Thing> from, Appendable out)
            throws ConversionException, IOException {
        var nodeTypeOrphanage = new Orphanage();
        var edgeTypeOrphanage = new Orphanage();
        var writer = CharStreams.asWriter(out);
        var jsonWriter = new JsonWriter(writer);
        jsonWriter.setStrictness(STRICT);
        jsonWriter.setFormattingStyle(PRETTY); // TODO FormattingStyle.COMPACT, if !pretty
        jsonWriter.setSerializeNulls(false);
        jsonWriter.setHtmlSafe(true); // TODO ?
        jsonWriter.beginObject();
        try (var ctx = TLC.open()) {
            ctx.push(ThingProvider.class, new StackedThingProvider(from));

            jsonWriter.name("nodes").beginArray();
            for (Thing thing : from) printThingNode(thing, jsonWriter, nodeTypeOrphanage);

            jsonWriter.endArray().name("edges").beginArray();
            for (Thing thing : from) printThingEdge(thing, jsonWriter, edgeTypeOrphanage);

            jsonWriter.endArray().name("nodeTypes").beginArray();
            for (Thing thing : from) {
                var types = thing.getLinks(KIRI.RDF.TYPE);
                // TODO Include classes that are a subclass of rdfs:Class, such as schema:Class
                if (!types.stream().map(Object::toString).toList().contains(KIRI.RDFS.CLASS))
                    continue;
                printThingNodeType(thing, jsonWriter, nodeTypeOrphanage);
            }
            for (String orphanIRI : nodeTypeOrphanage.orphans())
                printThingNodeType(new OnlyIRIThing(orphanIRI), jsonWriter, nodeTypeOrphanage);

            jsonWriter.endArray().name("edgeTypes").beginArray();
            for (Thing thing : from) {
                var types = thing.getLinks(KIRI.RDF.TYPE);
                // TODO Include properties that are subclass of rdf:Property (e.g. schema:Property)
                if (!types.stream().map(Object::toString).toList().contains(KIRI.RDF.PROPERTY))
                    continue;
                printThingEdgeType(thing, jsonWriter, edgeTypeOrphanage);
            }
            for (String orphanIRI : edgeTypeOrphanage.orphans())
                printThingEdgeType(new OnlyIRIThing(orphanIRI), jsonWriter, edgeTypeOrphanage);

            jsonWriter.endArray().name("name").value("Enola.dev");
        }
        jsonWriter.endObject();
        jsonWriter.flush();
        writer.close();
        return true;
    }

    private void printThingNode(Thing thing, JsonWriter jsonWriter, Orphanage nodeTypeOrphanage)
            throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("id").value(thing.iri());

        String typeId;
        var types = thing.getLinks(KIRI.RDF.TYPE);
        // NB: This randomly picks one of (arbitrarily the "first") type - there could be several!
        if (!types.isEmpty()) typeId = types.iterator().next().toString();
        else typeId = KIRI.E.UNKNOWN_CLASS;
        jsonWriter.name("typeId").value(typeId);
        nodeTypeOrphanage.candidate(typeId);

        var meta = metadataProvider.get(thing.iri());
        printName(meta, jsonWriter);
        printDescription(meta, jsonWriter);
        printImage(meta, jsonWriter);
        // TODO "reference" - what's that?

        jsonWriter.endObject();
    }

    private void printThingEdge(Thing thing, JsonWriter jsonWriter, Orphanage edgeTypeOrphanage)
            throws IOException {
        var sourceId = thing.iri();
        for (var linkPropertyIRI : thing.predicateIRIs()) {
            if (linkPropertyIRI.equals(KIRI.RDF.TYPE)) continue;
            var links = thing.getLinks(linkPropertyIRI);
            if (!links.isEmpty()) edgeTypeOrphanage.candidate(linkPropertyIRI);
            for (var link : links) {
                var linkIRI = link.toString();
                jsonWriter.beginObject();
                jsonWriter.name("id").value(hash(sourceId, linkPropertyIRI, linkIRI));
                jsonWriter.name("typeId").value(linkPropertyIRI);
                jsonWriter.name("sourceId").value(sourceId);
                jsonWriter.name("targetId").value(linkIRI);
                // TODO Weight?
                jsonWriter.endObject();
            }
        }
    }

    private void printThingNodeType(Thing thing, JsonWriter jsonWriter, Orphanage nodeTypeOrphanage)
            throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("id").value(thing.iri());
        nodeTypeOrphanage.nonOrphan(thing.iri());
        var meta = metadataProvider.get(thing.iri());
        printName(meta, jsonWriter);
        printDescription(meta, jsonWriter);
        printColor(thing, jsonWriter);
        // TODO "properties" ...
        jsonWriter.endObject();
    }

    private void printThingEdgeType(Thing thing, JsonWriter jsonWriter, Orphanage edgeTypeOrphanage)
            throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("id").value(thing.iri());
        edgeTypeOrphanage.nonOrphan(thing.iri());
        var meta = metadataProvider.get(thing.iri());
        printName(meta, jsonWriter);
        printDescription(meta, jsonWriter);
        // TODO Handle bi-directional relationships
        jsonWriter.name("directed").value(true);
        printColor(thing, jsonWriter);
        jsonWriter
                .name("sourceNodeTypeId")
                .value(printLinkPropertyOrUnknown(thing, KIRI.RDFS.DOMAIN));
        jsonWriter
                .name("targetNodeTypeId")
                .value(printLinkPropertyOrUnknown(thing, KIRI.RDFS.RANGE));
        // TODO "properties" ...
        jsonWriter.endObject();
    }

    private String printLinkPropertyOrUnknown(Thing thing, String propertyIRI) {
        // TODO What about if rdfs:domain or rdfs:range is multiple?!
        var iri = thing.getString(propertyIRI);
        if (iri != null) return iri;
        else return KIRI.E.UNKNOWN_CLASS;
    }

    private void printName(Metadata meta, JsonWriter jsonWriter) throws IOException {
        jsonWriter.name("name").value(meta.label());
    }

    private void printDescription(Metadata meta, JsonWriter jsonWriter) throws IOException {
        if (!meta.descriptionHTML().isEmpty())
            jsonWriter.name("description").value(meta.descriptionHTML());
    }

    private void printImage(Metadata meta, JsonWriter jsonWriter) throws IOException {
        if (!meta.imageURL().isEmpty()) jsonWriter.name("image").value(meta.imageURL());
    }

    private void printColor(Thing thing, JsonWriter jsonWriter) throws IOException {
        jsonWriter.name("color").value(Colorizer.hexColor(thing));
    }

    private String hash(String... strings) {
        var hasher = Hashing.murmur3_128().newHasher();
        for (var string : strings) {
            hasher.putString(string, UTF_8).putInt(1);
        }
        return hasher.hash().toString();
    }
}

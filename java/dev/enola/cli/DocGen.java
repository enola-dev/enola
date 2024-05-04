/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2024 The Enola <https://enola.dev> Authors
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
package dev.enola.cli;

import static dev.enola.core.thing.ListThingService.ENOLA_ROOT_LIST_THINGS;

import static java.util.stream.Collectors.toUnmodifiableSet;

import com.google.protobuf.InvalidProtocolBufferException;

import dev.enola.common.function.CheckedPredicate;
import dev.enola.core.IDs;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.meta.docgen.MarkdownDocGenerator;
import dev.enola.core.meta.docgen.Options;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.EnolaServiceGrpc.EnolaServiceBlockingStub;
import dev.enola.core.proto.GetThingRequest;
import dev.enola.core.proto.ID;
import dev.enola.core.proto.ListEntitiesRequest;
import dev.enola.thing.gen.markdown.MarkdownSiteGenerator;
import dev.enola.thing.message.MoreThings;
import dev.enola.thing.proto.Thing;
import dev.enola.web.EnolaThingProvider;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

@Command(name = "docgen", description = "Generate Markdown Documentation")
public class DocGen extends CommandWithModelAndOutput {

    @Option(
            names = {"--diagram", "-d"},
            required = true,
            defaultValue = "Mermaid",
            description =
                    "Type of diagrams to generate (${COMPLETION-CANDIDATES}; default:"
                            + " ${DEFAULT-VALUE})")
    Options.DiagramType diagram;

    @Option(
            names = {"--header", "-h"},
            required = true,
            defaultValue = "string:%23%20Models%0A",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            description = "URI of Markdown header (e.g. file: or string:<URL-encoded> etc.)")
    URI headerURI;

    @Option(
            names = {"--index", "-i"},
            negatable = true,
            required = true,
            defaultValue = "true",
            fallbackValue = "true",
            description = "Whether index.md should be generated")
    boolean generateIndexFile;

    @Override
    public void run(EntityKindRepository ekr, EnolaServiceBlockingStub service) throws Exception {
        if (group.model != null) {
            singleMDDocForEntities(service);
        } else {
            multipleMDDocsForThings(service, generateIndexFile);
        }
    }

    private void multipleMDDocsForThings(
            EnolaServiceBlockingStub service, boolean generateIndexFile) throws Exception {
        var mdp = getMetadataProvider(new EnolaThingProvider(service));
        var mdsg = new MarkdownSiteGenerator(output, rp, mdp);

        var thingsList = getThings(service, ENOLA_ROOT_LIST_THINGS);

        // TODO This works, but is not efficient if there were a HUGE amount of Things and MDs...
        var allIRI = thingsList.stream().map(Thing::getIri).collect(toUnmodifiableSet());
        Predicate<String> allIRIContainsPredicate = allIRI::contains;
        CheckedPredicate<String, IOException> knownIRIPredicate = iri -> hasThing(service, iri);
        mdsg.generate(thingsList, knownIRIPredicate, templateService, generateIndexFile);
    }

    private Collection<Thing> getThings(EnolaServiceBlockingStub service, String iri)
            throws InvalidProtocolBufferException {
        var request = GetThingRequest.newBuilder().setIri(iri).build();
        var response = service.getThing(request);
        return MoreThings.fromAny(response.getThing());
    }

    private boolean hasThing(EnolaServiceBlockingStub service, String iri) throws IOException {
        return !getThings(service, iri).isEmpty();
    }

    private void singleMDDocForEntities(EnolaServiceBlockingStub service) throws Exception {
        var eks = new ArrayList<EntityKind>();
        var ekid = ID.newBuilder().setNs("enola").setEntity("entity_kind").build();
        var eri = IDs.toPath(ekid);
        var response = service.listEntities(ListEntitiesRequest.newBuilder().setEri(eri).build());
        for (var entity : response.getEntitiesList()) {
            eks.add(entity.getDataOrThrow("schema").unpack(EntityKind.class));
        }

        var options = new Options();
        options.diagram = diagram;

        var resource = rp.getWritableResource(output);
        var header = rp.getReadableResource(headerURI).charSource().read();

        try (var writer = resource.charSink().openBufferedStream()) {
            new MarkdownDocGenerator(options).render(eks, header, writer);
        }
    }
}

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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

import com.google.protobuf.InvalidProtocolBufferException;

import dev.enola.common.function.CheckedPredicate;
import dev.enola.core.meta.docgen.Options;
import dev.enola.core.proto.EnolaServiceGrpc.EnolaServiceBlockingStub;
import dev.enola.core.proto.GetThingRequest;
import dev.enola.model.Datatypes;
import dev.enola.thing.gen.markdown.MarkdownSiteGenerator;
import dev.enola.thing.message.MoreThings;
import dev.enola.thing.message.ProtoThingMetadataProvider;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.template.Templates;
import dev.enola.web.EnolaThingProvider;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

@Command(name = "docgen", description = "Generate Markdown Documentation")
public class DocGenCommand extends CommandWithModelAndOutput {

    // TODO Replace "docgen" completely with separate class GenCommand subcommands

    @Option(
            names = {"--diagram", "-d"},
            required = true,
            defaultValue = "Mermaid",
            description =
                    "Type of diagrams to generate (${COMPLETION-CANDIDATES}; default:"
                            + " ${DEFAULT-VALUE})")
    Options.DiagramType diagram;

    @Option(
            names = {"--variables", "-var"},
            required = true,
            defaultValue = "Mustache",
            description =
                    "Type of variables to generate for IRI Templates (${COMPLETION-CANDIDATES};"
                            + " default: ${DEFAULT-VALUE})")
    Templates.Format variables;

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
    public void run(EnolaServiceBlockingStub service) throws Exception {
        multipleMDDocsForThings(service, generateIndexFile);
    }

    private void multipleMDDocsForThings(
            EnolaServiceBlockingStub service, boolean generateIndexFile) throws Exception {
        var mdp = getMetadataProvider(new EnolaThingProvider(service));
        var pmdp = new ProtoThingMetadataProvider(mdp);
        var mdsg =
                new MarkdownSiteGenerator(
                        Output.get(output), rp, mdp, pmdp, Datatypes.DTR, variables);

        var things = getThings(service, ENOLA_ROOT_LIST_THINGS);

        // TODO This works, but is not efficient if there were a HUGE amount of Things and MDs...
        var map = things.stream().collect(Collectors.toMap(Thing::getIri, Function.identity()));
        CheckedPredicate<String, IOException> isDocumentedIRI =
                iri -> templateService.get(iri) != null;
        mdsg.generate(things, map::get, isDocumentedIRI, templateService, generateIndexFile, true);
    }

    private Collection<Thing> getThings(EnolaServiceBlockingStub service, String iri)
            throws InvalidProtocolBufferException {
        var request = GetThingRequest.newBuilder().setIri(iri).build();
        var response = service.getThing(request);
        return MoreThings.fromAny(response.getThing());
    }
}

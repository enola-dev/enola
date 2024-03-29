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

import dev.enola.core.IDs;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.meta.docgen.MarkdownDocGenerator;
import dev.enola.core.meta.docgen.Options;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.EnolaServiceGrpc;
import dev.enola.core.proto.ID;
import dev.enola.core.proto.ListEntitiesRequest;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.net.URI;
import java.util.ArrayList;

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

    @Override
    public void run(EntityKindRepository ekr, EnolaServiceGrpc.EnolaServiceBlockingStub service)
            throws Exception {

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

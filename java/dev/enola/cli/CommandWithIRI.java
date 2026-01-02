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

import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.Message;

import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.io.resource.WriterResource;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.common.protobuf.TypeRegistryWrapper;
import dev.enola.core.proto.EnolaServiceGrpc.EnolaServiceBlockingStub;
import dev.enola.core.proto.GetFileDescriptorSetRequest;
import dev.enola.core.view.EnolaMessages;
import dev.enola.rdf.io.RdfWriterConverter;
import dev.enola.rdf.proto.ProtoThingRdfConverter;
import dev.enola.thing.gen.graphcommons.GraphCommonsJsonGenerator;
import dev.enola.thing.gen.graphviz.GraphvizGenerator;
import dev.enola.thing.message.ProtoThings;
import dev.enola.thing.metadata.ThingMetadataProvider;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.proto.Things;
import dev.enola.web.EnolaThingProvider;

import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import picocli.CommandLine;

import java.io.IOException;

public abstract class CommandWithIRI extends CommandWithModelAndOutput {

    @CommandLine.Option(
            names = {"--format", "-f"},
            required = true,
            defaultValue = "Turtle",
            description = "Output Format: ${COMPLETION-CANDIDATES}; default=${DEFAULT-VALUE}")
    Format format;

    // TODO @Parameters(index = "0..*") List<String> eris;
    @CommandLine.Parameters(index = "0", paramLabel = "iri", description = "IRI of Thing")
    String iri;

    private WritableResource resource;
    private TypeRegistryWrapper typeRegistryWrapper;
    protected EnolaMessages enolaMessages;
    private ThingMetadataProvider thingMetadataProvider;

    @Override
    protected final void run(EnolaServiceBlockingStub service) throws Exception {
        thingMetadataProvider = getMetadataProvider(new EnolaThingProvider(service));

        var gfdsr = GetFileDescriptorSetRequest.newBuilder().build();
        var fds = service.getFileDescriptorSet(gfdsr).getProtos();
        typeRegistryWrapper = TypeRegistryWrapper.from(fds);
        var extensionRegistry = ExtensionRegistryLite.getEmptyRegistry();
        enolaMessages = new EnolaMessages(typeRegistryWrapper, extensionRegistry);

        // See CommandWithModelAndOutput
        if (output == null || output.output.equals(Output.DEFAULT_OUTPUT_URI)) {
            resource = new WriterResource(spec.commandLine().getOut(), format.toMediaType());
        } else {
            resource =
                    rp.getWritableResource(
                            URIs.addMediaType(Output.get(output), format.toMediaType()));
        }

        run(service, iri);
    }

    protected abstract void run(EnolaServiceBlockingStub service, String eri) throws Exception;

    protected void write(Message thing) throws IOException {
        if (Format.Turtle.equals(format) || Format.JSONLD.equals(format)) {
            var model = new ModelBuilder().build();
            var statementCollector = new StatementCollector(model);
            if (thing instanceof Thing protoThing) {
                var vf = SimpleValueFactory.getInstance();
                new ProtoThingRdfConverter(vf).convertInto(protoThing, statementCollector);
            } else if (thing instanceof Things protoThings) {
                for (var protoThing : protoThings.getThingsList())
                    new ProtoThingRdfConverter().convertInto(protoThing, statementCollector);
            }
            new RdfWriterConverter().convertIntoOrThrow(model, resource);
            return;
        }

        if (Format.Graphviz.equals(format) && thing instanceof Things protoThings) {
            var javaThings = ProtoThings.proto2java(protoThings.getThingsList());
            new GraphvizGenerator(thingMetadataProvider).convertIntoOrThrow(javaThings, resource);
            return;
        }

        if (Format.GraphCommons.equals(format) && thing instanceof Things protoThings) {
            var javaThings = ProtoThings.proto2java(protoThings.getThingsList());
            new GraphCommonsJsonGenerator(thingMetadataProvider)
                    .convertIntoOrThrow(javaThings, resource);
            return;
        }

        // Otherwise
        new ProtoIO(typeRegistryWrapper.get()).write(thing, resource);
    }
}

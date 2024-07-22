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

import dev.enola.common.io.iri.namespace.NamespaceConverterWithRepository;
import dev.enola.common.io.iri.namespace.NamespaceRepositoryEnolaDefaults;
import dev.enola.common.io.metadata.MetadataProvider;
import dev.enola.common.io.resource.FileDescriptorResource;
import dev.enola.common.io.resource.stream.GlobResourceProviders;
import dev.enola.core.EnolaServiceProvider;
import dev.enola.core.grpc.EnolaGrpcClientProvider;
import dev.enola.core.grpc.EnolaGrpcInProcess;
import dev.enola.core.grpc.ServiceProvider;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.meta.proto.Type;
import dev.enola.core.proto.EnolaServiceGrpc.EnolaServiceBlockingStub;
import dev.enola.core.type.TypeRepositoryBuilder;
import dev.enola.data.ProviderFromIRI;
import dev.enola.data.Repository;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.datatype.DatatypeRepositoryBuilder;
import dev.enola.rdf.RdfResourceIntoThingConverter;
import dev.enola.thing.ThingMetadataProvider;
import dev.enola.thing.io.Loader;
import dev.enola.thing.io.ResourceIntoThingConverter;
import dev.enola.thing.message.ThingProviderAdapter;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.repo.ThingMemoryRepositoryROBuilder;
import dev.enola.thing.template.TemplateThingRepository;
import dev.enola.thing.validation.LoggingCollector;
import dev.enola.thing.validation.Validators;

import org.jspecify.annotations.Nullable;

import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.net.URI;

public abstract class CommandWithModel extends CommandWithResourceProvider {

    protected EnolaServiceProvider esp;

    @Spec CommandSpec spec;

    @ArgGroup(multiplicity = "1")
    ModelOrServer group;

    @CommandLine.Option(
            names = {"--validate"},
            negatable = true,
            required = true,
            defaultValue = "false", // TODO Enable Model validation by default
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            description = "Whether validation errors in loaded models should stop & exit")
    boolean validate;

    private EnolaServiceBlockingStub gRPCService;

    // TODO Turn remote service encapsulation upside down (as-is this "exception" is strange)
    protected TemplateThingRepository templateService;

    @Override
    public final void run() throws Exception {
        super.run();

        // TODO Fix design; as-is, this may stay null if --server instead of --model is used
        EntityKindRepository ekr = null;

        // TODO Move elsewhere for continuous ("shell") mode, as this is "expensive".
        ServiceProvider grpc = null;
        if (group.load != null) {
            // TODO Replace DatatypeRepository with store itself, once a Datatype is a Thing
            DatatypeRepository dtr = new DatatypeRepositoryBuilder().build();
            ThingMemoryRepositoryROBuilder store = new ThingMemoryRepositoryROBuilder();
            ResourceIntoThingConverter ritc = new RdfResourceIntoThingConverter(rp, dtr);
            var loader = new Loader(ritc);
            var fgrp = new GlobResourceProviders(rp);
            for (var globIRI : group.load) {
                try (var stream = fgrp.get(globIRI)) {
                    loader.convertIntoOrThrow(stream, store);
                }
            }

            var repo = store.build();
            var c = new LoggingCollector();
            var v = new Validators(repo);
            v.validate(repo, c);
            if (c.hasMessages()) {
                System.err.println("Loaded models have validation errors; use -v to show them");
                if (validate) {
                    System.exit(7);
                } else {
                    System.err.println("Use --no-validate to continue anyway");
                }
            }

            TemplateThingRepository templateThingRepository = new TemplateThingRepository(repo);
            templateService = templateThingRepository;
            // NB: Copy/pasted below...
            ekr = new EntityKindRepository();
            Repository<Type> tyr = new TypeRepositoryBuilder().build();
            esp = new EnolaServiceProvider(ekr, tyr, templateThingRepository, rp);
            var enolaService = esp.getEnolaService();
            grpc = new EnolaGrpcInProcess(esp, enolaService, false); // direct, single-threaded!
            gRPCService = grpc.get();

        } else if (group.model != null) {
            var modelResource = rp.getReadableResource(group.model);
            ekr = new EntityKindRepository();
            ekr.load(modelResource);
            // NB: Copy/paste from above...
            Repository<Type> tyr = new TypeRepositoryBuilder().build();
            esp = new EnolaServiceProvider(ekr, tyr, rp);
            var enolaService = esp.getEnolaService();
            grpc = new EnolaGrpcInProcess(esp, enolaService, false); // direct, single-threaded!
            gRPCService = grpc.get();

        } else if (group.server != null) {
            grpc = new EnolaGrpcClientProvider(group.server, false); // direct, single-threaded!
            gRPCService = grpc.get();
        }

        try {
            run(ekr, gRPCService);
        } finally {
            grpc.close();
        }
    }

    // TODO Move this to class EnolaProvider?
    protected MetadataProvider getMetadataProvider(ProviderFromIRI<Thing> thingProvider) {
        // TODO look up in global repository!
        var datatypeRepository = new DatatypeRepositoryBuilder().build();

        // TODO This must be configurable & dynamic...
        var namespaceRepo = NamespaceRepositoryEnolaDefaults.INSTANCE;
        var namespaceConverter = new NamespaceConverterWithRepository(namespaceRepo);

        return new ThingMetadataProvider(
                new ThingProviderAdapter(thingProvider, datatypeRepository), namespaceConverter);
    }

    // TODO Pass only the EnolaServiceBlockingStub through, remove the EntityKindRepository from
    // here
    protected abstract void run(EntityKindRepository ekr, EnolaServiceBlockingStub service)
            throws Exception;

    static class LoadableModelURIs {

        @Option(
                names = {"--load", "-l"},
                required = true,
                description = "URI Glob of Models to load (e.g. file:\"**.ttl\")")
        java.util.List<String> load;
    }

    static class ModelOrServer extends LoadableModelURIs {

        @Option(
                names = {"--model", "-m"},
                required = true,
                description = "URI to EntityKinds (e.g. file:model.yaml)")
        // TODO Simple integrate this into --load eventually...
        @Nullable URI model;

        @Option(
                names = {"--server", "-s"},
                required = true,
                description = "Target of an Enola gRPC Server (e.g. localhost:7070)")
        @Nullable String server;
    }

    static class Output {
        // Default command output destination is STDOUT.
        // NB: "fd:1" normally (in ResourceProviders) is FileDescriptorResource,
        // but CommandWithEntityID "hacks" this and uses WriterResource, for "testability".
        static final URI DEFAULT_OUTPUT_URI = FileDescriptorResource.STDOUT_URI;

        @Option(
                names = {"--output", "-o"},
                required = true,
                defaultValue = FileDescriptorResource.STDOUT,
                showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
                description = "URI (base) of where to write output/s")
        URI output;
    }
}

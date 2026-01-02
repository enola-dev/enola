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

import com.google.common.collect.ImmutableList;

import dev.enola.common.context.TLC;
import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.resource.FileDescriptorResource;
import dev.enola.common.io.resource.stream.GlobResolvers;
import dev.enola.core.EnolaServiceProvider;
import dev.enola.core.grpc.EnolaGrpcClientProvider;
import dev.enola.core.grpc.EnolaGrpcInProcess;
import dev.enola.core.grpc.ServiceProvider;
import dev.enola.core.proto.EnolaServiceGrpc.EnolaServiceBlockingStub;
import dev.enola.data.ProviderFromIRI;
import dev.enola.data.Trigger;
import dev.enola.data.iri.NamespaceConverter;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.infer.rdf.RDFSPropertyTrigger;
import dev.enola.thing.impl.MutableThing;
import dev.enola.thing.java.ProxyTBF;
import dev.enola.thing.java.TBF;
import dev.enola.thing.message.AlwaysThingProviderAdapter;
import dev.enola.thing.metadata.ThingMetadataProvider;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.repo.*;
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
import java.util.List;
import java.util.stream.Stream;

public abstract class CommandWithModel extends CommandWithResourceProviderAndLoader {

    protected EnolaServiceProvider esp;

    @Spec CommandSpec spec;
    @ArgGroup @Nullable ModelOrServer group;

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
        try (var ctx1 = TLC.open()) {
            setup(ctx1);

            if (group == null) {
                group = new ModelOrServer();
                group.load = List.of();
            }

            // TODO Move elsewhere for continuous ("shell") mode, as this is "expensive".
            ServiceProvider grpc = null;
            if (group.load != null) {
                ImmutableList<Trigger<? extends dev.enola.thing.Thing>> triggers =
                        ImmutableList.of(new RDFSPropertyTrigger());
                ThingMemoryRepositoryROBuilder store = new ThingMemoryRepositoryROBuilder(triggers);
                for (var trigger : triggers) {
                    ((ThingTrigger<?>) trigger).setRepo(store);
                }

                try (var ctx2 = TLC.open()) {
                    ctx2.push(ThingProvider.class, new AlwaysThingRepositoryStore(store));
                    ctx2.push(TBF.class, new ProxyTBF(MutableThing.FACTORY));
                    var loader = loader();
                    var fgrp = new GlobResolvers();
                    for (var globIRI : group.load) {
                        try (var stream = fgrp.get(globIRI)) {
                            loader.convertIntoOrThrow(stream, store);
                        }
                    }
                }
                var repo = store.build();

                if (validate) {
                    var c = new LoggingCollector();
                    var v = new Validators(repo);
                    v.validate(repo, c);
                    if (c.hasMessages()) {
                        System.err.println(
                                "Loaded models have validation errors; use -v to show them (or use"
                                        + " --no-validate to disable)");
                        System.exit(7);
                    }
                }

                TemplateThingRepository templateThingRepository = new TemplateThingRepository(repo);
                templateService = templateThingRepository;
                ThingsProvider thingsProvider =
                        new ThingsProvider() {
                            @Override
                            public Stream<dev.enola.thing.Thing> getThings(String iri) {
                                throw new UnsupportedOperationException("TODO");
                            }
                        };
                esp = new EnolaServiceProvider(thingsProvider, templateThingRepository, rp);
                var enolaService = esp.getEnolaService();
                grpc = new EnolaGrpcInProcess(esp, enolaService, false); // direct, single-threaded!
                gRPCService = grpc.get();

            } else if (group.server != null) {
                grpc = new EnolaGrpcClientProvider(group.server, false); // direct, single-threaded!
                gRPCService = grpc.get();
            }

            try {
                run(gRPCService);
            } finally {
                if (grpc != null) grpc.close();
            }
        }
    }

    // TODO Move this to class EnolaProvider?
    protected ThingMetadataProvider getMetadataProvider(ProviderFromIRI<Thing> thingProvider) {
        return new ThingMetadataProvider(
                new AlwaysThingProviderAdapter(thingProvider, DatatypeRepository.CTX),
                NamespaceConverter.CTX);
    }

    protected abstract void run(EnolaServiceBlockingStub service) throws Exception;

    static class LoadableModelURIs {

        @Option(
                names = {"--load", "-l"},
                description = "URI Glob of Models to load (e.g. file:\"**.ttl\")")
        /* TODO @Nullable */ java.util.List<String> load;
    }

    static class ModelOrServer extends LoadableModelURIs {

        @Option(
                names = {"--server", "-s"},
                required = true,
                description = "Target of an Enola gRPC Server (e.g. localhost:7070)")
        @Nullable String server;
    }

    static class Output {
        // Default command output destination is STDOUT.
        // NB: "fd:1" normally (in ResourceProviders) is FileDescriptorResource,
        // but CommandWithIRI "hacks" this and uses WriterResource, for "testability".
        static final URI DEFAULT_OUTPUT_URI = FileDescriptorResource.STDOUT_URI;

        @Option(
                names = {"--output", "-o"},
                required = true,
                defaultValue = FileDescriptorResource.STDOUT,
                showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
                description =
                        "URI (base) of where to write output/s; e.g. file:/tmp or "
                                + FileDescriptorResource.STDOUT)
        URI output;

        static URI get(Output output) {
            if (output == null) return FileDescriptorResource.STDOUT_URI;
            if (output != null && output.output == null) return FileDescriptorResource.STDOUT_URI;
            return URIs.absolutify(output.output);
        }
    }
}

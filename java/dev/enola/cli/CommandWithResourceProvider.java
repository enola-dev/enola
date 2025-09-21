/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import dev.enola.cas.IPFSGatewayResource;
import dev.enola.common.context.Context;
import dev.enola.common.io.hashbrown.IntegrityValidatingDelegatingResource;
import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.resource.*;
import dev.enola.data.iri.NamespaceConverter;
import dev.enola.data.iri.namespace.repo.NamespaceConverterWithRepository;
import dev.enola.data.iri.namespace.repo.NamespaceRepositoryEnolaDefaults;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.model.Datatypes;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.ProxyTBF;
import dev.enola.thing.java.TBF;

import picocli.CommandLine;

import java.nio.file.Paths;
import java.util.concurrent.Callable;

public abstract class CommandWithResourceProvider implements Callable<Integer> {

    @CommandLine.Option(
            names = {"--http-scheme"},
            negatable = true,
            required = true,
            defaultValue = "true",
            fallbackValue = "true",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            description = "Whether external HTTP requests are allowed")
    boolean http;

    @CommandLine.Option(
            names = {"--classpath-scheme"},
            negatable = true,
            required = true,
            defaultValue = "true",
            fallbackValue = "true",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            description =
                    "Whether classpath:/ resource scheme to access internal JAR content is allowed")
    boolean classpath;

    @CommandLine.Option(
            names = {"--file-scheme"},
            negatable = true,
            required = true,
            defaultValue = "true",
            fallbackValue = "true",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            description = "Whether file:/ resource scheme to access local filesystem is allowed")
    boolean file;

    @CommandLine.Option(
            hidden = true,
            names = {"--test-scheme"},
            negatable = true,
            required = true,
            defaultValue = "false",
            fallbackValue = "true",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            description = "Whether test:/ resource scheme is allowed")
    boolean test;

    @CommandLine.Option(
            names = {"--ipfs-gateway"},
            // Do NOT specify any defaultValue such as "http://localhost:8080/ipfs/" here,
            // nor "https://dweb.link/ipfs/" (that's worse, because non-local gateway require trust;
            // see https://docs.enola.dev/use/fetch/#ipfs).
            required = false,
            description = "See https://docs.enola.dev/use/fetch/#ipfs")
    String ipfsGateway;

    protected ResourceProvider rp;

    @Override
    public Integer call() throws Exception {
        run();
        return 0;
    }

    protected void run() throws Exception {
        var builder = ImmutableList.<ResourceProvider>builder();
        builder.add(new TeapotResource.Provider());
        builder.add(new DataResource.Provider());
        builder.add(new MultibaseResource.Provider());
        builder.add(new EmptyResource.Provider());
        builder.add(new StringResource.Provider()); // TODO Remove...
        if (file) {
            builder.add(new FileResource.Provider());
            builder.add(new FileDescriptorResource.Provider());
        }
        if (http) builder.add(new OkHttpResource.Provider());
        if (!Strings.isNullOrEmpty(ipfsGateway)) {
            var httpResourceProvider = new OkHttpResource.Provider();
            builder.add(new IPFSGatewayResource.Provider(httpResourceProvider, ipfsGateway));
        }
        if (test) builder.add(new TestResource.Provider());
        if (classpath) builder.add(new ClasspathResource.Provider());

        var original = new ResourceProviders(builder.build());
        rp = new IntegrityValidatingDelegatingResource.Provider(original);
    }

    protected void setup(Context ctx) {
        // NB: Singleton are set in class Configuration
        // TODO Move this entirely into class Configuration...

        ctx.push(ResourceProvider.class, rp);
        ctx.push(URIs.ContextKeys.BASE, Paths.get("").toUri());

        // TODO Replace DatatypeRepository with store itself, once a Datatype is a Thing
        DatatypeRepository dtr = Datatypes.DTR;
        ctx.push(DatatypeRepository.class, dtr);

        // TODO This must be configurable & dynamic... but shouldn't be configured in package cli?
        var namespaceRepo = NamespaceRepositoryEnolaDefaults.INSTANCE;
        var namespaceConverter = new NamespaceConverterWithRepository(namespaceRepo);
        ctx.push(NamespaceConverter.class, namespaceConverter);

        ctx.push(TBF.class, new ProxyTBF(ImmutableThing.FACTORY));
    }
}

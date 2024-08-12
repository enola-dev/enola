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
package dev.enola.cli;

import com.google.common.collect.ImmutableList;

import dev.enola.common.context.Context;
import dev.enola.common.function.CheckedRunnable;
import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.iri.namespace.NamespaceConverter;
import dev.enola.common.io.iri.namespace.NamespaceConverterWithRepository;
import dev.enola.common.io.iri.namespace.NamespaceRepositoryEnolaDefaults;
import dev.enola.common.io.resource.*;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.model.Datatypes;

import picocli.CommandLine;

import java.nio.file.Paths;

public abstract class CommandWithResourceProvider implements CheckedRunnable {

    @CommandLine.Option(
            names = {"--http-scheme"},
            negatable = true,
            required = true,
            defaultValue = "false",
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

    protected ResourceProvider rp;

    @Override
    public void run() throws Exception {
        var builder = ImmutableList.<ResourceProvider>builder();
        builder.add(new EmptyResource.Provider());
        builder.add(new StringResource.Provider());
        if (file) {
            builder.add(new FileResource.Provider());
            builder.add(new FileDescriptorResource.Provider());
        }
        if (http) builder.add(new OkHttpResource.Provider());
        if (test) builder.add(new TestResource.Provider());
        if (classpath) builder.add(new ClasspathResource.Provider());
        rp = new ResourceProviders(builder.build());
    }

    protected void setup(Context ctx) {
        ctx.push(ResourceProvider.class, rp);
        ctx.push(URIs.ContextKeys.BASE, Paths.get("").toUri());

        // TODO Replace DatatypeRepository with store itself, once a Datatype is a Thing
        DatatypeRepository dtr = Datatypes.DTR;
        ctx.push(DatatypeRepository.class, dtr);

        // TODO This must be configurable & dynamic...
        var namespaceRepo = NamespaceRepositoryEnolaDefaults.INSTANCE;
        var namespaceConverter = new NamespaceConverterWithRepository(namespaceRepo);
        ctx.push(NamespaceConverter.class, namespaceConverter);
    }
}

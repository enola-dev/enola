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

import static dev.enola.common.io.resource.FileDescriptorResource.STDOUT_URI;

import dev.enola.common.canonicalize.Canonicalizer;
import dev.enola.common.function.MoreStreams;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.stream.GlobResourceProviders;
import dev.enola.common.io.resource.stream.WritableResourcesProvider;

import picocli.CommandLine;

import java.io.IOException;

@CommandLine.Command(
        name = "canonicalize",
        description = {
            "Canonicalize Resources",
            "using e.g. RFC 8785 JSON Canonicalization Scheme (JCS) inspired approach",
            "(but this implementation is currently not yet fully compliant with that RFC)"
        })
public class CanonicalizeCommand extends CommandWithResourceProvider {

    // TODO Merge this completely with RosettaCommand ?!

    @CommandLine.ArgGroup(multiplicity = "1")
    CommandWithModel.LoadableModelURIs resources;

    @CommandLine.ArgGroup CommandWithModel.Output output;

    @CommandLine.Option(
            names = {"--pretty"},
            negatable = true,
            required = true,
            defaultValue = "false",
            fallbackValue = "true",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
            description = "Whether to 'pretty print' (format) output")
    boolean pretty;

    private WritableResourcesProvider wrp;

    @Override
    public void run() throws Exception {
        super.run();

        wrp = new WritableResourcesProvider(rp);
        var fgrp = new GlobResourceProviders(rp);
        for (var globIRI : resources.load) {
            try (var stream = fgrp.get(globIRI)) {
                MoreStreams.forEach(stream, r -> canonicalize(r));
            }
        }
    }

    private void canonicalize(ReadableResource r) throws IOException {
        var out = wrp.getWritableResource(output != null ? output.output : STDOUT_URI, r.uri());
        Canonicalizer.canonicalize(r, out, pretty);
    }
}

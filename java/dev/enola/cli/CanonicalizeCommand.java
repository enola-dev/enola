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

import dev.enola.common.canonicalize.Canonicalizer;
import dev.enola.common.context.TLC;
import dev.enola.common.function.MoreStreams;
import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.resource.DelegatingResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.stream.GlobResolvers;
import dev.enola.common.io.resource.stream.WritableResourcesProvider;

import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Paths;

@CommandLine.Command(
        name = "canonicalize",
        description = {
            "Canonicalize (AKA normalize) resources",
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
    private Canonicalizer canonicalizer;

    @Override
    public void run() throws Exception {
        super.run();
        try (var ctx = TLC.open().push(URIs.ContextKeys.BASE, Paths.get("").toUri())) {
            wrp = new WritableResourcesProvider(rp);
            canonicalizer = new Canonicalizer(rp);

            var fgrp = new GlobResolvers();
            for (var globIRI : resources.load) {
                try (var stream = fgrp.get(globIRI)) {
                    MoreStreams.forEach(stream, uri -> canonicalize(rp.getResource(uri)));
                }
            }
        }
    }

    private void canonicalize(ReadableResource r) throws IOException {
        var out = wrp.getWritableResource(CommandWithModel.Output.get(output), r.uri());
        out = new DelegatingResource(out, r.mediaType().withoutParameters());
        canonicalizer.canonicalize(r, out, pretty);
    }
}

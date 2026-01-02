/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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

import dev.enola.core.proto.EnolaServiceGrpc;
import dev.enola.web.EnolaThingProvider;

import picocli.CommandLine;

@CommandLine.Command(name = "metadata", description = "Provides metadata for a given IRI")
public class MetadataCommand extends CommandWithModel {

    @CommandLine.Parameters(index = "0", paramLabel = "iri", description = "IRI")
    String iri;

    @Override
    protected void run(EnolaServiceGrpc.EnolaServiceBlockingStub service) throws Exception {
        var mdp = getMetadataProvider(new EnolaThingProvider(service));

        var metadata = mdp.get(iri);
        spec.commandLine().getOut().println(metadata);
    }
}

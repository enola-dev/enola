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

import static dev.enola.common.protobuf.ProtobufMediaTypes.setProtoMessageFQN;

import dev.enola.common.context.TLC;
import dev.enola.common.io.resource.DelegatingResource;
import dev.enola.core.rosetta.Rosetta;
import dev.enola.model.enola.files.FileThingConverter;
import dev.enola.rdf.io.RdfResourceIntoThingConverter;
import dev.enola.thing.io.Loader;
import dev.enola.thing.io.UriIntoThingConverters;

import picocli.CommandLine;

import java.net.URI;

@CommandLine.Command(
        name = "rosetta",
        description = {
            "Transform YAML <=> JSON <=> TextProto <=> PB <=> RDF TTL <=> JSON-LD etc.",
            "(see https://en.wikipedia.org/wiki/Rosetta_Stone)"
        })
public class RosettaCommand extends CommandWithResourceProvider {

    private Rosetta rosetta;

    @CommandLine.Option(
            names = {"--protoFQN"},
            required = false,
            description = "Proto FQN; optional for YAML <=> JSON, required if --in is *.textproto")
    String protoFQN;

    @CommandLine.Option(
            names = {"--in"},
            required = true,
            description = "URI to Input (e.g. file:model.json)")
    URI in;

    @CommandLine.Option(
            names = {"--out", "--output", "-o"},
            required = true,
            description = "URI to Output (e.g. file:model.yaml)")
    URI out;

    @Override
    public void run() throws Exception {
        super.run();
        try (var ctx = TLC.open()) {
            setup(ctx);

            // TODO Explicitly add UriIntoThingConverter, depending on CLI feature flags
            UriIntoThingConverters ritc =
                    new UriIntoThingConverters(
                            new RdfResourceIntoThingConverter<>(), new FileThingConverter());
            var loader = new Loader(ritc);

            rosetta = new Rosetta(rp, loader);
            var inResource = rp.getReadableResource(in);
            var outResource = rp.getWritableResource(out);

            if (protoFQN != null) {
                // required if in is a *.textproto to determine its type
                // until header sniffing is implemented
                var mt = setProtoMessageFQN(inResource.mediaType(), protoFQN);
                inResource = new DelegatingResource(inResource, mt);
            }

            rosetta.convertIntoOrThrow(inResource, outResource);
        }
    }
}

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

import dev.enola.common.io.resource.DelegatingResource;
import dev.enola.common.protobuf.ProtobufMediaTypes;
import dev.enola.core.rosetta.Rosetta;

import picocli.CommandLine;

import java.net.URI;

@CommandLine.Command(
        name = "rosetta",
        description = {
            "Transform YAML <=> JSON <=> TextProto",
            "(see https://en.wikipedia.org/wiki/Rosetta_Stone)"
        })
public class RosettaCommand extends CommandWithResourceProvider {

    private final Rosetta rosetta = new Rosetta();

    @CommandLine.Option(
            names = {"--schema"},
            required = false,
            description =
                    "Schema (${COMPLETION-CANDIDATES}); optional for YAML <=> JSON, required if"
                            + " --in is *.textproto")
    Schema schema;

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

        var inResource = rp.getReadableResource(in);
        var outResource = rp.getWritableResource(out);

        if (schema != null) {
            // required if in is a *.textproto to determine its type (until header sniffing is
            // implemented)
            var mt = ProtobufMediaTypes.setProtoMessageFQN(inResource.mediaType(), schema.protoFQN);
            inResource = new DelegatingResource(inResource, mt);
        }

        rosetta.convertIntoOrThrow(inResource, outResource);
    }

    public enum Schema {
        Entity("dev.enola.core.Entity"),
        EntityKinds("dev.enola.core.meta.EntityKinds");

        final String protoFQN;

        Schema(String protoFQN) {
            this.protoFQN = protoFQN;
        }
    }
}

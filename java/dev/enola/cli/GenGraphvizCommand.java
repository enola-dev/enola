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

import static dev.enola.core.thing.ListThingService.ENOLA_ROOT_LIST_THINGS;

import dev.enola.core.proto.EnolaServiceGrpc.EnolaServiceBlockingStub;
import dev.enola.core.proto.GetThingRequest;
import dev.enola.thing.gen.graphviz.GraphvizGenerator;
import dev.enola.thing.message.MoreThings;
import dev.enola.thing.message.ProtoThings;
import dev.enola.web.EnolaThingProvider;

import picocli.CommandLine;

@CommandLine.Command(name = "graphviz", description = "Generate Graphviz Graphs from Things")
public class GenGraphvizCommand extends CommandWithModelAndOutput {

    @Override
    protected void run(EnolaServiceBlockingStub service) throws Exception {
        var thingMetadataProvider = getMetadataProvider(new EnolaThingProvider(service));
        var graphvizGenerator = new GraphvizGenerator(thingMetadataProvider);

        var request = GetThingRequest.newBuilder().setIri(ENOLA_ROOT_LIST_THINGS).build();
        var response = service.getThing(request);
        var protoThings = MoreThings.fromAny(response.getThing());
        var javaThings = ProtoThings.proto2java(protoThings);

        var base = Output.get(output);
        var graphviz = base.resolve("graphviz.gv");

        graphvizGenerator.convertIntoOrThrow(javaThings, rp.getWritableResource(graphviz));
    }
}

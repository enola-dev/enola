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

import dev.enola.core.proto.EnolaServiceGrpc.EnolaServiceBlockingStub;
import dev.enola.core.proto.GetThingRequest;

import picocli.CommandLine.Command;

@Command(name = "get", description = "Get Thing")
public class GetCommand extends CommandWithIRI {

    @Override
    protected void run(EnolaServiceBlockingStub service, String iri) throws Exception {
        var request = GetThingRequest.newBuilder().setIri(iri).build();
        var response = service.getThing(request);
        if (response.hasThing()) {
            var any = response.getThing();
            var message = enolaMessages.toMessage(any);
            write(message);
        } else {
            spec.commandLine().getErr().println(iri + " has nothing!");
            spec.commandLine().getErr().flush();
        }
        spec.commandLine().getOut().flush();
    }
}

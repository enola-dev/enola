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

import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.EnolaServiceGrpc.EnolaServiceBlockingStub;
import dev.enola.core.proto.GetEntityRequest;

import picocli.CommandLine.Command;

@Command(name = "get", description = "Get Entity")
public class Get extends CommandWithEntityID {

    @Override
    protected void run(EnolaServiceBlockingStub service, EntityKind ek, String eri)
            throws Exception {
        var request = GetEntityRequest.newBuilder().setEri(eri).build();
        var response = service.getEntity(request);
        var entity = response.getEntity();

        write(entity);
        out.flush();
    }
}

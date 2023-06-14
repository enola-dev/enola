/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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

import dev.enola.core.EnolaService;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.ID;
import dev.enola.core.proto.ListEntitiesRequest;

import picocli.CommandLine.Command;

@Command(name = "list", description = "List Entities")
public class List extends CommandWithEntityID {

    // With EntityKind name asks connector to list (first N?) entity IDs
    // With path asks connector, and behavior is connector specific; FileRepoConnector appends a *

    @Override
    protected void run(EntityKindRepository ekr, EnolaService service, EntityKind ek, ID id)
            throws Exception {
        // TODO Add CLI support for related_filter
        var request = ListEntitiesRequest.newBuilder().setId(id).build();
        var response = service.listEntities(request);

        for (var entity : response.getEntitiesList()) {
            write(ek, entity);
        }
    }
}

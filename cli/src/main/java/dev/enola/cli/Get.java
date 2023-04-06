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
import dev.enola.core.proto.GetEntityRequest;
import dev.enola.core.proto.ID;

import picocli.CommandLine.Command;

@Command(name = "get", description = "Get Entity")
public class Get extends CommandWithEntityID {

    @Override
    protected void run(EntityKindRepository ekr, EnolaService service, ID id) throws Exception {
        var request = GetEntityRequest.newBuilder().setId(id).build();
        var response = service.getEntity(request);
        var entity = response.getEntity();

        // TODO Allow formatting it as JSON or YAML, via Resource!
        // TODO What else do we do with the Entity?!
        out.println(entity);
    }
}

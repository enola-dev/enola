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
package dev.enola.core.meta;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.protobuf.ValidationException;
import dev.enola.core.EnolaException;
import dev.enola.core.EnolaService;
import dev.enola.core.EnolaServiceProvider;
import dev.enola.core.IDs;
import dev.enola.core.proto.GetEntityRequest;
import dev.enola.core.proto.ID;
import dev.enola.core.proto.ListEntitiesRequest;

import org.junit.Test;

public class SchemaAspectTest {

    EntityKindRepository ekr = new EntityKindRepository();
    EnolaService service = new EnolaServiceProvider().get(ekr);
    ID.Builder schemaKindID = SchemaAspect.idBuilderTemplate;

    public SchemaAspectTest() throws ValidationException, EnolaException {}

    @Test
    public void list() throws ValidationException, EnolaException {
        var eri = IDs.toPath(schemaKindID);
        var request = ListEntitiesRequest.newBuilder().setEri(eri).build();
        var response = service.listEntities(request);

        assertThat(response.getEntitiesList().size()).isAtLeast(38);
    }

    @Test
    public void get() throws ValidationException, EnolaException {
        var id = schemaKindID.clone().addPaths("google.protobuf.Timestamp");
        var eri = IDs.toPath(id);
        var request = GetEntityRequest.newBuilder().setEri(eri).build();
        var response = service.getEntity(request);

        assertThat(response.getEntity().getDataOrThrow("proto")).isNotNull();
    }
}

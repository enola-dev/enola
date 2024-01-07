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
package dev.enola.core.grpc;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.protobuf.ValidationException;
import dev.enola.core.EnolaException;
import dev.enola.core.EnolaService;
import dev.enola.core.EnolaServiceProvider;
import dev.enola.core.IDs;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.proto.EnolaServiceGrpc;
import dev.enola.core.proto.GetEntityRequest;
import dev.enola.core.proto.ID;

import org.junit.Test;

import java.io.IOException;

public class EnolaGrpcServerTest {

    private final ReadableResource model = new ClasspathResource("demo-model.yaml");
    private final EntityKindRepository ekr = new EntityKindRepository().load(model);
    private final EnolaServiceProvider esp = new EnolaServiceProvider();
    private final EnolaService service;

    public EnolaGrpcServerTest() throws ValidationException, IOException, EnolaException {
        service = esp.get(ekr);
    }

    @Test
    public void remoting() throws Exception {
        try (var enolaServer = new EnolaGrpcServer(esp, service).start(0)) {
            // similarly in dev.enola.demo.ServerTest
            var port = enolaServer.getPort();
            var endpoint = "localhost:" + port;
            try (var enolaClient = new EnolaGrpcClientProvider(endpoint, false)) {
                check(enolaClient.get());
            }
        }
    }

    @Test
    public void inProcess() throws Exception {
        try (var enolaServer = new EnolaGrpcInProcess(esp, service, false)) {
            check(enolaServer.get());
        }
    }

    private void check(EnolaServiceGrpc.EnolaServiceBlockingStub client) {
        var id = ID.newBuilder().setNs("demo").setEntity("bar").addPaths("a").addPaths("b").build();
        var eri = IDs.toPath(id);
        var request = GetEntityRequest.newBuilder().setEri(eri).build();
        var response = client.getEntity(request);
        assertThat(response.getEntity().getLinkMap()).hasSize(1);
    }
}

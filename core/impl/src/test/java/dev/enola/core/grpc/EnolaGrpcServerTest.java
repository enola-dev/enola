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

import com.google.protobuf.InvalidProtocolBufferException;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.protobuf.ValidationException;
import dev.enola.core.EnolaException;
import dev.enola.core.EnolaService;
import dev.enola.core.EnolaServiceProvider;
import dev.enola.core.IDs;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.proto.EnolaServiceGrpc;
import dev.enola.core.proto.EnolaServiceGrpc.EnolaServiceBlockingStub;
import dev.enola.core.proto.Entity;
import dev.enola.core.proto.GetThingRequest;
import dev.enola.core.proto.ID;
import dev.enola.core.proto.ListEntitiesRequest;

import org.junit.Test;

import java.io.IOException;

public class EnolaGrpcServerTest {

    private final ReadableResource model = new ClasspathResource("demo-model.yaml");
    private final EntityKindRepository ekr = new EntityKindRepository().load(model);
    private final EnolaServiceProvider esp = new EnolaServiceProvider(ekr);
    private final EnolaService service;

    public EnolaGrpcServerTest() throws ValidationException, IOException, EnolaException {
        service = esp.getEnolaService();
    }

    @Test
    public void remoting() throws Exception {
        try (var enolaServer = new EnolaGrpcServer(esp, service)) {
            // similarly in dev.enola.demo.ServerTest
            enolaServer.start(0);
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

    private void check(EnolaServiceGrpc.EnolaServiceBlockingStub client)
            throws InvalidProtocolBufferException {
        checkGet(client);
        checkList(client);
    }

    private void checkGet(EnolaServiceBlockingStub client) throws InvalidProtocolBufferException {
        var id = ID.newBuilder().setNs("demo").setEntity("bar").addPaths("a").addPaths("b").build();
        var eri = IDs.toPath(id);
        var request = GetThingRequest.newBuilder().setEri(eri).build();
        var response = client.getThing(request);
        var any = response.getThing();
        var entity = any.unpack(Entity.class);
        var linkMap = entity.getLinkMap();
        assertThat(linkMap).hasSize(1);
        assertThat(linkMap.get("wiki"))
                .isEqualTo("https://en.wikipedia.org/w/index.php?fulltext=Search&search=b");
    }

    private void checkList(EnolaServiceBlockingStub client) {
        var id = ID.newBuilder().setNs("demo").setEntity("bar").addPaths("a").addPaths("b").build();
        var eri = IDs.toPath(id);
        var request = ListEntitiesRequest.newBuilder().setEri(eri).build();
        var response = client.listEntities(request);
        assertThat(response.getEntitiesList()).isEmpty();
        // TODO Make this more interesting!
    }
}

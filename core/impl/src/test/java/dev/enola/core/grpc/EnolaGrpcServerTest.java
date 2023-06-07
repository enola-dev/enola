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
package dev.enola.core.grpc;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.core.EnolaServiceProvider;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.proto.EnolaServiceGrpc;
import dev.enola.core.proto.GetEntityRequest;
import dev.enola.core.proto.ID;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class EnolaGrpcServerTest {
    @Test
    public void grpc() throws Exception {
        var model = new ClasspathResource("demo-model.yaml");
        var ekr = new EntityKindRepository().load(model);
        var service = new EnolaServiceProvider().get(ekr);
        try (var enolaServer = new EnolaGrpcServer(service).start(0)) {
            // similarly in dev.enola.demo.ServerTest
            var port = enolaServer.getPort();
            var endpoint = "localhost:" + port;
            var credz = InsecureChannelCredentials.create();
            ManagedChannel channel = Grpc.newChannelBuilder(endpoint, credz).build();
            var client = EnolaServiceGrpc.newBlockingStub(channel);

            var id =
                    ID.newBuilder()
                            .setNs("demo")
                            .setEntity("bar")
                            .addPaths("a")
                            .addPaths("b")
                            .build();
            var request = GetEntityRequest.newBuilder().setId(id).build();
            var response = client.getEntity(request);
            assertThat(response.getEntity().getLinkMap()).hasSize(1);

            channel.shutdownNow().awaitTermination(3, TimeUnit.SECONDS);
        }
    }
}

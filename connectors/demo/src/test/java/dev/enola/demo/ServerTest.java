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
package dev.enola.demo;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.core.connector.proto.AugmentRequest;
import dev.enola.core.connector.proto.ListRequest;
import dev.enola.core.connector.proto.ConnectorServiceGrpc;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ServerTest {
    @Test
    public void testServer() throws IOException, InterruptedException {
        try (var server = new Server().start(0)) {
            var endpoint = "localhost:" + server.getPort();
            var credz = InsecureChannelCredentials.create();
            ManagedChannel channel = Grpc.newChannelBuilder(endpoint, credz).build();
            var client = ConnectorServiceGrpc.newBlockingStub(channel);

            var augmentRequest = AugmentRequest.newBuilder().build();
            var augmentResponse = client.augment(augmentRequest);
            assertThat(augmentResponse.getEntity().getLinkOrThrow("link1"))
                    .isEqualTo("http://www.vorburger.ch");

            var listRequest = ListRequest.newBuilder().build();
            var listResponse = client.list(listRequest);
            assertThat(listResponse.getEntityList().size())
                    .isEqualTo(2);
            assertThat(listResponse.getEntityList().get(0).getLinkOrThrow("link1"))
                    .isEqualTo("http://www.vorburger.ch");
            assertThat(listResponse.getEntityList().get(1).getLinkOrThrow("link1"))
                    .isEqualTo("http://www.vorburgerag.ch");

            channel.shutdownNow().awaitTermination(3, TimeUnit.SECONDS);
        }
    }
}

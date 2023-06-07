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

import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.common.io.resource.StringResource;
import dev.enola.common.protobuf.ValidationException;
import dev.enola.core.EnolaException;
import dev.enola.core.EnolaService;
import dev.enola.core.EnolaServiceProvider;
import dev.enola.core.connector.proto.AugmentRequest;
import dev.enola.core.connector.proto.ConnectorServiceGrpc;
import dev.enola.core.connector.proto.ConnectorServiceListRequest;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.proto.GetEntityRequest;
import dev.enola.core.proto.ID;
import dev.enola.core.proto.ListEntitiesRequest;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

public class ServerTest {
    @Test
    public void bothConnectorDirectlyAndViaServer()
            throws IOException, InterruptedException, ValidationException, EnolaException {
        try (var connectorServer = new Server().start(0)) {
            // similarly in dev.enola.core.grpc.EnolaGrpcServerTest
            var port = connectorServer.getPort();
            var endpoint = "localhost:" + port;
            var credz = InsecureChannelCredentials.create();
            ManagedChannel channel = Grpc.newChannelBuilder(endpoint, credz).build();
            var client = ConnectorServiceGrpc.newBlockingStub(channel);

            // Test the Demo Connector directly
            checkConnectorAugment(client);
            checkConnectorList(client);

            // Test Demo Connector through invoking Enola Core
            var enola = createEnolaService(port);
            checkEnolaGet(enola);
            checkEnolaList(enola);

            channel.shutdownNow().awaitTermination(3, TimeUnit.SECONDS);
        }
    }

    private void checkConnectorAugment(ConnectorServiceGrpc.ConnectorServiceBlockingStub client) {
        var request = AugmentRequest.newBuilder().build();
        var response = client.augment(request);
        assertThat(response.getEntity().getLinkOrThrow("link1"))
                .isEqualTo("http://www.vorburger.ch");
    }

    private void checkConnectorList(ConnectorServiceGrpc.ConnectorServiceBlockingStub client) {
        var request = ConnectorServiceListRequest.newBuilder().build();
        var response = client.list(request);
        assertThat(response.getEntitiesList().size()).isEqualTo(2);
        assertThat(response.getEntitiesList().get(0).getLinkOrThrow("link1"))
                .isEqualTo("http://www.vorburger.ch");
        assertThat(response.getEntitiesList().get(1).getLinkOrThrow("link1"))
                .isEqualTo("https://enola.dev");
    }

    private EnolaService createEnolaService(int port) throws ValidationException, IOException {
        // As in dev.enola.cli.CommandWithModel + dev.enola.cli.CommandWithEntityID
        var model = URI.create("classpath:demo-model.textproto");
        var modelResourceTemplate = new ResourceProviders().getReadableResource(model);
        var modelResourceText = modelResourceTemplate.charSource().read();
        modelResourceText = modelResourceText.replace("PORT", Integer.toString(port));
        var modelResource =
                new StringResource(modelResourceText, modelResourceTemplate.mediaType());
        var ekr = new EntityKindRepository();
        ekr.load(modelResource);
        return new EnolaServiceProvider().get(ekr);
    }

    private void checkEnolaGet(EnolaService enola) throws EnolaException {
        var id = ID.newBuilder().setNs("demo").setEntity("foo").addPaths("hello").build();
        var request = GetEntityRequest.newBuilder().setId(id).build();
        var response = enola.getEntity(request);
        assertThat(response.getEntity().getLinkOrThrow("link1"))
                .isEqualTo("http://www.vorburger.ch");
    }

    private void checkEnolaList(EnolaService enola) throws EnolaException {
        var id = ID.newBuilder().setNs("demo").setEntity("foo").build();
        var request = ListEntitiesRequest.newBuilder().setId(id).build();
        var response = enola.listEntities(request);
        var entities = response.getEntitiesList();

        assertThat(entities.size()).isEqualTo(2);
        assertThat(entities.get(0).getLinkOrThrow("link1")).isEqualTo("http://www.vorburger.ch");
        assertThat(entities.get(1).getLinkOrThrow("link1")).isEqualTo("https://enola.dev");
    }
}

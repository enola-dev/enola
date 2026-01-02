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
package dev.enola.demo;

import static com.google.common.truth.Truth.assertThat;

import static java.util.concurrent.TimeUnit.SECONDS;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.MemoryResource;
import dev.enola.common.io.resource.ReplacingResource;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.common.protobuf.ProtobufMediaTypes;
import dev.enola.common.protobuf.TypeRegistryWrapper;
import dev.enola.common.protobuf.ValidationException;
import dev.enola.core.EnolaException;
import dev.enola.core.EnolaService;
import dev.enola.core.EnolaServiceProvider;
import dev.enola.core.connector.proto.AugmentRequest;
import dev.enola.core.connector.proto.ConnectorServiceGrpc;
import dev.enola.core.connector.proto.ConnectorServiceListRequest;
import dev.enola.core.proto.Entity;
import dev.enola.core.proto.GetThingRequest;
import dev.enola.core.proto.ID;
import dev.enola.core.proto.ListEntitiesRequest;
import dev.enola.demo.proto.Something;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

import org.junit.Test;

import java.io.IOException;

public class ServerTest {

    private EntityKindRepository ekr;
    private EnolaService enola;
    private TypeRegistryWrapper typeRegistryWrapper;

    @Test
    public void bothConnectorDirectlyAndViaServer()
            throws IOException, InterruptedException, ValidationException, EnolaException {
        try (var connectorServer = new Server()) {
            // similarly in dev.enola.core.grpc.EnolaGrpcServerTest
            connectorServer.start(0);
            var port = connectorServer.getPort();
            var endpoint = "localhost:" + port;
            var credz = InsecureChannelCredentials.create();
            ManagedChannel channel = Grpc.newChannelBuilder(endpoint, credz).build();
            var client =
                    ConnectorServiceGrpc.newBlockingStub(channel).withDeadlineAfter(7, SECONDS);

            // Test the Demo Connector directly
            checkConnectorAugment(client);
            checkConnectorList(client);

            // Test Demo Connector through invoking Enola Core
            createEnolaService(port);
            checkEnolaGet(enola);
            checkEnolaList(enola);

            channel.shutdownNow().awaitTermination(7, SECONDS);
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

    private void createEnolaService(int port)
            throws ValidationException, IOException, EnolaException {
        var sPort = Integer.toString(port);
        var model =
                new ReplacingResource(
                        new ClasspathResource("demo-connector-model.textproto"), "9090", sPort);
        // As in dev.enola.cli.CommandWithModel + dev.enola.cli.CommandWithIRI
        ekr = new EntityKindRepository();
        ekr.load(model);

        var esp = new EnolaServiceProvider(ekr);
        enola = esp.getEnolaService();
        typeRegistryWrapper = esp.getTypeRegistryWrapper();
    }

    private void checkEnolaGet(EnolaService enola) throws EnolaException, IOException {
        var id = ID.newBuilder().setNs("demo").setEntity("foo").addPaths("hello").build();
        var eri = IDs.toPath(id);
        var request = GetThingRequest.newBuilder().setIri(eri).build();
        var response = enola.getThing(request);
        var thing = response.getThing();
        Entity entity = thing.unpack(Entity.class);
        assertThat(entity.getLinkOrThrow("link1")).isEqualTo("http://www.vorburger.ch");

        var any = entity.getDataOrThrow("data1");
        var something = any.unpack(Something.class);
        assertThat(something.getText()).isEqualTo("hello, world");
        assertThat(something.getNumber()).isEqualTo(123);

        var io = new ProtoIO(typeRegistryWrapper.get());
        var resource = new MemoryResource(ProtobufMediaTypes.PROTOBUF_YAML_UTF_8);
        io.write(entity, resource);
        assertThat(resource.charSource().read()).contains("text: 'hello, world'");
    }

    private void checkEnolaList(EnolaService enola) throws EnolaException {
        var id = ID.newBuilder().setNs("demo").setEntity("foo").build();
        var eri = IDs.toPath(id);
        var request = ListEntitiesRequest.newBuilder().setEri(eri).build();
        var response = enola.listEntities(request);
        var entities = response.getEntitiesList();

        assertThat(entities.size()).isEqualTo(2);
        assertThat(entities.get(0).getLinkOrThrow("link1")).isEqualTo("http://www.vorburger.ch");
        assertThat(entities.get(1).getLinkOrThrow("link1")).isEqualTo("https://enola.dev");
    }
}

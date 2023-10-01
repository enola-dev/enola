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
package dev.enola.web.rest;

import static com.google.common.truth.Truth.assertThat;

import static java.net.URI.create;

import com.google.common.net.MediaType;

import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.common.protobuf.Timestamps2;
import dev.enola.core.EnolaException;
import dev.enola.core.EnolaService;
import dev.enola.core.grpc.EnolaGrpcInProcess;
import dev.enola.core.proto.*;
import dev.enola.web.sun.SunServer;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.time.Instant;

public class RestTest {

    @Test
    public void getAndList() throws Exception {
        var addr = new InetSocketAddress(0);
        try (var server = new SunServer(addr)) {
            // Setup
            try (EnolaGrpcInProcess grpc = new EnolaGrpcInProcess(new TestService())) {
                var testGrpcService = grpc.get();
                new RestAPI(testGrpcService).register(server);
                server.start();
                var rp = new ResourceProviders();
                var port = server.getInetAddress().getPort();
                var prefix = "http://localhost:" + port;

                // Get
                var uri1 = create(prefix + "/api/entity/test.demo/123");
                var response1 = rp.getResource(uri1);
                assertThat(response1.charSource().read())
                        .startsWith(
                                "{\"id\":{\"ns\":\"test\",\"entity\":\"demo\",\"paths\":[\"123\"]},\"ts\":\"");
                assertThat(response1.mediaType()).isEqualTo(MediaType.JSON_UTF_8);

                // List
                var uri2 = create(prefix + "/api/entities/test.demo");
                var response2 = rp.getResource(uri2);
                assertThat(response1.charSource().read())
                        .startsWith(
                                "{\"id\":{\"ns\":\"test\",\"entity\":\"demo\",\"paths\":[\"123\"]},\"ts\":\"");
                assertThat(response1.mediaType()).isEqualTo(MediaType.JSON_UTF_8);
            }
        }
    }

    private static class TestService implements EnolaService {
        @Override
        public GetEntityResponse getEntity(GetEntityRequest r) throws EnolaException {
            return GetEntityResponse.newBuilder().setEntity(newEntity("123")).build();
        }

        @Override
        public ListEntitiesResponse listEntities(ListEntitiesRequest r) throws EnolaException {
            return ListEntitiesResponse.newBuilder()
                    .addEntities(newEntity("123"))
                    .addEntities(newEntity("456"))
                    .build();
        }

        private Entity newEntity(String path) {
            var id = ID.newBuilder().setNs("test").setEntity("demo").addPaths(path);
            var now = Timestamps2.fromInstant(Instant.now());
            return Entity.newBuilder().setId(id).setTs(now).build();
        }
    }
}

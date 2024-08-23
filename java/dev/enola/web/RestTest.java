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
package dev.enola.web;

import static com.google.common.truth.Truth.assertThat;

import static java.net.URI.create;

import com.google.common.net.MediaType;
import com.google.protobuf.Any;

import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.common.protobuf.Timestamps2;
import dev.enola.core.EnolaException;
import dev.enola.core.EnolaService;
import dev.enola.core.EnolaServiceProvider;
import dev.enola.core.grpc.EnolaGrpcInProcess;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.proto.*;
import dev.enola.web.netty.NettyHttpServer;

import org.junit.Test;

import java.time.Instant;

public class RestTest {

    @Test
    public void getAndList() throws Exception {
        // Setup
        var rp = new ResourceProviders();
        var ekr = new EntityKindRepository();
        var esp = new EnolaServiceProvider(ekr, rp);
        try (EnolaGrpcInProcess grpc = new EnolaGrpcInProcess(esp, new TestService(), false)) {
            var testGrpcService = grpc.get();
            var handlers = new WebHandlers().register("/api", new RestAPI(testGrpcService));
            try (var server = new NettyHttpServer(0, handlers)) {
                server.start();
                var port = server.getInetAddress().getPort();
                var prefix = "http://localhost:" + port;

                // Get
                var uri1 = create(prefix + "/api/entity/test.demo/123");
                var response1 = rp.getResource(uri1);
                assertThat(response1.charSource().read())
                        .startsWith(
                                "{\"@type\":\"type.googleapis.com/dev.enola.core.Entity\","
                                    + "\"id\":{\"ns\":\"test\",\"entity\":\"demo\",\"paths\":[\"123\"]},\"ts\":\"");
                assertThat(response1.mediaType()).isEqualTo(MediaType.JSON_UTF_8);

                // List
                var uri2 = create(prefix + "/api/entities/test.demo");
                var response2 = rp.getResource(uri2);
                assertThat(response2.charSource().read())
                        .startsWith(
                                "{\"id\":{\"ns\":\"test\",\"entity\":\"demo\",\"paths\":[\"123\"]},\"ts\":\"");
                assertThat(response1.mediaType()).isEqualTo(MediaType.JSON_UTF_8);
            }
        }
    }

    private static class TestService implements EnolaService {
        @Override
        public GetThingsResponse getThings(GetThingsRequest r) throws EnolaException {
            return GetThingsResponse.newBuilder().build();
        }

        @Override
        public GetThingResponse getThing(GetThingRequest r) throws EnolaException {
            return GetThingResponse.newBuilder().setThing(Any.pack(newEntity("123"))).build();
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

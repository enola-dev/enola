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
package dev.enola.web;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import static java.net.URI.create;

import com.google.common.net.MediaType;
import com.google.protobuf.Any;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.OkHttpResource;
import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.core.EnolaException;
import dev.enola.core.EnolaService;
import dev.enola.core.EnolaServiceProvider;
import dev.enola.core.grpc.EnolaGrpcInProcess;
import dev.enola.core.proto.*;
import dev.enola.thing.proto.Thing;
import dev.enola.web.netty.NettyHttpServer;

import org.junit.Rule;
import org.junit.Test;

public class RestTest {

    public @Rule SingletonRule r = $(MediaTypeProviders.set(new MediaTypeProviders()));

    @Test
    public void getAndList() throws Exception {
        // Setup
        var rp =
                new ResourceProviders(
                        new ClasspathResource.Provider(), new OkHttpResource.Provider());
        var esp = new EnolaServiceProvider(rp);
        try (EnolaGrpcInProcess grpc = new EnolaGrpcInProcess(esp, new TestService(), false)) {
            var testGrpcService = grpc.get();
            var handlers = new WebHandlers().register("/api", new RestAPI(testGrpcService));
            try (var server = new NettyHttpServer(0, handlers)) {
                server.start();
                var port = server.getInetAddress().getPort();
                var prefix = "http://localhost:" + port;

                // Get
                var uri1 = create(prefix + "/api/http://example.enola.dev/Dalí");
                var response1 = rp.getResource(uri1);
                assertThat(response1.charSource().read())
                        .startsWith(
                                "{\"@type\":\"type.googleapis.com/dev.enola.thing.Thing\","
                                        + "\"iri\":\"http://example.org/test\"");
                assertThat(response1.mediaType()).isEqualTo(MediaType.JSON_UTF_8);
            }
        }
    }

    // TODO Replace with e.g. picasso.ttl
    static class TestService implements EnolaService {

        Thing thing = Thing.newBuilder().setIri("http://example.org/test").build();

        @Override
        public GetThingsResponse getThings(GetThingsRequest r) throws EnolaException {
            return GetThingsResponse.newBuilder().addThings(thing).build();
        }

        @Override
        public GetThingResponse getThing(GetThingRequest r) throws EnolaException {
            return GetThingResponse.newBuilder().setThing(Any.pack(thing)).build();
        }
    }
}

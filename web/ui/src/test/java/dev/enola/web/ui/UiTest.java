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
package dev.enola.web.ui;

import static com.google.common.truth.Truth.assertThat;

import static java.net.URI.create;

import com.google.common.net.MediaType;

import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.common.protobuf.Timestamps2;
import dev.enola.core.EnolaException;
import dev.enola.core.EnolaService;
import dev.enola.core.proto.Entity;
import dev.enola.core.proto.GetEntityRequest;
import dev.enola.core.proto.GetEntityResponse;
import dev.enola.core.proto.ID;
import dev.enola.web.sun.SunServer;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Instant;

public class UiTest {
    @Test
    public void testUi() throws IOException {
        var addr = new InetSocketAddress(0);
        try (var server = new SunServer(addr)) {
            new UI(new TestService(), null).register(server);
            server.start();
            var rp = new ResourceProviders();
            var port = server.getInetAddress().getPort();
            var prefix = "http://localhost:" + port;

            var uri1 = create(prefix + "/ui/bad-page-404");
            var response1 = rp.getResource(uri1);
            assertThat(response1.mediaType()).isEqualTo(MediaType.HTML_UTF_8);
            assertThat(response1.charSource().read()).contains("Enola");
            assertThat(response1.charSource().read()).contains("404");

            var uri2 = create(prefix + "/ui/entity/test.demo/123");
            var response2 = rp.getResource(uri2);
            assertThat(response2.charSource().read()).contains("Enola");
            assertThat(response2.charSource().read()).contains("test.demo/123");
        }
    }

    private static class TestService implements EnolaService {
        @Override
        public GetEntityResponse getEntity(GetEntityRequest r) throws EnolaException {
            var id = ID.newBuilder().setNs("test").setEntity("demo").addPaths("123");
            var now = Timestamps2.fromInstant(Instant.now());
            var entity = Entity.newBuilder().setId(id).setTs(now).build();
            return GetEntityResponse.newBuilder().setEntity(entity).build();
        }
    }
}

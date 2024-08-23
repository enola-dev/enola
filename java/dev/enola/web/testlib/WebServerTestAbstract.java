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
package dev.enola.web.testlib;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.util.concurrent.Futures.immediateFailedFuture;
import static com.google.common.util.concurrent.Futures.immediateFuture;

import com.google.common.net.MediaType;

import dev.enola.common.context.Context;
import dev.enola.common.context.TLC;
import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.common.io.resource.StringResource;
import dev.enola.web.StaticWebHandler;
import dev.enola.web.WebServer;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public abstract class WebServerTestAbstract {

    protected abstract WebServer create() throws IOException;

    private void configure(WebServer server) {
        var hello = StringResource.of("hello, world", MediaType.PLAIN_TEXT_UTF_8);
        server.register("/hello", uri -> immediateFuture(hello));

        server.register("/abc/xyz/", new StaticWebHandler("/abc/xyz/", "static"));

        server.register(
                "/error1", uri -> immediateFailedFuture(new IllegalArgumentException("oink")));
        server.register(
                "/error2",
                uri -> {
                    throw new IllegalArgumentException("oink");
                });

        server.register(
                "/context",
                uri -> immediateFuture(StringResource.of(TLC.get(TestCtxKey.MAGIC).toString())));
    }

    @Test
    public void testServer() throws IOException {
        try (var ctx = TLC.open()) {
            ctx.push(TestCtxKey.MAGIC, 123);
            try (var server = create()) {
                configure(server);
                server.start();

                var prefix = "http://localhost:" + server.getInetAddress().getPort();
                var rp = new ResourceProviders();

                // IPv6 NOK :( var uri = URI.create("http://" + server.getInetAddress() + "/hello");
                var uri1 = URI.create(prefix + "/hello");
                var response1 = rp.getResource(uri1);
                assertThat(response1.mediaType()).isEqualTo(MediaType.PLAIN_TEXT_UTF_8);
                assertThat(response1.charSource().read()).isEqualTo("hello, world");

                var contextURI = URI.create(prefix + "/context");
                // TODO !?! var responseFromTLC = rp.getResource(contextURI);
                // TODO !?! assertThat(responseFromTLC.charSource().read()).isEqualTo("123");

                var uri2 = URI.create(prefix + "/abc/xyz/hello.txt");
                var response2 = rp.getResource(uri2);
                assertThat(response2.mediaType()).isEqualTo(MediaType.PLAIN_TEXT_UTF_8);
                assertThat(response2.charSource().read()).isEqualTo("hi, you\n");

                var error1 = URI.create(prefix + "/error1");
                Assert.assertThrows(IllegalArgumentException.class, () -> rp.getResource(error1));
                // var errorResponse1 = rp.getResource(error1);
                // Assert.assertThrows(IOException.class, () -> errorResponse1.charSource().read());

                var error2 = URI.create(prefix + "/error2");
                Assert.assertThrows(IllegalArgumentException.class, () -> rp.getResource(error2));
                // var errorResponse2 = rp.getResource(error2);
                // Assert.assertThrows(IOException.class, () -> errorResponse2.charSource().read());

                // TODO expect HTTP Error 500
            }
        }
    }

    private enum TestCtxKey implements Context.Key<Integer> {
        MAGIC
    }
}

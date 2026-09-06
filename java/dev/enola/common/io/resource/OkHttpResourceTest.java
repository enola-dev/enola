/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.resource;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.util.concurrent.Futures.immediateFailedFuture;
import static com.google.common.util.concurrent.Futures.immediateFuture;

import static dev.enola.common.context.testlib.SingletonRule.$;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.net.MediaType;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.web.WebHandlers;
import dev.enola.web.netty.NettyHttpServer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;
import java.io.UncheckedIOException;

class OkHttpResourceTest {

    @RegisterExtension SingletonRule r = $(MediaTypeProviders.set(new MediaTypeProviders()));

    private static NettyHttpServer server;
    private static String prefix;

    @BeforeAll
    static void beforeAll() throws Exception {
        var handlers = new WebHandlers();
        var html = StringResource.of("<!DOCTYPE html><html><body>hello", MediaType.HTML_UTF_8);
        handlers.register("/test.html", uri -> immediateFuture(html));
        handlers.register(
                "/bad",
                uri -> immediateFailedFuture(new IllegalArgumentException("intentional error")));
        server = new NettyHttpServer(0, handlers);
        server.start();
        prefix = "http://localhost:" + server.getInetAddress().getPort();
    }

    @AfterAll
    static void afterAll() {
        if (server != null) {
            server.close();
        }
    }

    // TODO Add test coverage to ensure that simply constructing an OkHttpResource object
    //   does not cause any network activity, as the mediaType must be obtained lazily, on demand!

    @Test
    void http() throws IOException {
        var r = new OkHttpResource(prefix + "/test.html");
        assertThat(r.charSource().read()).ignoringCase().contains("<!doctype html>");
        assertThat(r.mediaType()).isEqualTo(MediaType.HTML_UTF_8);
    }

    @Test
    void httpError() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new OkHttpResource(prefix + "/bad").charSource().read());
    }

    @Test
    void connectTimeout() {
        // NB: 203.0.113.1 is a non-routable IPv4 address; the cause includes Timeout
        assertThrows(
                UncheckedIOException.class,
                () -> new OkHttpResource("http://203.0.113.1").charSource().read());
    }
}

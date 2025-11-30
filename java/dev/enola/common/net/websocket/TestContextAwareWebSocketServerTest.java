/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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
package dev.enola.common.net.websocket;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.context.TestContext;
import dev.enola.common.context.testlib.TestContextRule;

import org.java_websocket.WebSocket;
import org.junit.Rule;
import org.junit.Test;

import java.net.InetSocketAddress;

public class TestContextAwareWebSocketServerTest {

    private static class TestWebSocketServer extends EchoWebSocketServer {

        public TestWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            if (!TestContext.isUnderTest()) throw new IllegalStateException("Not under test");
            super.onMessage(conn, message);
        }
    }

    @Rule public TestContextRule rule = new TestContextRule();

    @Test
    public void test() throws Exception {
        var sock = new InetSocketAddress(0);
        try (var server = new TestWebSocketServer(sock)) {
            try (var ws = new WebSocketClient(server.awaitPort())) {
                assertThat(ws.send("hello, world", true)).isEqualTo("hello, world");
            }
        }
    }
}

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
package dev.enola.audio.voice.twilio.relay.websocket;

import static com.google.common.truth.Truth.assertThat;

import org.java_websocket.WebSocket;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.URI;

// TODO Move to a (TBD) dev.enola.common.net.websocket package
public class WebSocketClientTest {

    private static class EchoWebSocketServer extends LoggingWebSocketServer {
        public EchoWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            conn.send(message);
        }
    }

    @Test
    public void multiple() throws Exception {
        var sock = new InetSocketAddress(0);
        try (var server = new EchoWebSocketServer(sock)) {
            int port = server.awaitPort();
            try (var ws = new WebSocketClient(URI.create("ws://localhost:" + port))) {
                var response1 = ws.send("hello, world #1", true);
                assertThat(response1).contains("hello, world #1");

                var response2 = ws.send("hello, world #2", true);
                assertThat(response2).contains("hello, world #2");
            }
        }
    }
}

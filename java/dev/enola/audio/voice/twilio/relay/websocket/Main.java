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

import dev.enola.common.logging.JavaUtilLogging;

import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.logging.Level;

public class Main {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 8888;

        JavaUtilLogging.configure(Level.ALL);
        WebSocketServer server = new ConversationRelayServer(new InetSocketAddress(host, port));
        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(
                                () -> {
                                    try {
                                        server.stop(1000); // 1 second timeout
                                    } catch (InterruptedException e) {
                                        System.err.println(
                                                "Failed to stop the WebSocketServer gracefully.");
                                        Thread.currentThread().interrupt();
                                    }
                                }));
        server.run();
    }
}

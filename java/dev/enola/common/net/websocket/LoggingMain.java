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

import dev.enola.common.ShutdownCloser;
import dev.enola.common.logging.JavaUtilLogging;

import org.java_websocket.WebSocket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.logging.Level;

public class LoggingMain {

    public static void main(String[] args) {
        JavaUtilLogging.configure(Level.INFO);
        var sock = new InetSocketAddress(8888);
        ShutdownCloser.add(
                new LoggingWebSocketServer(sock) {
                    @Override
                    public void onMessage(WebSocket conn, String message) {
                        super.onMessage(conn, message);
                        System.out.println(message);
                    }

                    @Override
                    public void onMessage(WebSocket conn, ByteBuffer message) {
                        super.onMessage(conn, message);
                        System.err.println(message.array().length + " bytes...");
                    }
                });
    }
}

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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

import dev.enola.common.concurrent.Threads;

import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.time.Duration;

public abstract class CloseableWebSocketServer extends WebSocketServer implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(CloseableWebSocketServer.class);

    protected CloseableWebSocketServer(InetSocketAddress address) {
        super(address);
        start();
    }

    /** Wait for the server to start and bind the socket. */
    public int awaitPort() {
        int port = 0;
        for (int i = 0; i < 100; i++) {
            port = getPort();
            if (port > 0) {
                break;
            }
            Threads.sleep(Duration.ofMillis(10));
        }
        if (port < 1) throw new IllegalStateException("Failed to bind WebSocketServer");
        return port;
    }

    @Override
    public final void close() {
        try {
            stop(3000, "close()"); // 3 second timeout
        } catch (InterruptedException e) {
            logger.error("Failed to stop the WebSocketServer gracefully.", e);
            Thread.currentThread().interrupt();
        }
    }
}

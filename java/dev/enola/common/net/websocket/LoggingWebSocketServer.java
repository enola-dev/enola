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

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public abstract class LoggingWebSocketServer extends CloseableWebSocketServer {

    private static final Logger logger = LoggerFactory.getLogger(LoggingWebSocketServer.class);

    protected LoggingWebSocketServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onStart() {
        logger.info("WebSocket server started on {}", getAddress());
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.debug(
                "WebSocket connection opened: remote={}, local={}",
                conn.getRemoteSocketAddress(),
                conn.getLocalSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        logger.trace(
                "WebSocket message received: remote={}, local={}, message={}",
                conn.getRemoteSocketAddress(),
                conn.getLocalSocketAddress(),
                message);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        logger.debug(
                "WebSocket connection closed: remote={}, local={}, code={}, reason={}, remote={}",
                conn.getRemoteSocketAddress(),
                conn.getLocalSocketAddress(),
                code,
                reason,
                remote);
    }

    @Override
    public void onError(@Nullable WebSocket conn, Exception ex) {
        if (conn != null) {
            logger.error(
                    "WebSocket error on connection: remote={}, local={}",
                    conn.getRemoteSocketAddress(),
                    conn.getLocalSocketAddress(),
                    ex);
        } else {
            logger.error("WebSocket server error", ex);
        }
    }
}

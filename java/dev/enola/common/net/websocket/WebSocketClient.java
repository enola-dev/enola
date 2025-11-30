/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * A simple WebSocket client which only depends on the JDK and no external libraries, suitable e.g.
 * for testing. Consider using a more feature-complete library for production code; e.g. {@link
 * org.java_websocket.client.WebSocketClient} or similar.
 */
public class WebSocketClient implements AutoCloseable {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Queue<CompletableFuture<String>> pending = new ConcurrentLinkedQueue<>();
    private final WebSocket ws;

    public WebSocketClient(int localhostPort) {
        this(URI.create("ws://localhost:" + localhostPort));
    }

    public WebSocketClient(URI uri) {
        ws =
                httpClient
                        .newWebSocketBuilder()
                        .buildAsync(
                                uri,
                                new WebSocket.Listener() {
                                    @Override
                                    public CompletionStage<?> onText(
                                            WebSocket webSocket, CharSequence data, boolean last) {
                                        var future = pending.poll();
                                        if (future != null) {
                                            future.complete(data.toString());
                                        }
                                        return WebSocket.Listener.super.onText(
                                                webSocket, data, last);
                                    }
                                })
                        .join();
    }

    /** Sends a message and waits for the response. */
    public synchronized String send(String message, boolean last) throws Exception {
        var future = new CompletableFuture<String>();
        pending.add(future);
        // skipcq: JAVA-W1087
        ws.sendText(message, last)
                .exceptionally(
                        ex -> {
                            future.completeExceptionally(ex);
                            return null;
                        });
        return future.get(5, TimeUnit.SECONDS);
    }

    @Override
    public void close() {
        ws.sendClose(WebSocket.NORMAL_CLOSURE, "").join();
        httpClient.close();
    }
}

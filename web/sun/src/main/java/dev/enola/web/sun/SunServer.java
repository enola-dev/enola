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
package dev.enola.web.sun;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.net.MediaType.PLAIN_TEXT_UTF_8;

import static java.util.Objects.requireNonNull;

import com.google.common.io.ByteSource;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import dev.enola.common.io.resource.ReadableResource;
import dev.enola.web.WebHandler;
import dev.enola.web.WebServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/** {@link WebServer} API implementation using {@link HttpServer} from com.sun. */
public class SunServer implements WebServer {

    private static final Logger LOG = LoggerFactory.getLogger(SunServer.class);

    private static final Executor SERVER_EXECUTOR = Executors.newCachedThreadPool();
    private static final Executor HANDLER_EXECUTOR = Executors.newSingleThreadExecutor();

    private final HttpServer sun;

    public SunServer(InetSocketAddress address) throws IOException {
        this.sun = HttpServer.create(requireNonNull(address, "InetSocketAddress"), 0);
        this.sun.setExecutor(SERVER_EXECUTOR);
    }

    @Override
    public void register(String path, WebHandler h) {
        sun.createContext(path, new Wrapper(h));
    }

    @Override
    public void start() {
        sun.start();
    }

    @Override
    public InetSocketAddress getInetAddress() {
        return sun.getAddress();
    }

    @Override
    public void close() {
        sun.stop(3);
    }

    private static class Wrapper implements HttpHandler {
        private final WebHandler handler;

        public Wrapper(WebHandler h) {
            this.handler = h;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            var uri = exchange.getRequestURI();
            if (exchange.getRequestMethod().equals("GET")) {
                var callback = new HandlerCallback(exchange);
                try {
                    ListenableFuture<ReadableResource> f = handler.get(uri);
                    Futures.addCallback(f, callback, HANDLER_EXECUTOR);
                } catch (Throwable t) {
                    // Without this, exceptions thrown by the WebHandler are "lost"
                    callback.onFailure(t);
                }
            } else {
                throw new IOException(
                        "Not implemented HTTP Method: " + exchange.getRequestMethod());
            }
        }
    }

    private static class HandlerCallback implements FutureCallback<ReadableResource> {
        private final HttpExchange exchange;

        public HandlerCallback(HttpExchange exchange) {
            this.exchange = exchange;
        }

        @Override
        public void onSuccess(ReadableResource r) {
            try {
                ByteSource byteSource = r.byteSource();
                // "content-type" instead of "Content-type" or "Content-Type" for HTTP/2.0
                exchange.getResponseHeaders().set("content-type", r.mediaType().toString());
                exchange.sendResponseHeaders(200, byteSource.sizeIfKnown().or(0L));
                byteSource.copyTo(exchange.getResponseBody());
                exchange.close();
            } catch (Throwable t) {
                onFailure(t);
            }
        }

        @Override
        public void onFailure(Throwable throwable) {
            LOG.warn("URI {} handling failed", exchange.getRequestURI(), throwable);
            try {
                exchange.getResponseHeaders().set("content-type", PLAIN_TEXT_UTF_8.toString());
                exchange.sendResponseHeaders(500, 0);
                var pw = new PrintWriter(exchange.getResponseBody(), true, UTF_8);
                throwable.printStackTrace(pw);
                exchange.close();
            } catch (Throwable io) {
                LOG.error("Error Page response failed", io);
            }
        }
    }
}

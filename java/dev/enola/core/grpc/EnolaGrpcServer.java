/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.core.grpc;

import com.google.common.util.concurrent.ListeningExecutorService;

import dev.enola.common.concurrent.Executors;
import dev.enola.core.EnolaService;
import dev.enola.core.EnolaServiceProvider;

import io.grpc.ServerBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class EnolaGrpcServer implements AutoCloseable {

    // See also dev.enola.demo.Server

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final EnolaService service;
    private final EnolaServiceProvider esp;
    private io.grpc.Server server;
    private ListeningExecutorService executor;

    public EnolaGrpcServer(EnolaServiceProvider esp, EnolaService service) {
        this.esp = esp;
        this.service = service;
    }

    public EnolaGrpcServer start(int port) throws IOException {
        executor = Executors.newListeningCachedThreadPool("gRPC-Server", LOGGER);
        var builder = ServerBuilder.forPort(port);
        builder.executor(executor);
        builder.addService(new EnolaGrpcService(esp, service)); // as in EnolaGrpcInProcess
        server = builder.build().start();
        return this;
    }

    public int getPort() {
        return server.getPort();
    }

    @Override
    public void close() throws InterruptedException {
        server.shutdown().awaitTermination(7, TimeUnit.SECONDS);
        Executors.shutdownAndAwaitTermination(executor);
    }
}

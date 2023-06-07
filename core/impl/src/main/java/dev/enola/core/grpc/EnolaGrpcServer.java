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
package dev.enola.core.grpc;

import dev.enola.core.EnolaService;

import io.grpc.ServerBuilder;

import java.io.IOException;

public class EnolaGrpcServer implements AutoCloseable {

    // See also dev.enola.demo.Server

    private final EnolaService service;
    private io.grpc.Server server;

    public EnolaGrpcServer(EnolaService service) {
        this.service = service;
    }

    public EnolaGrpcServer start(int port) throws IOException {
        var builder = ServerBuilder.forPort(port);
        builder.addService(new EnolaGrpcService(service));
        server = builder.build().start();
        return this;
    }

    public int getPort() {
        return server.getPort();
    }

    @Override
    public void close() throws Exception {
        server.shutdown().awaitTermination();
    }
}

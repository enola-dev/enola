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
package dev.enola.cli;

import dev.enola.core.grpc.EnolaGrpcServer;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.proto.EnolaServiceGrpc;
import dev.enola.web.WebServer;
import dev.enola.web.rest.RestAPI;
import dev.enola.web.sun.SunServer;
import dev.enola.web.ui.UI;

import picocli.CommandLine;

import java.net.InetSocketAddress;

@CommandLine.Command(name = "server", description = "Start HTTP Server")
public class ServerCommand extends CommandWithModel implements AutoCloseable {

    @CommandLine.ParentCommand Enola parentCommand;

    @CommandLine.Option(
            names = {"--httpPort"},
            required = true,
            description = "HTTP Port")
    int httpPort;

    @CommandLine.Option(
            names = {"--grpcPort"},
            required = false,
            description = "gRPC API Port")
    Integer grpcPort;

    private EnolaGrpcServer grpcServer;
    private WebServer httpServer;

    @Override
    protected void run(EntityKindRepository ekr, EnolaServiceGrpc.EnolaServiceBlockingStub service)
            throws Exception {

        parentCommand.setCloseableSubCommand(this);

        // gRPC API
        if (grpcPort != null) {
            grpcServer = new EnolaGrpcServer(esp.get(ekr));
            grpcServer.start(grpcPort);
            out.println("gRPC API server now available on port " + grpcServer.getPort());
        }

        // HTML UI + JSON REST API
        httpServer = new SunServer(new InetSocketAddress(httpPort));
        new UI(service).register(httpServer);
        new RestAPI(service).register(httpServer);
        httpServer.start();
        out.println(
                "HTTP JSON REST API + HTML UI server started; open http:/"
                        + httpServer.getInetAddress()
                        + "/ui ...");

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            // Expected, from close()
        }
    }

    @Override
    public void close() throws InterruptedException {
        if (grpcServer != null) {
            grpcServer.close();
        }
        if (httpServer != null) {
            httpServer.close();
        }
        Thread.currentThread().interrupt();
    }
}

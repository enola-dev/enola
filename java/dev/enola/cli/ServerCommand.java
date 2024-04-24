/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2024 The Enola <https://enola.dev> Authors
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
import dev.enola.web.*;

import picocli.CommandLine;

import java.net.InetSocketAddress;

@CommandLine.Command(name = "server", description = "Start HTTP and/or gRPC Server/s")
public class ServerCommand extends CommandWithModel {

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1")
    HttpAndOrGrpcPorts ports;

    @CommandLine.Option(
            names = {"--immediateExitOnlyForTest"},
            defaultValue = "false",
            hidden = true)
    boolean immediateExitOnlyForTest;

    private EnolaGrpcServer grpcServer;
    private WebServer httpServer;

    @Override
    protected void run(EntityKindRepository ekr, EnolaServiceGrpc.EnolaServiceBlockingStub service)
            throws Exception {
        var out = spec.commandLine().getOut();

        // gRPC API
        if (ports.grpcPort != null) {
            grpcServer = new EnolaGrpcServer(esp, esp.getEnolaService());
            grpcServer.start(ports.grpcPort);
            out.println("gRPC API server now available on port " + grpcServer.getPort());
        }

        // HTML UI + JSON REST API
        if (ports.httpPort != null) {
            httpServer = new SunServer(new InetSocketAddress(ports.httpPort));
            new UI(service, getMetadataProvider(new EnolaThingProvider(service)))
                    .register(httpServer);
            new RestAPI(service).register(httpServer);
            httpServer.start();
            out.println(
                    "HTTP JSON REST API + HTML UI server started; open http:/"
                            + httpServer.getInetAddress()
                            + "/ui ...");
        }

        if (!immediateExitOnlyForTest) {
            Thread.currentThread().join();
        } else {
            close();
        }
    }

    public void close() throws InterruptedException {
        if (grpcServer != null) {
            grpcServer.close();
        }
        if (httpServer != null) {
            httpServer.close();
        }
        // TODO Thread.currentThread().interrupt();
    }

    static class HttpAndOrGrpcPorts {
        @CommandLine.Option(
                names = {"--httpPort"},
                description = "HTTP Port")
        Integer httpPort;

        @CommandLine.Option(
                names = {"--grpcPort"},
                description = "gRPC API Port")
        Integer grpcPort;
    }
}

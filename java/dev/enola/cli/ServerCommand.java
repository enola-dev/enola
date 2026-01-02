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
package dev.enola.cli;

import dev.enola.ai.adk.web.AdkHttpServer;
import dev.enola.chat.sshd.EnolaSshServer;
import dev.enola.common.FreedesktopDirectories;
import dev.enola.common.context.TLC;
import dev.enola.core.grpc.EnolaGrpcServer;
import dev.enola.core.proto.EnolaServiceGrpc;
import dev.enola.web.*;
import dev.enola.web.netty.NettyHttpServer;

import org.jspecify.annotations.Nullable;

import picocli.CommandLine;

@CommandLine.Command(name = "server", description = "Start HTTP, SSH and/or gRPC Server/s")
public class ServerCommand extends CommandWithModel {

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1")
    HttpAndOrGrpcPorts ports;

    @CommandLine.ArgGroup(exclusive = false)
    @Nullable AiOptions aiOptions;

    @CommandLine.Option(
            names = {"--immediateExitOnlyForTest"},
            defaultValue = "false",
            hidden = true)
    boolean immediateExitOnlyForTest;

    private @Nullable EnolaGrpcServer grpcServer;
    private @Nullable WebServer httpServer;
    private @Nullable AutoCloseable chatServer;
    private @Nullable EnolaSshServer sshServer;

    @Override
    protected void run(EnolaServiceGrpc.EnolaServiceBlockingStub service) throws Exception {
        try (var ctx = TLC.open()) {
            setup(ctx);
            runInContext(service);
        }
    }

    private void runInContext(EnolaServiceGrpc.EnolaServiceBlockingStub service) throws Exception {
        var out = spec.commandLine().getOut();

        // gRPC API
        if (ports.grpcPort != null) {
            grpcServer = new EnolaGrpcServer(esp, esp.getEnolaService());
            grpcServer.start(ports.grpcPort);
            out.println("gRPC API server now available on port " + grpcServer.getPort());
        }

        // HTML UI + JSON REST API
        if (ports.httpPort != null) {
            var handlers = new WebHandlers();
            new UI(service, getMetadataProvider(new EnolaThingProvider(service)))
                    .register(handlers);
            handlers.register("/api", new RestAPI(service));
            httpServer = new NettyHttpServer(ports.httpPort, handlers);
            httpServer.start();
            out.println(
                    "HTTP JSON REST API + HTML UI server started; open http://"
                            + httpServer.getInetAddress()
                            + "/ui ...");
        }

        // Chat (ADK) UI
        if (ports.chatPort != null) {
            var agents = AI.load(rp, aiOptions);
            chatServer = AdkHttpServer.start(agents, ports.chatPort);
            out.println(
                    "HTTP Chat UI server started; open http://localhost:"
                            + ports.chatPort
                            + " ...");
        }

        // SSH Server
        if (ports.sshPort != null) {
            var hostKeyPath = FreedesktopDirectories.HOSTKEY_PATH;
            sshServer = new EnolaSshServer(ports.sshPort, hostKeyPath);
            out.println("SSH server (" + hostKeyPath + ") running on port " + sshServer.port());
        }

        if (!immediateExitOnlyForTest) {
            Thread.currentThread().join();
        } else {
            close();
        }
    }

    public void close() throws Exception {
        if (grpcServer != null) {
            grpcServer.close();
        }
        if (httpServer != null) {
            httpServer.close();
        }
        if (chatServer != null) {
            chatServer.close();
        }
        if (sshServer != null) {
            sshServer.close();
        }
        // TODO Thread.currentThread().interrupt();
    }

    static class HttpAndOrGrpcPorts {
        @CommandLine.Option(
                names = {"--httpPort"},
                description = "HTTP Port of Enola UI")
        @Nullable Integer httpPort;

        @CommandLine.Option(
                names = {"--chatPort"},
                description = "HTTP Port of Chat UI")
        @Nullable Integer chatPort;

        @CommandLine.Option(
                names = {"--sshPort"},
                description = "SSH (Chat) Port")
        @Nullable Integer sshPort;

        @CommandLine.Option(
                names = {"--grpcPort"},
                description = "gRPC API Port")
        @Nullable Integer grpcPort;
    }
}

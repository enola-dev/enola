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
package dev.enola.demo;

import io.grpc.ServerBuilder;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class Server implements Closeable {

    // See also dev.enola.core.grpc.EnolaGrpcServer

    private io.grpc.Server server;

    public static void main(String[] args) throws IOException, InterruptedException {
        var port = args.length > 0 ? Integer.parseInt(args[0]) : 9090;
        try (var server = new Server()) {
            server.start(port);
            System.out.println("Started Demo gRPC Server on localhost at port: " + port);

            var pid = ProcessHandle.current().pid();
            Files.writeString(Path.of("/tmp/ServerPID"), Long.toString(pid));
            System.out.println(pid + " PID written to /tmp/ServerPID");
            server.blockUntilShutdown();
        }
    }

    public Server start(int port) throws IOException {
        var builder = ServerBuilder.forPort(port);
        builder.addService(new DemoConnector());
        server = builder.build().start();
        return this;
    }

    public int getPort() {
        return server.getPort();
    }

    /**
     * Stops the servers and awaits its termination for a few seconds. This would typically be used
     * in test teardowns, or e.g. from a container preStop hook.
     */
    @Override
    public void close() throws IOException {
        if (server != null) {
            try {
                if (!server.shutdown().awaitTermination(3, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Server failed to shutdown in 3s");
                }
            } catch (InterruptedException e) {
                // Ignore.
            }
        }
    }

    /**
     * Await termination on the main thread, since the grpc library uses daemon threads. This would
     * typically be invoked at the end of an application's main() method.
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}

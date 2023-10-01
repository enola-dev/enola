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

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;

import dev.enola.common.concurrent.Executors;
import dev.enola.core.EnolaService;
import dev.enola.core.proto.EnolaServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class EnolaGrpcInProcess
        implements AutoCloseable { // javax.inject.Provider<EnolaServiceGrpc.EnolaServiceBlockingStub>

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final EnolaService service;
    private final Server server;
    private final ManagedChannel channel;
    private final EnolaServiceGrpc.EnolaServiceBlockingStub client;
    private final ListeningExecutorService executor;
    private final ListeningScheduledExecutorService scheduledExecutor;

    public EnolaGrpcInProcess(EnolaService service) throws IOException {
        this.service = service;

        executor = Executors.newListeningCachedThreadPool("gRPC-InProcess-Non_Scheduled", LOGGER);
        scheduledExecutor =
                Executors.newListeningScheduledThreadPool(16, "gRPC-InProcess-Scheduled", LOGGER);

        var uniqueName = InProcessServerBuilder.generateName();
        var builder = InProcessServerBuilder.forName(uniqueName);
        builder.executor(executor);
        builder.scheduledExecutorService(scheduledExecutor);
        builder.addService(new EnolaGrpcService(service)); // as in EnolaGrpcServer
        server = builder.build().start();

        InProcessChannelBuilder channelBuilder = InProcessChannelBuilder.forName(uniqueName);
        channelBuilder.directExecutor(); // as above
        channel = channelBuilder.build();

        // .withDeadlineAfter(13, SECONDS) doesn't seem to work will with InProcessChannelBuilder?!
        client = EnolaServiceGrpc.newBlockingStub(channel);
    }

    public EnolaServiceGrpc.EnolaServiceBlockingStub get() {
        return client;
    }

    @Override
    public void close() throws Exception {
        // As in GrpcServerRule#after() and GrpcCleanupRule...
        channel.shutdown();
        server.shutdown();

        try {
            channel.awaitTermination(7, TimeUnit.SECONDS);
            server.awaitTermination(7, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            channel.shutdownNow();
            server.shutdownNow();
        }

        Executors.shutdownAndAwaitTermination(executor);
        Executors.shutdownAndAwaitTermination(scheduledExecutor);
    }
}

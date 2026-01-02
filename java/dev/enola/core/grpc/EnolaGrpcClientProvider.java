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

import static java.util.concurrent.TimeUnit.SECONDS;

import com.google.common.util.concurrent.ListeningExecutorService;

import dev.enola.common.concurrent.Executors;
import dev.enola.core.proto.EnolaServiceGrpc;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnolaGrpcClientProvider implements ServiceProvider {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final EnolaServiceGrpc.EnolaServiceBlockingStub client;
    private final ManagedChannel channel;
    private final ListeningExecutorService executor;
    private final ListeningExecutorService offloadExecutor;

    public EnolaGrpcClientProvider(String endpoint, boolean multithreaded) {
        if (multithreaded) {
            executor = Executors.newListeningCachedThreadPool("gRPC-Client", LOGGER);
            offloadExecutor = Executors.newListeningCachedThreadPool("gRPC-Client-Offload", LOGGER);
        } else {
            executor = Executors.newListeningDirectExecutor();
            offloadExecutor = Executors.newListeningDirectExecutor();
        }

        var credz = InsecureChannelCredentials.create();
        channel =
                Grpc.newChannelBuilder(endpoint, credz)
                        .executor(executor)
                        .offloadExecutor(offloadExecutor)
                        .build();
        client = EnolaServiceGrpc.newBlockingStub(channel).withDeadlineAfter(3, SECONDS);
    }

    public EnolaServiceGrpc.EnolaServiceBlockingStub get() {
        return client;
    }

    public void close() throws Exception {
        channel.shutdownNow().awaitTermination(3, SECONDS);
        Executors.shutdownAndAwaitTermination(executor);
        Executors.shutdownAndAwaitTermination(offloadExecutor);
    }
}

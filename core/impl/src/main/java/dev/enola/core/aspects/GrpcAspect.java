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
package dev.enola.core.aspects;

import dev.enola.core.EnolaException;
import dev.enola.core.EntityAspect;
import dev.enola.core.connector.proto.AugmentRequest;
import dev.enola.core.connector.proto.ConnectorServiceGrpc;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.Entity;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GrpcAspect implements Closeable, EntityAspect {

    private final ManagedChannel channel;
    private final ConnectorServiceGrpc.ConnectorServiceBlockingStub client;

    public GrpcAspect(String endpoint) {
        var credz = InsecureChannelCredentials.create();
        channel = Grpc.newChannelBuilder(endpoint, credz).build();
        client = ConnectorServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public void close() throws IOException {
        if (channel != null) {
            try {
                channel.shutdownNow().awaitTermination(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // Ignore.
            }
        }
    }

    @Override
    public void augment(Entity.Builder entity, EntityKind entityKind) throws EnolaException {
        var request = AugmentRequest.newBuilder().setEntity(entity).build();
        var response = client.augment(request);
        var builder = response.getEntity().toBuilder();
        builder.clearId(); // required to avoid duplicating path (and also safer)
        entity.mergeFrom(builder.build());
    }
}

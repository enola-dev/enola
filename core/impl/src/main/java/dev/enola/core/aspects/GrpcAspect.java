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

import static java.util.concurrent.TimeUnit.SECONDS;

import com.google.protobuf.Descriptors;

import dev.enola.core.EnolaException;
import dev.enola.core.EntityAspect;
import dev.enola.core.IDs;
import dev.enola.core.connector.proto.AugmentRequest;
import dev.enola.core.connector.proto.ConnectorServiceGrpc;
import dev.enola.core.connector.proto.ConnectorServiceListRequest;
import dev.enola.core.connector.proto.GetDescriptorsRequest;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.Entity;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GrpcAspect implements Closeable, EntityAspect {

    private final ManagedChannel channel;
    private final ConnectorServiceGrpc.ConnectorServiceBlockingStub client;

    public GrpcAspect(String endpoint) {
        var credz = InsecureChannelCredentials.create();
        channel = Grpc.newChannelBuilder(endpoint, credz).build();
        client = ConnectorServiceGrpc.newBlockingStub(channel).withDeadlineAfter(7, SECONDS);
    }

    @Override
    public void close() throws IOException {
        if (channel != null) {
            try {
                channel.shutdownNow().awaitTermination(3, SECONDS);
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

    @Override
    public void list(
            ConnectorServiceListRequest request,
            EntityKind entityKind,
            List<Entity.Builder> entities)
            throws EnolaException {

        var requestedKindID = IDs.withoutPath(request.getId());
        var response = client.list(request);
        for (var entity : response.getEntitiesList()) {
            var connectorResponseKindID = IDs.withoutPath(entity.getId());
            if (!requestedKindID.equals(connectorResponseKindID)) {
                throw new EnolaException(
                        "Connector returned wrong Entity Kind; requested: "
                                + requestedKindID
                                + ", got: "
                                + connectorResponseKindID);
            }
            entities.add(entity.toBuilder());
        }
    }

    @Override
    // This is only called once, at initialization
    // See the documentation in enola_connector.proto's GetDescriptorsResponse
    // why this does it like this (it's to build up the dependencies, in order).
    public List<Descriptors.Descriptor> getDescriptors() throws EnolaException {
        var response = client.getDescriptors(GetDescriptorsRequest.getDefaultInstance());
        var descriptors = new ArrayList<Descriptors.Descriptor>(response.getProtosCount());
        var dependencies = new ArrayList<Descriptors.FileDescriptor>(response.getProtosCount());
        for (var fileDescriptorProto : response.getProtosList()) {
            try {
                var fileDescriptor =
                        Descriptors.FileDescriptor.buildFrom(
                                fileDescriptorProto,
                                dependencies.toArray(new Descriptors.FileDescriptor[] {}));
                dependencies.add(fileDescriptor);
                for (var descriptor : fileDescriptor.getMessageTypes()) {
                    descriptors.add(descriptor);
                }
            } catch (Descriptors.DescriptorValidationException e) {
                throw new EnolaException("Proto Validation failed", e);
            }
        }
        return descriptors;
    }
}

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

import com.google.protobuf.Any;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;

import dev.enola.core.connector.proto.*;
import dev.enola.core.proto.Entity;
import dev.enola.core.proto.ID;
import dev.enola.demo.proto.Something;

import io.grpc.stub.StreamObserver;

import java.util.Date;

public class DemoConnector extends ConnectorServiceGrpc.ConnectorServiceImplBase {
    @Override
    public void augment(AugmentRequest request, StreamObserver<AugmentResponse> responseObserver) {
        // Note how for Augment (for Get) we do NOT need to set an ID!
        var entity = request.getEntity().toBuilder();
        entity.putLink("link1", "http://www.vorburger.ch");

        var invention = Timestamps.fromMillis(new Date(2023, 5, 9).getTime());
        var something =
                Something.newBuilder()
                        .setText("hello, world")
                        .setNumber(123)
                        .setTs(invention)
                        .build();
        entity.putData("data1", Any.pack(something, "demo.enola.dev/"));

        var response = AugmentResponse.newBuilder().setEntity(entity).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void list(
            ConnectorServiceListRequest request,
            StreamObserver<ConnectorServiceListResponse> responseObserver) {
        // Note how for List we DO need to set IDs!
        var response = ConnectorServiceListResponse.newBuilder();
        var id1 = ID.newBuilder().setNs("demo").setEntity("foo").addPaths("hello").build();
        var id2 = ID.newBuilder().setNs("demo").setEntity("foo").addPaths("world").build();
        response.addEntities(
                Entity.newBuilder().setId(id1).putLink("link1", "http://www.vorburger.ch"));
        response.addEntities(Entity.newBuilder().setId(id2).putLink("link1", "https://enola.dev"));
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getDescriptors(
            GetDescriptorsRequest request,
            StreamObserver<GetDescriptorsResponse> responseObserver) {
        var response = GetDescriptorsResponse.newBuilder();

        // As per demo_data.proto
        // See the documentation in enola_connector.proto's GetDescriptorsResponse
        response.addProtos(Timestamp.getDescriptor().getFile().toProto());
        response.addProtos(Something.getDescriptor().getFile().toProto());

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}

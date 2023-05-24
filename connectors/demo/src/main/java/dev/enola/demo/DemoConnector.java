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
package dev.enola.demo;

import dev.enola.core.connector.proto.AugmentRequest;
import dev.enola.core.connector.proto.AugmentResponse;
import dev.enola.core.connector.proto.ListRequest;
import dev.enola.core.connector.proto.ListResponse;
import dev.enola.core.proto.Entity;
import dev.enola.core.connector.proto.ConnectorServiceGrpc;

import io.grpc.stub.StreamObserver;

public class DemoConnector extends ConnectorServiceGrpc.ConnectorServiceImplBase {
    @Override
    public void augment(AugmentRequest request, StreamObserver<AugmentResponse> responseObserver) {
        var entity = request.getEntity().toBuilder();
        entity.putLink("link1", "http://www.vorburger.ch");
        var response = AugmentResponse.newBuilder().setEntity(entity).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void list(ListRequest request, StreamObserver<ListResponse> responseObserver) {
        var response = ListResponse.newBuilder();
        response.addEntity(Entity.newBuilder().putLink("link1", "http://www.vorburger.ch"));
        response.addEntity(Entity.newBuilder().putLink("link1", "http://www.vorburgerag.ch"));
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}

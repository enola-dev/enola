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

import dev.enola.core.EnolaException;
import dev.enola.core.EnolaService;
import dev.enola.core.proto.*;

import io.grpc.stub.StreamObserver;

public class EnolaGrpcService extends EnolaServiceGrpc.EnolaServiceImplBase {

    private final EnolaService enola;

    public EnolaGrpcService(EnolaService service) {
        this.enola = service;
    }

    @Override
    public void getEntity(
            GetEntityRequest request, StreamObserver<GetEntityResponse> responseObserver) {
        try {
            var response = enola.getEntity(request);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (EnolaException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void listEntities(
            ListEntitiesRequest request, StreamObserver<ListEntitiesResponse> responseObserver) {
        try {
            var response = enola.listEntities(request);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (EnolaException e) {
            responseObserver.onError(e);
        }
    }
}

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
import dev.enola.core.connector.proto.ConnectorServiceGrpc;

import dev.enola.core.connector.proto.ConnectorServiceGrpc;

import com.google.protobuf.Struct;
import com.google.protobuf.Any;

import dev.enola.core.IDs;
import dev.enola.core.proto.ID;
import dev.enola.core.util.proto.EnolaString;

import io.grpc.stub.StreamObserver;

public class DemoConnector extends ConnectorServiceGrpc.ConnectorServiceImplBase {
    @Override
    public void augment(AugmentRequest request, StreamObserver<AugmentResponse> responseObserver) {
        var entity = request.getEntity().toBuilder();

        // these returned values match docs/user/library/model.textproto (and not the yaml version)
        System.out.println("augment: " + IDs.toPath(entity.getId()));
        if (IDs.toPath(entity.getId()).equals("demo.work/8706-1000")) {

            Any any = Any.pack(EnolaString.newBuilder().setValue("En attendant Godot").build());
            entity.putData("title", any);

            any = Any.pack(EnolaString.newBuilder().setValue("French").build());
            entity.putData("language", any);

            entity.putLink("wikipedia", "https://en.wikipedia.org/wiki/Waiting_for_Godot");
            entity.putLink("search", "https://www.google.com/search?q=En%20attendant%20Godot");

            var id  = ID.newBuilder().setNs("demo").setEntity("author").addPaths("samuel_beckett").build();
            entity.putRelated("author", id);
        } else if (IDs.toPath(entity.getId()).equals("demo.work/0-13-140731-7")) {

            Any any = Any.pack(EnolaString.newBuilder().setValue("Core Java data objects").build());
            entity.putData("title", any);

            any = Any.pack(EnolaString.newBuilder().setValue("English").build());
            entity.putData("language", any);

            var id  = ID.newBuilder().setNs("demo").setEntity("author").addPaths("michael_vorburger").build();
            entity.putRelated("author", id);
        } else if (IDs.toPath(entity.getId()).equals("demo.author/samuel_beckett")) {
            Any any = Any.pack(EnolaString.newBuilder().setValue("French").build());
            entity.putData("language", any);

            var id  = ID.newBuilder().setNs("demo").setEntity("work").addPaths("samuel_beckett").build();
            entity.putRelated("work", id);
        } else if (IDs.toPath(entity.getId()).equals("demo.author/michael_vorburger")) {
            Any any = Any.pack(EnolaString.newBuilder().setValue("French").build());
            entity.putData("language", any);

            var id  = ID.newBuilder().setNs("demo").setEntity("work").addPaths("michael_vorburger").build();
            entity.putRelated("work", id);
        } else {
            entity.putLink("link1", "http://www.vorburger.ch");
        }

        var response = AugmentResponse.newBuilder().setEntity(entity).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

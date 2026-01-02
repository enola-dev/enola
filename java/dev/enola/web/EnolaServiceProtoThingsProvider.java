/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.web;

import com.google.protobuf.InvalidProtocolBufferException;

import dev.enola.core.proto.EnolaServiceGrpc;
import dev.enola.core.proto.GetThingRequest;
import dev.enola.thing.message.MoreThings;
import dev.enola.thing.message.ProtoThingsProvider;
import dev.enola.thing.proto.Thing;

import java.util.Collections;

public class EnolaServiceProtoThingsProvider implements ProtoThingsProvider {

    // TODO With the (new) GetThingsResponse, this shouldn't even be required anymore...

    private final EnolaServiceGrpc.EnolaServiceBlockingStub service;

    public EnolaServiceProtoThingsProvider(EnolaServiceGrpc.EnolaServiceBlockingStub service) {
        this.service = service;
    }

    @Override
    public Iterable<Thing> get(String iri) {
        var request = GetThingRequest.newBuilder().setIri(iri).build();
        var response = service.getThing(request);
        try {
            if (!response.hasThing()) return Collections.emptySet();
            var any = response.getThing();
            return MoreThings.fromAny(any);

        } catch (InvalidProtocolBufferException e) {
            throw new IllegalArgumentException(e);
        }
    }
}

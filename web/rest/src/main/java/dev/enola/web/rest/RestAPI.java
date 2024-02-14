/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2024 The Enola <https://enola.dev> Authors
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
package dev.enola.web.rest;

import com.google.common.net.MediaType;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Descriptors.DescriptorValidationException;

import dev.enola.common.io.resource.MemoryResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.core.EnolaException;
import dev.enola.core.meta.TypeRegistryWrapper;
import dev.enola.core.proto.EnolaServiceGrpc.EnolaServiceBlockingStub;
import dev.enola.core.proto.GetFileDescriptorSetRequest;
import dev.enola.core.proto.GetThingRequest;
import dev.enola.core.proto.ListEntitiesRequest;
import dev.enola.web.WebHandler;
import dev.enola.web.WebServer;

import java.io.IOException;
import java.net.URI;

// TODO Merge this with class UI in /ui/ module, to re-use code better
public class RestAPI implements WebHandler {

    private final EnolaServiceBlockingStub service;
    private final TypeRegistryWrapper typeRegistryWrapper;
    private ProtoIO protoIO;

    public RestAPI(EnolaServiceBlockingStub service) throws DescriptorValidationException {
        this.service = service;
        var gfdsr = GetFileDescriptorSetRequest.newBuilder().build();
        var fds = service.getFileDescriptorSet(gfdsr).getProtos();
        typeRegistryWrapper = TypeRegistryWrapper.from(fds);
    }

    public void register(WebServer server) {
        server.register("/api", this);
    }

    @Override
    public ListenableFuture<ReadableResource> get(URI uri) {
        try {
            var resource = new MemoryResource(MediaType.JSON_UTF_8);
            writeJSON(uri, resource);
            return Futures.immediateFuture(resource);
        } catch (EnolaException | IOException e) {
            return Futures.immediateFailedFuture(e);
        }
    }

    private void writeJSON(URI uri, WritableResource resource) throws EnolaException, IOException {
        var path = uri.getPath();
        if (path.startsWith("/api/entity/")) {
            var eri = path.substring("/api/entity/".length());
            getEntityJSON(eri, resource);
        } else if (path.startsWith("/api/entities/")) {
            var eri = path.substring("/api/entities/".length());
            listEntityJSON(eri, resource);
        } else {
            // TODO 404 instead 500 (needs API changes)
            throw new IllegalArgumentException("404 - Unknown URI!");
        }
    }

    private void getEntityJSON(String eri, WritableResource resource)
            throws EnolaException, IOException {
        var request = GetThingRequest.newBuilder().setEri(eri).build();
        var response = service.getThing(request);
        var thing = response.getThing();

        getProtoIO().write(thing, resource);
    }

    private void listEntityJSON(String eri, WritableResource resource)
            throws EnolaException, IOException {
        var request = ListEntitiesRequest.newBuilder().setEri(eri).build();
        var response = service.listEntities(request);
        for (var entity : response.getEntitiesList()) {
            getProtoIO().write(entity, resource);
        }
    }

    private ProtoIO getProtoIO() {
        if (protoIO == null) {
            protoIO = new ProtoIO(typeRegistryWrapper.get());
        }
        return protoIO;
    }
}

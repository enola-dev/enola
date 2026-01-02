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
package dev.enola.web;

import com.google.common.net.MediaType;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Descriptors.DescriptorValidationException;

import dev.enola.common.io.resource.MemoryResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.common.protobuf.TypeRegistryWrapper;
import dev.enola.core.EnolaException;
import dev.enola.core.proto.EnolaServiceGrpc.EnolaServiceBlockingStub;
import dev.enola.core.proto.GetFileDescriptorSetRequest;
import dev.enola.core.proto.GetThingRequest;

import java.io.IOException;
import java.net.URI;

// TODO Merge this with class UI, to re-use code better
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

    @Override
    public ListenableFuture<ReadableResource> handle(URI uri) {
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
        if (path.startsWith("/api/")) {
            var iri = path.substring("/api/".length());
            getThingJSON(iri, resource);
        } else {
            // TODO 404 instead 500 (needs API changes)
            throw new IllegalArgumentException("404 - Unknown URI: " + uri);
        }
    }

    private void getThingJSON(String iri, WritableResource resource)
            throws EnolaException, IOException {
        var request = GetThingRequest.newBuilder().setIri(iri).build();
        var response = service.getThing(request);
        var thing = response.getThing();

        getProtoIO().write(thing, resource);
    }

    private ProtoIO getProtoIO() {
        if (protoIO == null) {
            protoIO = new ProtoIO(typeRegistryWrapper.get());
        }
        return protoIO;
    }
}

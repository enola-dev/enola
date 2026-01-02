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

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.common.protobuf.Anys;
import dev.enola.common.protobuf.ValidationException;
import dev.enola.core.EnolaException;
import dev.enola.core.EnolaService;
import dev.enola.core.EnolaServiceProvider;
import dev.enola.core.proto.EnolaServiceGrpc;
import dev.enola.core.proto.EnolaServiceGrpc.EnolaServiceBlockingStub;
import dev.enola.core.proto.GetThingRequest;
import dev.enola.rdf.io.RdfMediaTypes;
import dev.enola.thing.KIRI;
import dev.enola.thing.message.ProtoTypes;
import dev.enola.thing.message.ThingExt;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.proto.Things;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

public class EnolaGrpcServerTest {

    @Rule public SingletonRule r = $(MediaTypeProviders.set(new RdfMediaTypes()));

    private final ResourceProvider rp = new ResourceProviders(new ClasspathResource.Provider());
    private final EnolaServiceProvider esp = new EnolaServiceProvider(rp);
    private final EnolaService service;

    public EnolaGrpcServerTest() throws ValidationException, IOException, EnolaException {
        service = esp.getEnolaService();
    }

    @Test
    public void remoting() throws Exception {
        try (var enolaServer = new EnolaGrpcServer(esp, service)) {
            // similarly in dev.enola.demo.ServerTest
            enolaServer.start(0);
            var port = enolaServer.getPort();
            var endpoint = "localhost:" + port;
            try (var enolaClient = new EnolaGrpcClientProvider(endpoint, false)) {
                check(enolaClient.get());
            }
        }
    }

    @Test
    public void inProcess() throws Exception {
        try (var enolaServer = new EnolaGrpcInProcess(esp, service, false)) {
            check(enolaServer.get());
        }
    }

    private void check(EnolaServiceGrpc.EnolaServiceBlockingStub client)
            throws InvalidProtocolBufferException {
        // TODO checkGetProtoMessage(client);
        // TODO checkGetProtoField(client);
        checkGetYAML(client);
    }

    private Things getThings(EnolaServiceBlockingStub client, String iri)
            throws InvalidProtocolBufferException {
        var request = GetThingRequest.newBuilder().setIri(iri).build();
        var response = client.getThing(request);
        var any = response.getThing();
        // TODO Need to check if the Any is multiple Things or single Thing? Or ditch.. too complex!
        return Anys.unpack(any, Things.class);
    }

    private void checkGetProtoMessage(EnolaServiceBlockingStub client)
            throws InvalidProtocolBufferException {
        checkGetProtoMessage(client, Timestamp.getDescriptor());
        checkGetProtoMessage(client, Things.getDescriptor());
        checkGetProtoMessage(client, Thing.getDescriptor());
    }

    private void checkGetProtoMessage(EnolaServiceBlockingStub client, Descriptor descriptor)
            throws InvalidProtocolBufferException {
        var iri = ProtoTypes.getMessageERI(descriptor);
        var things = getThings(client, iri);
        var thing = things.getThingsList().get(0);
        // As per ThingMetadataProvider (TODO Just test that directly instead!)
        var label = ThingExt.getString(thing, KIRI.RDFS.LABEL);
        assertThat(label).isNotEmpty();
    }

    private void checkGetProtoField(EnolaServiceBlockingStub client)
            throws InvalidProtocolBufferException {
        var iri = ProtoTypes.getFieldERI(Timestamp.getDescriptor().findFieldByNumber(1));
        var things = getThings(client, iri);
        var thing = things.getThingsList().get(0);
        // As per ThingMetadataProvider (TODO Just test that directly instead!)
        var label = ThingExt.getString(thing, KIRI.RDFS.LABEL);
        assertThat(label).isEqualTo("seconds");
    }

    private void checkGetYAML(EnolaServiceBlockingStub client)
            throws InvalidProtocolBufferException {
        var things = getThings(client, "classpath:/picasso.ttl");
        assertThat(things.getThingsList()).hasSize(2);
        // TODO assertThat it contains Dalí & Picasso from picasso.thing.yaml
    }
}

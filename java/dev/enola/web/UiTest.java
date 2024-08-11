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
package dev.enola.web;

import static com.google.common.truth.Truth.assertThat;

import static java.net.URI.create;

import com.google.common.net.MediaType;
import com.google.protobuf.Any;

import dev.enola.common.io.iri.namespace.NamespaceConverterWithRepository;
import dev.enola.common.io.iri.namespace.NamespaceRepositoryEnolaDefaults;
import dev.enola.common.io.metadata.MetadataProvider;
import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.common.protobuf.Timestamps2;
import dev.enola.core.EnolaException;
import dev.enola.core.EnolaService;
import dev.enola.core.EnolaServiceProvider;
import dev.enola.core.grpc.EnolaGrpcInProcess;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.proto.Entity;
import dev.enola.core.proto.GetThingRequest;
import dev.enola.core.proto.GetThingResponse;
import dev.enola.core.proto.ID;
import dev.enola.core.proto.ListEntitiesRequest;
import dev.enola.core.proto.ListEntitiesResponse;
import dev.enola.data.ProviderFromIRI;
import dev.enola.datatype.DatatypeRepositoryBuilder;
import dev.enola.thing.message.ThingProviderAdapter;
import dev.enola.thing.metadata.ThingMetadataProvider;
import dev.enola.thing.proto.Thing;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.time.Instant;

public class UiTest {
    @Test
    public void testUi() throws Exception {
        var addr = new InetSocketAddress(0);
        try (var server = new SunServer(addr)) {
            var rp = new ResourceProviders();
            var ekr = new EntityKindRepository();
            var esp = new EnolaServiceProvider(ekr, rp);
            var service = new TestService();
            try (var grpc = new EnolaGrpcInProcess(esp, service, false)) {
                var testGrpcService = grpc.get();
                new UI(
                                testGrpcService,
                                getMetadataProvider(new EnolaThingProvider(testGrpcService)))
                        .register(server);
                server.start();
                var port = server.getInetAddress().getPort();
                var prefix = "http://localhost:" + port;

                var uri1 = create(prefix + "/ui/bad-page-404");
                var response1 = rp.getResource(uri1);
                assertThat(response1.mediaType()).isEqualTo(MediaType.HTML_UTF_8);
                assertThat(response1.charSource().read()).contains("Enola");
                assertThat(response1.charSource().read()).contains("404");

                var uri2 = create(prefix + "/ui/test.demo/123");
                var response2 = rp.getResource(uri2).charSource().read();
                assertThat(response2).contains("Enola");
                assertThat(response2).contains("test.demo/123");
                assertThat(response2).contains("<table class=\"thing\">");
                assertThat(response2).contains("test.demo/123");

                // Linked Data
                assertThat(response2)
                        .contains(
                                "<a href=/ui/enola:/enola.dev/proto/message/dev.enola.core.Entity"
                                        + ">dev.enola.core.Entity");

                // TODO find some DOM Diff type thing to compare response2 with /expected-book.html?

                // @Test void enolaThingProvider() {
                // var tsIRI = "enola:/enola.dev/proto/field/google.protobuf.Timestamp/1";
                // var thingProvider = new EnolaThingProvider(testGrpcService);
                // var tsThing = thingProvider.getThing(tsIRI);
                // assertThat(ThingExt.getString(tsThing, KIRI.RDFS.LABEL)).isEqualTo("seconds");

                // @Test void thingMetadataProvider() {
                // var thingMetadataProvider = new ThingMetadataProvider(thingProvider);
                // var label = thingMetadataProvider.getLabel(tsIRI);
                // assertThat(label).isEqualTo("seconds");
            }
        }
    }

    private MetadataProvider getMetadataProvider(ProviderFromIRI<Thing> thingProvider) {
        // TODO look up in global repository!
        var datatypeRepository = new DatatypeRepositoryBuilder().build();

        // TODO This must be configurable & dynamic...
        var namespaceRepo = NamespaceRepositoryEnolaDefaults.INSTANCE;
        var namespaceConverter = new NamespaceConverterWithRepository(namespaceRepo);

        return new ThingMetadataProvider(
                new ThingProviderAdapter(thingProvider, datatypeRepository), namespaceConverter);
    }

    private static class TestService implements EnolaService {
        @Override
        public GetThingResponse getThing(GetThingRequest r) throws EnolaException {
            var id = ID.newBuilder().setNs("test").setEntity("demo").addPaths("123");
            var now = Timestamps2.fromInstant(Instant.now());
            var entity = Entity.newBuilder().setId(id).setTs(now).build();
            return GetThingResponse.newBuilder().setThing(Any.pack(entity)).build();
        }

        @Override
        public ListEntitiesResponse listEntities(ListEntitiesRequest r) throws EnolaException {
            return ListEntitiesResponse.newBuilder().build();
        }
    }
}

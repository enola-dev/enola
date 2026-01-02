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

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import static java.net.URI.create;

import com.google.common.net.MediaType;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.core.EnolaServiceProvider;
import dev.enola.core.grpc.EnolaGrpcInProcess;
import dev.enola.core.proto.*;
import dev.enola.data.ProviderFromIRI;
import dev.enola.data.iri.namespace.repo.NamespaceConverterWithRepository;
import dev.enola.data.iri.namespace.repo.NamespaceRepositoryEnolaDefaults;
import dev.enola.datatype.DatatypeRepositoryBuilder;
import dev.enola.thing.message.AlwaysThingProviderAdapter;
import dev.enola.thing.metadata.ThingMetadataProvider;
import dev.enola.thing.proto.Thing;
import dev.enola.web.netty.NettyHttpServer;

import org.junit.Rule;
import org.junit.Test;

public class UiTest {

    public @Rule SingletonRule r = $(MediaTypeProviders.set(new MediaTypeProviders()));

    @Test
    public void testUi() throws Exception {
        // TODO Change this to use a "real" set-up; to detect e.g. broken wiring issues
        // TODO Try new ResourceProviders(new ClasspathResource.Provider())
        var rp = new ResourceProviders();
        var esp = new EnolaServiceProvider(rp);
        var service = new RestTest.TestService();
        try (var grpc = new EnolaGrpcInProcess(esp, service, false)) {
            var testGrpcService = grpc.get();
            var ui =
                    new UI(
                            testGrpcService,
                            getMetadataProvider(new EnolaThingProvider(testGrpcService)));
            var handlers = new WebHandlers();
            ui.register(handlers);
            try (var server = new NettyHttpServer(0, handlers)) {

                server.start();
                var port = server.getInetAddress().getPort();
                var prefix = "http://localhost:" + port;

                var uri1 = create(prefix + "/ui/bad-page-404");
                var response1 = rp.getResource(uri1);
                assertThat(response1.mediaType()).isEqualTo(MediaType.HTML_UTF_8);
                assertThat(response1.charSource().read()).contains("Enola");
                assertThat(response1.charSource().read()).contains("404");

                var uri2 = create(prefix + "/ui/http://example.org/test");
                var response2 = rp.getResource(uri2).charSource().read();
                assertThat(response2).contains("Enola");
                assertThat(response2).contains("http://example.org/test");
                // TODO assertThat(response2).contains("<table class=\"thing\">");

                // Linked Data
                /* TODO assertThat(response2)
                        .contains(
                                "<a href=/ui/enola:/enola.dev/proto/message/dev.enola.core.Entity"
                                        + ">dev.enola.core.Entity");
                */

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

    // TODO Why is this test specific, and not the normal production one?
    private ThingMetadataProvider getMetadataProvider(ProviderFromIRI<Thing> thingProvider) {
        var datatypeRepository = new DatatypeRepositoryBuilder().build();
        var namespaceRepo = NamespaceRepositoryEnolaDefaults.INSTANCE;
        var namespaceConverter = new NamespaceConverterWithRepository(namespaceRepo);

        return new ThingMetadataProvider(
                new AlwaysThingProviderAdapter(thingProvider, datatypeRepository),
                namespaceConverter);
    }
}

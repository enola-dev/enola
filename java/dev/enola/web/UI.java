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

import static com.google.common.net.MediaType.HTML_UTF_8;
import static com.google.common.util.concurrent.Futures.immediateFuture;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.ExtensionRegistryLite;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.MemoryResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ReplacingResource;
import dev.enola.common.io.resource.StringResource;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.common.protobuf.ProtobufMediaTypes;
import dev.enola.common.protobuf.TypeRegistryWrapper;
import dev.enola.core.EnolaException;
import dev.enola.core.proto.EnolaServiceGrpc.EnolaServiceBlockingStub;
import dev.enola.core.proto.GetFileDescriptorSetRequest;
import dev.enola.core.proto.GetThingRequest;
import dev.enola.core.view.EnolaMessages;
import dev.enola.thing.gen.LinkTransformer;
import dev.enola.thing.gen.gexf.GexfGenerator;
import dev.enola.thing.gen.visjs.VisJsTimelineGenerator;
import dev.enola.thing.message.ProtoThingMetadataProvider;
import dev.enola.thing.metadata.ThingMetadataProvider;
import dev.enola.thing.repo.ThingRepository;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class UI implements WebHandler {

    // TODO Use Appendable-based approach, for better memory efficiency, and less String "trashing"

    private static final ReadableResource HTML_FRAME =
            new ClasspathResource("templates/index.html", HTML_UTF_8);

    private static final ClasspathResource FOUR_O_FOUR =
            new ClasspathResource("static/404.html", HTML_UTF_8);

    private final EnolaServiceBlockingStub service;
    private final TypeRegistryWrapper typeRegistryWrapper;
    private final EnolaMessages enolaMessages;
    private final EnolaThingProvider /* TODO ThingProvider*/ thingProvider;
    private final LinkTransformer linkTransformer = new UiLinkTransformer();
    private final ThingUI thingUI;
    private final ThingsConverterWrapperHandler timelineHandler;
    private final ThingsConverterWrapperHandler gexfHandler;
    private ProtoIO protoIO;

    public UI(EnolaServiceBlockingStub service, ThingMetadataProvider metadataProvider)
            throws DescriptorValidationException {
        this.service = service;
        var gfdsr = GetFileDescriptorSetRequest.newBuilder().build();
        var fds = service.getFileDescriptorSet(gfdsr).getProtos();
        typeRegistryWrapper = TypeRegistryWrapper.from(fds);
        var extensionRegistry = ExtensionRegistryLite.getEmptyRegistry();
        enolaMessages = new EnolaMessages(typeRegistryWrapper, extensionRegistry);
        thingProvider = new EnolaThingProvider(service);

        var protoThingMetadataProvider = new ProtoThingMetadataProvider(metadataProvider);
        thingUI = new ThingUI(protoThingMetadataProvider, linkTransformer);

        ThingRepository thingRepository = new ProtoToThingRepository(thingProvider);
        timelineHandler =
                new ThingsConverterWrapperHandler(
                        thingRepository,
                        new VisJsTimelineGenerator(metadataProvider, linkTransformer));
        gexfHandler =
                new ThingsConverterWrapperHandler(
                        thingRepository, new GexfGenerator(metadataProvider));
    }

    public void register(WebHandlers handlers) {
        // TODO Hard-coding this here like that is non-sense of course... will be fixed later!
        // BTW It's ./public/ instead of ./web/public/ only because web/README.md assumes `cd web`.
        var fixMeToNotBeHardCoded = new File("./web-out/bundle/");
        handlers.register("/wui/", new StaticWebHandler("/wui/", fixMeToNotBeHardCoded));
        handlers.register("/ui/static/", new StaticWebHandler("/ui/static/", "static"));
        handlers.register("/ui", this);
        handlers.register("/timeline", timelineHandler);
        handlers.register("/gexf", gexfHandler);
        // TODO Create HTML page “frame” from template, with body from another template
        handlers.register("", uri -> immediateFuture(FOUR_O_FOUR));
    }

    @Override
    public ListenableFuture<ReadableResource> handle(URI uri) {
        try {
            String html = getHTML(uri);
            var resource = StringResource.of(html, HTML_UTF_8);
            return immediateFuture(resource);
        } catch (EnolaException | IOException | ConversionException e) {
            return Futures.immediateFailedFuture(e);
        }
    }

    private String getHTML(URI uri) throws EnolaException, IOException, ConversionException {
        var path = uri.getPath();
        var eri = (path.length() > 4) ? path.substring("/ui/".length()) : "";
        return getThingHTML(eri);
    }

    private String getThingHTML(String iri)
            throws EnolaException, IOException, ConversionException {
        var thing = thingProvider.get(iri);
        var thingHTML =
                thing != null
                        ? thingUI.html(thing).toString()
                        // TODO Remove copy/paste duplication :( from here and in 404.html
                        : "Not found: <code>"
                                + iri
                                + "</code>; try e.g. <a href=\"/ui/enola:/\"><code>enola:/</code>"
                                + " for the index</a> or e.g. <a"
                                + " href=\"/wui/index.html?q=enola:/inline\">the Network Graph</a>"
                                + " or the <a href=\"/timeline?q=enola:/inline\">the"
                                + " Timeline</a>...";

        return new ReplacingResource(
                        HTML_FRAME,
                        "%%ERI%%",
                        iri,
                        "%%THING%%",
                        thingHTML,
                        "%%YAML%%",
                        getYAML(iri))
                .charSource()
                .read();
    }

    private String getYAML(String iri) throws IOException {
        // TODO Replace this with a *.yaml (et al.) link in the UI!
        var request = GetThingRequest.newBuilder().setIri(iri).build();
        var response = service.getThing(request);
        var any = response.getThing();
        if (Strings.isNullOrEmpty(any.getTypeUrl())) return "";

        var message = enolaMessages.toMessage(any);
        var yamlResource = new MemoryResource(ProtobufMediaTypes.PROTOBUF_YAML_UTF_8);
        getProtoIO().write(message, yamlResource);
        return yamlResource.charSource().read();
    }

    private ProtoIO getProtoIO() {
        if (protoIO == null) {
            protoIO = new ProtoIO(typeRegistryWrapper.get());
        }
        return protoIO;
    }
}

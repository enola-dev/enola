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
package dev.enola.web.ui;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.StringResource;
import dev.enola.core.EnolaException;
import dev.enola.core.EnolaService;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.util.proto.EnolaString;
import com.google.protobuf.Any;
import com.google.protobuf.*;

import dev.enola.core.IDs;
import dev.enola.view.proto.SingleEntityView;
import dev.enola.view.proto.ViewItem;
import dev.enola.core.meta.docgen.Soy;
import dev.enola.core.proto.Entity;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.GetEntityRequest;
import dev.enola.core.proto.ID;
import dev.enola.web.StaticWebHandler;
import dev.enola.web.WebHandler;
import dev.enola.web.WebServer;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class UI implements WebHandler {

    private final Soy soy =
            new Soy.Builder()
                    .addSoy("ui.soy")
                    .addProto(
                        SingleEntityView.getDescriptor(),
                        ViewItem.getDescriptor())
                    .build();

    private final EnolaService service;
    private final EntityKindRepository ekr;

    public UI(EnolaService service, EntityKindRepository ekr) {
        this.service = service;
        this.ekr = ekr;
    }

    public void register(WebServer server) {
        // The /ui/ prefix is to allow e.g. /api/ for a future REST endpoint
        server.register("/ui", this);
        server.register("/ui/static/", new StaticWebHandler("/ui/static/", "static"));
    }

    @Override
    public ListenableFuture<ReadableResource> get(URI uri) {
        try {
            String html = getHTML(uri);
            var resource = new StringResource(html, MediaType.HTML_UTF_8);
            return Futures.immediateFuture(resource);
        } catch (EnolaException | IOException e) {
            return Futures.immediateFailedFuture(e);
        }
    }

    private String getHTML(URI uri) throws EnolaException, IOException {
        System.out.println("+++++++++++++++++++++++");
        var path = uri.getPath();
        System.out.println("+++++++++++++++++++++++  " + path);
        // TODO implement the to-many relationship
        if (path.startsWith("/ui/entity/")) {
            var idString = path.substring("/ui/entity/".length());
            var id = IDs.parse(idString);
        System.out.println("+++++++++++++++++++++++  " + id);
            return getEntityHTML(id);
        } else {
            // TODO Create HTML page “frame” from .soy, with body from another .soy
            return new ClasspathResource("404.html").charSource().read();
        }
    }

    private String getRelatedRelativePath(ID id, boolean many){
        var path =  "/ui/entity/" + IDs.toPath(id);
        return path;
        // TODO implement the to-many relationship
    }


    private String getEntityHTML(ID id) throws EnolaException {
        var request = GetEntityRequest.newBuilder().setId(id).build();
        var response = service.getEntity(request);
        Entity entity = response.getEntity();
        System.out.println("+++++++++++++++++++++++ 11 " + id);

        EntityKind ek = ekr.getOptional(entity.getId()).get();
        System.out.println("+++++++++++++++++++++++ 22 " + id);

        var singleEntityView = SingleEntityView.newBuilder();
        singleEntityView.setId(IDs.toPath(entity.getId()));
        singleEntityView.setKindName(entity.getId().getEntity());
        singleEntityView.setFullName(entity.getId().getNs()+"."+entity.getId().getEntity());
        for (var kv : entity.getLinkMap().entrySet()){
            var viewItem = ViewItem.newBuilder()
                            .setRef(kv.getValue())
                            .setLabel(kv.getKey())
                            .build();
            singleEntityView.addLink(viewItem);
        }

        for (var kv : entity.getDataMap().entrySet()){
            Any any = kv.getValue();
            String value = "-";
            try {
                value = any.unpack(EnolaString.class).getValue();
            } catch (InvalidProtocolBufferException e){
                System.out.println(e);
            }
            var viewItem = ViewItem.newBuilder()
                            .setLabel(ek.getDataMap().get(kv.getKey()).getLabel())
                            .setRef("")
                            .setValue(value)
                            .build();
            singleEntityView.addData(viewItem);
        }
        for (var kv : entity.getRelatedMap().entrySet()){
            var many = true;
            var tags = ek.getRelatedMap().get(kv.getKey()).getTagsMap();
            if (tags.containsKey("cardinality") && tags.get("cardinality").equals("one")) {
                many = false;
            }
            var viewItem = ViewItem.newBuilder()
                            .setLabel(kv.getKey())
                            .setRef(getRelatedRelativePath(kv.getValue(), many))
                            .setValue(ek.getRelatedMap().get(kv.getKey()).getLabel())
                            .build();

            singleEntityView.addRelated(viewItem);
        }
        Map<String, ?> params = ImmutableMap.of("sv",singleEntityView.build());
        var renderer = soy.newRenderer("dev.enola.ui.page", params);
        // TODO Make this async - but I don't understand Soy's async API...
        return renderer.renderHtml().get().getContent();
    }
}

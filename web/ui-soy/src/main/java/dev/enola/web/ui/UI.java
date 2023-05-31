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
import dev.enola.core.IDs;
import dev.enola.core.proto.Entity;
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
                    .addProto(ID.getDescriptor(), Entity.getDescriptor())
                    .build();

    private final EnolaService service;

    public UI(EnolaService service) {
        this.service = service;
    }

    public void register(WebServer server) {
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
        var path = uri.getPath();
        if (path.startsWith("/ui/entity/")) {
            var idString = path.substring("/ui/entity/".length());
            var id = IDs.parse(idString);
            return getEntityHTML(id);
        } else {
            // TODO Create HTML page “frame” from .soy, with body from another .soy
            return new ClasspathResource("404.html").charSource().read();
        }
    }

    private String getEntityHTML(ID id) throws EnolaException {
        var request = GetEntityRequest.newBuilder().setId(id).build();
        var response = service.getEntity(request);
        var entity = response.getEntity();

        Map<String, ?> params = ImmutableMap.of("e", entity);
        var renderer = soy.newRenderer("dev.enola.ui.page", params);
        // TODO Make this async - but I don't understand Soy's async API...
        return renderer.renderHtml().get().getContent();
    }
}

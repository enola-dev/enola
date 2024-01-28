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
package dev.enola.web.ui;

import com.google.common.net.MediaType;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ReplacingResource;
import dev.enola.common.io.resource.StringResource;
import dev.enola.core.EnolaException;
import dev.enola.core.proto.EnolaServiceGrpc.EnolaServiceBlockingStub;
import dev.enola.core.proto.GetEntityRequest;
import dev.enola.core.view.ThingViews;
import dev.enola.web.StaticWebHandler;
import dev.enola.web.WebHandler;
import dev.enola.web.WebServer;

import java.io.IOException;
import java.net.URI;

public class UI implements WebHandler {

    // TODO Use Appendable-based approach, for better memory efficiency, and less String "trashing"

    private static final ReadableResource HTML_FRAME =
            new ClasspathResource("templates/index.html");
    private final EnolaServiceBlockingStub service;
    private final ThingUI thingUI = new ThingUI();

    public UI(EnolaServiceBlockingStub service) {
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
            var eri = path.substring("/ui/entity/".length());
            return getEntityHTML(eri);
        } else {
            // TODO Create HTML page “frame” from template, with body from another template
            return new ClasspathResource("static/404.html").charSource().read();
        }
    }

    private String getEntityHTML(String eri) throws EnolaException, IOException {
        var request = GetEntityRequest.newBuilder().setEri(eri).build();
        var response = service.getEntity(request);
        var entity = response.getEntity();
        var thingView = ThingViews.from(entity);
        return new ReplacingResource(HTML_FRAME, "%%MAIN%%", thingUI.html(thingView))
                .charSource()
                .read();
    }
}

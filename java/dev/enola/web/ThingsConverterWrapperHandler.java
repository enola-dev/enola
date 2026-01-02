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

import com.google.common.net.MediaType;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.resource.MemoryResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.core.thing.ListThingService;
import dev.enola.thing.gen.ThingsIntoAppendableConverter;
import dev.enola.thing.repo.ThingRepository;

import java.io.IOException;
import java.net.URI;

class ThingsConverterWrapperHandler implements WebHandler {

    private final ThingRepository thingRepository;
    private final ThingsIntoAppendableConverter converter;

    ThingsConverterWrapperHandler(
            ThingRepository thingRepository, ThingsIntoAppendableConverter converter) {
        this.thingRepository = thingRepository;
        this.converter = converter;
    }

    @Override
    public ListenableFuture<ReadableResource> handle(URI uri) {
        var query = URIs.getQueryMap(uri).get("q");
        if (query == null) throw new IllegalArgumentException("Missing ?q=");
        if (!query.equals(ListThingService.ENOLA_ROOT_LIST_THINGS))
            throw new IllegalArgumentException(
                    "Currently (TODO) only supports ?q="
                            + ListThingService.ENOLA_ROOT_LIST_THINGS
                            + ", not: ?q="
                            + query);

        var things = thingRepository.list();
        var resource = new MemoryResource(MediaType.HTML_UTF_8);
        try {
            converter.convertIntoOrThrow(things, resource);
            return Futures.immediateFuture(resource);
        } catch (IOException e) {
            return Futures.immediateFailedFuture(e);
        }
    }
}

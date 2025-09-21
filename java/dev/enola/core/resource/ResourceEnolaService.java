/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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
package dev.enola.core.resource;

import com.google.protobuf.Any;

import dev.enola.common.convert.OptionalConverter;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.core.EnolaException;
import dev.enola.core.EnolaService;
import dev.enola.core.proto.*;
import dev.enola.rdf.io.RdfResourceIntoProtoThingConverter;
import dev.enola.thing.message.ProtoThingProvider;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.template.Templates;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

/**
 * ResourceEnolaService implements {@link EnolaService} by fetching bytes from a {@link
 * ResourceProvider} and converting them into Things using an {@link OptionalConverter}, such as its
 * default {@link RdfResourceIntoProtoThingConverter}.
 */
public class ResourceEnolaService implements EnolaService, ProtoThingProvider {
    // TODO Remove implements EnolaService (only ProtoThingProvider)

    private static final Logger LOG = LoggerFactory.getLogger(ResourceEnolaService.class);

    private final RdfResourceIntoProtoThingConverter resourceToThingConverter;
    private final ResourceProvider rp;

    public ResourceEnolaService(
            ResourceProvider rp, RdfResourceIntoProtoThingConverter resourceToThingConverter) {
        this.resourceToThingConverter = resourceToThingConverter;
        this.rp = rp;
    }

    public ResourceEnolaService(ResourceProvider rp) {
        this(rp, new RdfResourceIntoProtoThingConverter(rp));
    }

    public @Nullable List<Thing.Builder> getThingsAsBuilderList(String iri) {
        if (Templates.hasVariables(iri)) return null;
        var uri = URI.create(iri); // TODO IRIs.toURI(iri);
        var resource = rp.getReadableResource(uri);
        if (resource == null) {
            LOG.debug("Could not load: {}", iri);
            return null;
        }
        var opt = resourceToThingConverter.convert(resource);
        if (opt.isEmpty()) {
            LOG.debug("Filtered; or unknown format, no parser for {}", iri);
            return null;
        }
        return opt.get();
    }

    @Override
    public GetThingsResponse getThings(GetThingsRequest r) throws EnolaException {
        var responseBuilder = GetThingsResponse.newBuilder();
        List<Thing.Builder> things = getThingsAsBuilderList(r.getIri());
        if (things != null) {
            for (var thingBuilder : things) {
                responseBuilder.addThings(thingBuilder.build());
            }
        }
        return responseBuilder.build();
    }

    @Override
    public @Nullable Any get(String iri) {
        var things = getThingsAsBuilderList(iri);
        if (things == null) return null;

        var message = resourceToThingConverter.asMessage(things).build();
        return Any.pack(message);
    }

    @Override
    public GetThingResponse getThing(GetThingRequest r) throws EnolaException {
        var builder = GetThingResponse.newBuilder();
        var any = get(r.getIri());
        if (any != null) builder.setThing(any);
        return builder.build();
    }
}

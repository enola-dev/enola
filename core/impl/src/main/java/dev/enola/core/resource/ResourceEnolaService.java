/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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

import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.iri.IRIs;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.core.EnolaException;
import dev.enola.core.EnolaService;
import dev.enola.core.proto.GetThingRequest;
import dev.enola.core.proto.GetThingResponse;
import dev.enola.core.proto.ListEntitiesRequest;
import dev.enola.core.proto.ListEntitiesResponse;
import dev.enola.core.rosetta.ResourceIntoThingConverter;

import java.net.URISyntaxException;

public class ResourceEnolaService implements EnolaService {

    private final ResourceIntoThingConverter resourceToThingConverter =
            new ResourceIntoThingConverter();
    private final ResourceProvider rp;

    public ResourceEnolaService(ResourceProvider rp) {
        this.rp = rp;
    }

    @Override
    public GetThingResponse getThing(GetThingRequest r) throws EnolaException {
        try {
            var iri = IRIs.toURI(r.getIri());
            var resource = rp.getReadableResource(iri);
            var opt = resourceToThingConverter.convert(resource);
            if (opt.isEmpty()) {
                throw new EnolaException(
                        "Unknown format, no parser for " + resource.mediaType() + " from " + iri);
            }
            var thingsList = opt.get();
            var message = resourceToThingConverter.asMessage(thingsList).build();
            var any = Any.pack(message);
            return GetThingResponse.newBuilder().setThing(any).build();

        } catch (URISyntaxException | ConversionException e) {
            throw new EnolaException("Bad IRI: " + r.getIri(), e);
        }
    }

    @Override
    public ListEntitiesResponse listEntities(ListEntitiesRequest r) throws EnolaException {
        throw new UnsupportedOperationException("listEntities will eventually be removed");
    }
}

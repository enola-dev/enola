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
package dev.enola.core;

import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.core.iri.URITemplateMatcherChain;
import dev.enola.core.meta.proto.Type;
import dev.enola.core.proto.GetThingRequest;
import dev.enola.core.proto.GetThingResponse;
import dev.enola.core.proto.ID;
import dev.enola.core.proto.ListEntitiesRequest;
import dev.enola.core.proto.ListEntitiesResponse;
import dev.enola.core.resource.ResourceEnolaService;
import dev.enola.core.thing.ThingService;

class EnolaServiceRegistry implements EnolaService {

    private final URITemplateMatcherChain<ThingService> matcher;
    private ResourceEnolaService resourceEnolaService;

    public static Builder builder() {
        return new Builder();
    }

    private EnolaServiceRegistry(
            URITemplateMatcherChain<ThingService> matcherChain,
            ResourceEnolaService resourceEnolaService) {
        this.matcher = matcherChain;
        this.resourceEnolaService = resourceEnolaService;
    }

    @Override
    public GetThingResponse getThing(GetThingRequest r) throws EnolaException {
        var opt = matcher.match(r.getIri());
        if (opt.isEmpty()) {
            // Nota bene: If the IRI didn't match any registered Types,
            // then we assume it points to a Resource of a Thing, and we (try to) load it:
            //
            // TODO This is kind of wrong... all EntityKind and Type IRIs should be (but are not,
            // yet?)  of scheme enola: and so we should only fall back to the ResourceEnolaService
            // for any other non-enola: scheme URIs!
            return resourceEnolaService.getThing(r);
        }

        var entry = opt.get();
        var delegate = entry.getKey();
        var parameters = entry.getValue();
        var things = delegate.getThing(r.getIri(), parameters);
        return GetThingResponse.newBuilder().setThing(things).build();
    }

    @Override
    public ListEntitiesResponse listEntities(ListEntitiesRequest r) throws EnolaException {
        var opt = matcher.match(r.getEri());
        if (opt.isEmpty())
            throw new UnsupportedOperationException("listEntities will eventually be removed");
        return opt.get().getKey().listEntities(r);
    }

    public static class Builder {
        private final URITemplateMatcherChain.Builder<ThingService> b =
                URITemplateMatcherChain.builder();

        public Builder register(ID ekid, ThingService service) {
            // URI for get():
            var uriTemplateWithPath = IDs.toURITemplate(ekid);
            b.add(uriTemplateWithPath, service);

            // URI for list():
            var ekidWithoutPath = IDs.withoutPath(ekid);
            var uriTemplateWithoutPath = IDs.toURITemplate(ekidWithoutPath);
            b.add(uriTemplateWithoutPath, service);

            return this;
        }

        public void register(Type type, ThingService service) {
            b.add(type.getUri(), service);
        }

        public EnolaServiceRegistry build() {
            return new EnolaServiceRegistry(
                    b.build(), new ResourceEnolaService(new ResourceProviders()));
        }
    }
}

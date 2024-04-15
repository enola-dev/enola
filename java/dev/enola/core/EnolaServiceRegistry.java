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

import com.google.protobuf.Any;

import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.core.iri.URITemplateMatcherChain;
import dev.enola.core.meta.proto.Type;
import dev.enola.core.proto.GetThingRequest;
import dev.enola.core.proto.GetThingResponse;
import dev.enola.core.proto.ID;
import dev.enola.core.proto.ListEntitiesRequest;
import dev.enola.core.proto.ListEntitiesResponse;
import dev.enola.core.resource.ResourceEnolaService;
import dev.enola.core.thing.ListThingService;
import dev.enola.core.thing.ThingRepositoryThingService;
import dev.enola.core.thing.ThingService;
import dev.enola.thing.ThingRepository;
import dev.enola.thing.message.ProtoThingRepository;

import java.util.Map;

class EnolaServiceRegistry implements EnolaService, ProtoThingRepository {

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
    public Any get(String iri) {
        var opt = matcher.match(iri);
        if (opt.isPresent()) {
            var entry = opt.get();
            var delegate = entry.getKey();
            var parameters = entry.getValue();
            Any things = delegate.getThing(iri, parameters);
            return things;

        } else {
            // Nota bene: If the IRI didn't match any registered Types or pre-loaded Things,
            // then we assume it points to a Resource of a Thing, and we (try to) load it:
            //
            // TODO This is kind of wrong... all EntityKind and Type IRIs should be (but are not,
            // yet?)  of scheme enola: and so we should only fall back to the ResourceEnolaService
            // for any other non-enola: scheme URIs!
            return resourceEnolaService.get(iri);
        }
    }

    @Override
    public GetThingResponse getThing(GetThingRequest r) throws EnolaException {
        String iri = r.getIri();
        return GetThingResponse.newBuilder().setThing(get(iri)).build();
    }

    @Override
    public Iterable<String> listIRI() {
        // TODO Should this still also do the same as, and be called by, listEntities() below?
        return matcher.listTemplates();
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

        private ThingService wrap(ThingService service) {
            return new ThingService() {

                @Override
                public Any getThing(String iri, Map<String, String> parameters) {
                    try {
                        return service.getThing(iri, parameters);
                    } catch (RuntimeException e) {
                        throw new RuntimeException(
                                service.toString() + " failed to get: " + iri, e);
                    }
                }

                @Override
                public ListEntitiesResponse listEntities(ListEntitiesRequest r)
                        throws EnolaException {
                    return service.listEntities(r);
                }
            };
        }

        public void register(Type type, ThingService service) {
            b.add(type.getUri(), wrap(service));
        }

        public void register(ThingRepository thingRepository) {
            var thingRepositoryThingService = new ThingRepositoryThingService(thingRepository);
            for (var iri : thingRepository.listIRI()) {
                b.add(iri, wrap(thingRepositoryThingService));
            }
        }

        public EnolaServiceRegistry build() {
            var listThingService = new ListThingService();
            b.add(ListThingService.ENOLA_ROOT_LIST_IRIS, wrap(listThingService));
            b.add(ListThingService.ENOLA_ROOT_LIST_THINGS, wrap(listThingService));
            var uriTemplateMatcherChain = b.build();
            var esr =
                    new EnolaServiceRegistry(
                            uriTemplateMatcherChain,
                            new ResourceEnolaService(new ResourceProviders()));
            listThingService.setProtoThingProvider(esr);
            return esr;
        }
    }
}

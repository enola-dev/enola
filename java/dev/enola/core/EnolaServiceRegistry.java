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
package dev.enola.core;

import com.google.protobuf.Any;

import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.core.proto.*;
import dev.enola.core.resource.ResourceEnolaService;
import dev.enola.core.thing.ListThingService;
import dev.enola.core.thing.ThingRepositoryThingService;
import dev.enola.core.thing.ThingService;
import dev.enola.data.iri.template.URITemplateMatcherChain;
import dev.enola.rdf.io.FilteringResourceIntoProtoThingConverter;
import dev.enola.rdf.io.RdfResourceIntoProtoThingConverter;
import dev.enola.thing.Thing;
import dev.enola.thing.message.JavaThingToProtoThingConverter;
import dev.enola.thing.message.ProtoThingRepository;
import dev.enola.thing.repo.ThingRepository;
import dev.enola.thing.repo.ThingsProvider;

import java.util.Map;

class EnolaServiceRegistry implements EnolaService, ProtoThingRepository {

    private final URITemplateMatcherChain<ThingService> matcher;
    private final ResourceEnolaService resourceEnolaService;
    private final JavaThingToProtoThingConverter converter;

    public static Builder builder() {
        return new Builder();
    }

    private EnolaServiceRegistry(
            URITemplateMatcherChain<ThingService> matcherChain,
            ResourceEnolaService resourceEnolaService) {
        this.matcher = matcherChain;
        this.resourceEnolaService = resourceEnolaService;
        this.converter = new JavaThingToProtoThingConverter();
    }

    @Override
    public GetThingsResponse getThings(GetThingsRequest r) throws EnolaException {
        var iri = r.getIri();
        var builder = GetThingsResponse.newBuilder();
        var opt = matcher.match(iri);
        if (opt.isPresent()) {
            var entry = opt.get();
            var delegate = entry.getKey();
            var parameters = entry.getValue();
            try {
                var javaThings = delegate.getThings(iri, parameters);
                for (var javaThing : javaThings) {
                    builder.addThings(converter.convert(javaThing));
                }

            } catch (EnolaException e) {
                throw new EnolaException(e);
            }
        }
        return builder.build();
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
            // yet?) of scheme enola: and so we should only fall back to the ResourceEnolaService
            // for any other non-enola: scheme URIs!
            return resourceEnolaService.get(iri);
        }
    }

    @Override
    public GetThingResponse getThing(GetThingRequest r) throws EnolaException {
        var builder = GetThingResponse.newBuilder();
        var any = get(r.getIri());
        if (any != null) builder.setThing(any);
        return builder.build();
    }

    @Override
    public Iterable<String> listIRI() {
        // TODO Should this still also do the same as, and be called by, listEntities() below?
        return matcher.listTemplates();
    }

    public static class Builder {
        private final URITemplateMatcherChain.Builder<ThingService> b =
                URITemplateMatcherChain.builder();

        private ThingService wrap(ThingService service) {
            return new ThingService() {

                @Override
                public Iterable<Thing> getThings(String iri, Map<String, String> parameters)
                        throws EnolaException {
                    return service.getThings(iri, parameters);
                }

                @Override
                public Any getThing(String iri, Map<String, String> parameters) {
                    try {
                        return service.getThing(iri, parameters);
                    } catch (RuntimeException e) {
                        throw new RuntimeException(service + " failed to get: " + iri, e);
                    }
                }
            };
        }

        public void register(ThingRepository thingRepository, ThingsProvider thingsProvider) {
            var thingRepositoryThingService =
                    new ThingRepositoryThingService(thingsProvider, thingRepository);
            for (var iri : thingRepository.listIRI()) {
                // TODO Why are we adding *ALL* IRI? Wouldn't only those with Templates suffice?
                b.add(iri, wrap(thingRepositoryThingService));
            }
        }

        public EnolaServiceRegistry build(ResourceProvider rp) {
            var listThingService = new ListThingService();
            b.add(ListThingService.ENOLA_ROOT_LIST_IRIS, wrap(listThingService));
            b.add(ListThingService.ENOLA_ROOT_LIST_THINGS, wrap(listThingService));
            var uriTemplateMatcherChain = b.build();
            var riptc =
                    new FilteringResourceIntoProtoThingConverter(
                            new RdfResourceIntoProtoThingConverter(rp));
            var res = new ResourceEnolaService(rp, riptc);
            var esr = new EnolaServiceRegistry(uriTemplateMatcherChain, res);
            listThingService.setProtoThingProvider(esr);
            return esr;
        }
    }
}

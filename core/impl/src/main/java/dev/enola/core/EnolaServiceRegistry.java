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

import dev.enola.core.iri.URITemplateMatcherChain;
import dev.enola.core.meta.proto.Type;
import dev.enola.core.proto.GetThingRequest;
import dev.enola.core.proto.GetThingResponse;
import dev.enola.core.proto.ID;
import dev.enola.core.proto.ListEntitiesRequest;
import dev.enola.core.proto.ListEntitiesResponse;

class EnolaServiceRegistry implements EnolaService {

    private final URITemplateMatcherChain<EnolaService> matcher;

    public static Builder builder() {
        return new Builder();
    }

    private EnolaServiceRegistry(URITemplateMatcherChain<EnolaService> matcherChain) {
        this.matcher = matcherChain;
    }

    @Override
    public GetThingResponse getThing(GetThingRequest r) throws EnolaException {
        return getDelegate(r.getEri()).getThing(r);
    }

    @Override
    public ListEntitiesResponse listEntities(ListEntitiesRequest r) throws EnolaException {
        return getDelegate(r.getEri()).listEntities(r);
    }

    private EnolaService getDelegate(String eri) {
        var opt = matcher.match(eri);
        // TODO Instead of throwing IllegalArgumentException, return list of URI templates!
        opt.orElseThrow(
                () ->
                        new IllegalArgumentException(
                                "No EnolaService handler registered for: "
                                        + eri
                                        + ", only: "
                                        + matcher.listTemplates()));
        var entry = opt.get();
        var map = entry.getValue();
        var delegate = entry.getKey();

        // TODO map has to somehow be passed to the EnolaService!
        // TODO Is it OK to ditch?! var lookup = IDs.withoutPath(IDs.parse(eri));

        return delegate;
    }

    public static class Builder {
        private final URITemplateMatcherChain.Builder<EnolaService> b =
                URITemplateMatcherChain.builder();

        public Builder register(ID ekid, EnolaService service) {
            // URI for get():
            var uriTemplateWithPath = IDs.toURITemplate(ekid);
            b.add(uriTemplateWithPath, service);

            // URI for list():
            var ekidWithoutPath = IDs.withoutPath(ekid);
            var uriTemplateWithoutPath = IDs.toURITemplate(ekidWithoutPath);
            b.add(uriTemplateWithoutPath, service);

            return this;
        }

        public void register(Type type, EnolaService service) {
            b.add(type.getUri(), service);
        }

        public EnolaServiceRegistry build() {
            return new EnolaServiceRegistry(b.build());
        }
    }
}

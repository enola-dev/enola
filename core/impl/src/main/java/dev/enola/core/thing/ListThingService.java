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
package dev.enola.core.thing;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;

import dev.enola.core.EnolaException;
import dev.enola.core.proto.ListEntitiesRequest;
import dev.enola.core.proto.ListEntitiesResponse;
import dev.enola.thing.message.ProtoThingProvider;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.proto.Things;
import dev.enola.thing.proto.Value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/** ThingService which returns the list of all known Things' IRIs for GET enola:/ */
public class ListThingService implements ThingService {
    private static final Logger LOG = LoggerFactory.getLogger(ListThingService.class);

    public static final String ENOLA_ROOT_LIST_IRIS = "enola:/";
    // TODO "enola:/?inline" would be nicer than "enola:/inline" but fails to match
    public static final String ENOLA_ROOT_LIST_THINGS = "enola:/inline";
    public static final String ENOLA_ROOT_LIST_PROPERTY = "https://enola.dev/thing-iri-list";

    private List<String> iris;
    private ProtoThingProvider protoThingProvider;

    public void setIRIs(List<String> iris) {
        if (this.iris != null) throw new IllegalStateException();
        this.iris = iris;
    }

    public void setProtoThingProvider(ProtoThingProvider protoThingProvider) {
        this.protoThingProvider = protoThingProvider;
    }

    @Override
    public Any getThing(String iri, Map<String, String> parameters) {
        if (ENOLA_ROOT_LIST_THINGS.equals(iri)) {
            var things = Things.newBuilder();
            for (var thingIRI : iris) {
                if (ENOLA_ROOT_LIST_IRIS.equals(thingIRI)) continue;
                if (ENOLA_ROOT_LIST_THINGS.equals(thingIRI)) continue;
                if (thingIRI.startsWith("enola."))
                    // TODO Fix enola.* hack... (get enola.entity_kind & enola.schema doesn't work)
                    continue;
                if (thingIRI.contains("{"))
                    // TODO Return some kinda Template Descriptor Thing...
                    continue;
                var any = protoThingProvider.get(thingIRI);
                if (any.getTypeUrl().endsWith("Thing")) {
                    try {
                        var thing = any.unpack(Thing.class);
                        things.addThings(thing);
                    } catch (InvalidProtocolBufferException e) {
                        throw new IllegalStateException("Huh?!", e);
                    }
                } else {
                    LOG.warn("Skipping non-Thing Any: " + any);
                }
            }
            return Any.pack(things.build());

        } else { // only IRIs, not fully inlined Things
            // TODO Have a static Proto message type for this? And use it e.g. in DocGen?
            var list = Value.List.newBuilder();
            for (var thingIRI : iris) {
                var linkValue = Value.newBuilder().setLink(thingIRI);
                list.addValues(linkValue);
            }
            var value = Value.newBuilder().setList(list);
            var thing = Thing.newBuilder();
            thing.setIri(ENOLA_ROOT_LIST_IRIS);
            thing.putFields(ENOLA_ROOT_LIST_PROPERTY, value.build());
            return Any.pack(thing.build());
        }
    }

    @Override
    public ListEntitiesResponse listEntities(ListEntitiesRequest r) throws EnolaException {
        throw new UnsupportedOperationException(
                "listEntities() will be removed, and never implemented here");
    }
}

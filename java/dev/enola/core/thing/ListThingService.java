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
package dev.enola.core.thing;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;

import dev.enola.core.EnolaException;
import dev.enola.thing.KIRI;
import dev.enola.thing.message.ProtoThingRepository;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.proto.Things;
import dev.enola.thing.proto.Value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/** ThingService which returns the list of all known Things' IRIs for GET enola:/ */
public class ListThingService implements ThingService {
    private static final Logger LOG = LoggerFactory.getLogger(ListThingService.class);

    // See also dev.enola.data.AllQuery
    public static final String ENOLA_ROOT_LIST_IRIS = KIRI.E.LIST_IRIS;
    public static final String ENOLA_ROOT_LIST_THINGS = KIRI.E.LIST_THINGS;
    public static final String ENOLA_ROOT_LIST_PROPERTY = "https://enola.dev/thing-iri-list";

    private ProtoThingRepository protoThingRepository;

    public void setProtoThingProvider(ProtoThingRepository protoThingRepository) {
        this.protoThingRepository = protoThingRepository;
    }

    // Nota bene: The "decoder" (inverse) of this is (currently) in EnolaThingProvider!

    @Override
    public Iterable<dev.enola.thing.Thing> getThings(String iri, Map<String, String> parameters)
            throws EnolaException {
        throw new UnsupportedOperationException(
                "TODO implement after switching from Proto to Java Thing");
    }

    @Override
    public Any getThing(String iri, Map<String, String> parameters) {
        var thingIRIs = protoThingRepository.listIRI();
        if (ENOLA_ROOT_LIST_THINGS.equals(iri)) {
            var things = Things.newBuilder();
            for (var thingIRI : thingIRIs) {
                if (ENOLA_ROOT_LIST_IRIS.equals(thingIRI)) continue;
                if (ENOLA_ROOT_LIST_THINGS.equals(thingIRI)) continue;
                var any = protoThingRepository.get(thingIRI);
                if (any == null) {
                    LOG.error("Any null: {}", thingIRI);
                    continue;
                }
                if (!any.getTypeUrl().endsWith("Thing")) {
                    LOG.warn("Skipping non-Thing Any: {}", any);
                    continue;
                }
                try {
                    var thing = any.unpack(Thing.class);
                    things.addThings(thing);
                } catch (InvalidProtocolBufferException e) {
                    throw new IllegalStateException("Huh?!", e);
                }
            }
            return Any.pack(things.build());

        } else { // only IRIs, not fully inlined Things
            // TODO Have a static Proto message type for this? And use it e.g. in DocGen?
            var list = Value.List.newBuilder();
            for (var thingIRI : thingIRIs) {
                var linkValue = Value.newBuilder().setLink(thingIRI);
                list.addValues(linkValue);
            }
            var value = Value.newBuilder().setList(list);
            var thing = Thing.newBuilder();
            thing.setIri(ENOLA_ROOT_LIST_IRIS);
            thing.putProperties(ENOLA_ROOT_LIST_PROPERTY, value.build());
            return Any.pack(thing.build());
        }
    }
}

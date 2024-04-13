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

import dev.enola.core.EnolaException;
import dev.enola.core.proto.ListEntitiesRequest;
import dev.enola.core.proto.ListEntitiesResponse;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.proto.Value;

import java.util.List;
import java.util.Map;

/** ThingService which returns the list of all known Things' IRIs for GET enola:/ */
public class ListThingService implements ThingService {

    public static final String ENOLA_ROOT_LIST_IRI = "enola:/";
    private static final String ENOLA_ROOT_LIST_PROPERTY = "https://enola.dev/thing-iri-list";

    private List<String> iris;

    public void setIRIs(List<String> iris) {
        this.iris = iris;
    }

    @Override
    public Any getThing(String iri, Map<String, String> parameters) throws EnolaException {
        var list = Value.List.newBuilder();
        for (var thingIRI : iris) {
            var linkValue = Value.newBuilder().setLink(thingIRI);
            list.addValues(linkValue);
        }
        var value = Value.newBuilder().setList(list);
        var thing = Thing.newBuilder();
        thing.setIri(ENOLA_ROOT_LIST_IRI);
        thing.putFields(ENOLA_ROOT_LIST_PROPERTY, value.build());
        return Any.pack(thing.build());
    }

    @Override
    public ListEntitiesResponse listEntities(ListEntitiesRequest r) throws EnolaException {
        throw new UnsupportedOperationException(
                "listEntities() will be removed, and never implemented here");
    }
}

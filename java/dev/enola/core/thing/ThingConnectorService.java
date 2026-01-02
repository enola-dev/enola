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
package dev.enola.core.thing;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Any;

import dev.enola.core.EnolaException;
import dev.enola.core.view.EnolaMessages;
import dev.enola.thing.Thing;
import dev.enola.thing.proto.Things;

import java.util.Map;

public class ThingConnectorService implements ThingService {
    // TODO Rename ThingEnolaService

    private final ImmutableList<ThingConnector> aspects;

    public ThingConnectorService(
            ImmutableList<ThingConnector> aspects, EnolaMessages enolaMessages) {
        this.aspects = aspects;
    }

    public ThingConnectorService(ThingConnector aspect, EnolaMessages enolaMessages) {
        this.aspects = ImmutableList.of(aspect);
    }

    @Override
    public Iterable<Thing> getThings(String iri, Map<String, String> parameters)
            throws EnolaException {
        throw new UnsupportedOperationException(
                "Not implemented, because this class is about to be removed anyways!");
    }

    @Override
    public Any getThing(String iri, Map<String, String> parameters) {
        return Any.pack(getProtoThings(iri, parameters));
    }

    private dev.enola.thing.proto.Things getProtoThings(
            String iri, Map<String, String> parameters) {
        // Builder thing = enolaMessages.newBuilder(type.getProto());
        Things.Builder things = Things.newBuilder();

        for (var aspect : aspects) {
            aspect.augment(things, iri, parameters);
        }

        return things.build();
    }
}

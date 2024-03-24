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

import dev.enola.common.convert.ConversionException;
import dev.enola.core.EnolaException;
import dev.enola.core.iri.URITemplateMatcherChain;
import dev.enola.thing.ThingProvider;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.proto.Things;

import java.io.IOException;

/**
 * ThingConnectorsProvider implements {@link ThingProvider} by delegating to a list of {@link
 * ThingConnector}.
 */
public class ThingConnectorsProvider implements ThingProvider {

    // TODO Align the overlap this has with EnolaThingProvider & EnolaServiceRegistry

    private final URITemplateMatcherChain<ThingConnector> matcher;

    public ThingConnectorsProvider(Iterable<ThingConnector> connectors) {
        var builder = URITemplateMatcherChain.builder();
        for (var connector : connectors) {
            builder.add(connector.type().getUri(), connector);
        }

        this.matcher = builder.build();
    }

    @Override
    public Thing getThing(String iri) throws IOException, ConversionException {
        var opt = matcher.match(iri);
        if (opt.isEmpty()) throw new IllegalArgumentException("No template matched: " + iri);

        var entry = opt.get();
        var thingConnector = entry.getKey();
        var parameters = entry.getValue();

        try {
            var thingsBuilder = Things.newBuilder();
            thingConnector.augment(thingsBuilder, iri, parameters);
            // TODO The get(0) is wrong, we need to return Things here, and UI needs to show all
            return thingsBuilder.build().getThings(0);
        } catch (EnolaException e) {
            throw new IOException("Failed to augment: " + iri, e);
        }
    }
}

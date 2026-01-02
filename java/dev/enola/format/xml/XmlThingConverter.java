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
package dev.enola.format.xml;

import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.xml.XmlResourceParser;
import dev.enola.thing.Thing;
import dev.enola.thing.io.UriIntoThingConverter;
import dev.enola.thing.repo.ThingRepositoryStore;

import java.net.URI;

public class XmlThingConverter implements UriIntoThingConverter {

    private final ResourceProvider rp;
    private final XmlResourceParser parser = new XmlResourceParser();

    public XmlThingConverter(ResourceProvider rp) {
        this.rp = rp;
    }

    @Override
    public boolean convertInto(URI from, ThingRepositoryStore into) throws ConversionException {
        var resource = rp.getReadableResource(from);
        if (resource == null) return false;

        var id = TLC.optional(XmlThingContext.ID).orElse(resource.uri().toString());
        var ns = TLC.optional(XmlThingContext.NS).orElse(id);

        Thing.Builder<?> builder = into.getBuilder(id);
        // TODO addOrigin(from, builder);
        var handler = new XMLToThingHandler(ns, builder);
        var success = parser.convertInto(resource, handler);
        into.store(builder.build());
        return success;
    }
}

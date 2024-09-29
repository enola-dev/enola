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
package dev.enola.format.xml;

import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.io.resource.convert.CatchingResourceConverter;
import dev.enola.rdf.io.JavaThingsRdfWriterConverter;
import dev.enola.thing.repo.ThingsBuilder;

public record XmlResourceConverter(ResourceProvider rp) implements CatchingResourceConverter {

    @Override
    public boolean convertIntoThrows(ReadableResource from, WritableResource into)
            throws Exception {
        var xmlThingConverter = new XmlThingConverter(rp);
        var thingsWriterConverter = new JavaThingsRdfWriterConverter();

        var things = new ThingsBuilder();
        if (!xmlThingConverter.convertInto(from.uri(), things)) return false;
        return thingsWriterConverter.convertInto(things, into);
    }
}

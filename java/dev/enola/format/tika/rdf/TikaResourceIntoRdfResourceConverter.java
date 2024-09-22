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
package dev.enola.format.tika.rdf;

import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.io.resource.convert.CatchingResourceConverter;
import dev.enola.format.tika.TikaThingConverter;
import dev.enola.rdf.io.JavaThingsBuilderRdfConverter;
import dev.enola.rdf.io.WritableResourceRDFHandler;
import dev.enola.thing.repo.ThingsBuilder;

public class TikaResourceIntoRdfResourceConverter implements CatchingResourceConverter {

    private final TikaThingConverter tikaThingConverter;
    private final JavaThingsBuilderRdfConverter javaToRdfConverter;

    public TikaResourceIntoRdfResourceConverter(ResourceProvider rp) {
        this.tikaThingConverter = new TikaThingConverter(rp);
        this.javaToRdfConverter = new JavaThingsBuilderRdfConverter();
    }

    @Override
    public boolean convertIntoThrows(ReadableResource from, WritableResource into)
            throws Exception {

        ThingsBuilder thingsBuilder = new ThingsBuilder();
        if (!tikaThingConverter.convertInto(from, thingsBuilder)) return false;

        var rdfHandler = WritableResourceRDFHandler.create(into);
        if (rdfHandler.isEmpty()) return false;

        try (var closeableRDFHandler = rdfHandler.get()) {
            return javaToRdfConverter.convertInto(thingsBuilder, closeableRDFHandler);
        }
    }
}

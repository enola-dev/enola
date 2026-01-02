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
package dev.enola.rdf.io;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.ConverterInto;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.thing.repo.ThingRepository;

import java.io.IOException;

public class JavaThingsRdfWriterConverter
        implements ConverterInto<ThingRepository, WritableResource> {

    private final JavaThingRdfConverter javaToRdf = new JavaThingRdfConverter();

    @Override
    public boolean convertInto(ThingRepository from, WritableResource into)
            throws ConversionException, IOException {

        var rdfHandler = WritableResourceRDFHandler.create(into);
        if (rdfHandler.isEmpty()) return false;

        try (var closeableRDFHandler = rdfHandler.get()) {
            return javaToRdf.convertInto(from.stream(), closeableRDFHandler);
        }
    }
}

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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

import com.google.common.net.MediaType;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.resource.AppendableResource;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.thing.Thing;
import dev.enola.thing.io.ThingIntoAppendableConverter;

import java.io.IOException;
import java.util.stream.Stream;

public class JavaThingIntoRdfAppendableConverter implements ThingIntoAppendableConverter {

    private final MediaType rdfMediaType;
    private final JavaThingRdfConverter javaToRdf = new JavaThingRdfConverter();

    public JavaThingIntoRdfAppendableConverter(MediaType rdfMediaType) {
        this.rdfMediaType = rdfMediaType;
    }

    public JavaThingIntoRdfAppendableConverter() {
        this(RdfMediaTypes.TURTLE);
    }

    @Override
    public boolean convertInto(Thing from, Appendable into)
            throws ConversionException, IOException {

        WritableResource resource = new AppendableResource(into, rdfMediaType);
        var optRdfHandler = WritableResourceRDFHandler.create(resource);
        if (optRdfHandler.isEmpty()) throw new IllegalStateException(rdfMediaType.toString());
        try (var rdfHandler = optRdfHandler.get()) {
            return javaToRdf.convertInto(Stream.of(from), rdfHandler);
        }
    }
}

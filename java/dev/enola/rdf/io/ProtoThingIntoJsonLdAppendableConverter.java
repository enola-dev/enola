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
import dev.enola.common.convert.ConverterIntoAppendable;
import dev.enola.common.io.resource.AppendableResource;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.rdf.proto.ProtoThingRdfConverter;
import dev.enola.thing.proto.Thing;

import java.io.IOException;

public class ProtoThingIntoJsonLdAppendableConverter implements ConverterIntoAppendable<Thing> {

    private final ProtoThingRdfConverter thingRdfConverter = new ProtoThingRdfConverter();
    private final RdfWriterConverter rdfWriterConverter = new RdfWriterConverter();

    @Override
    public boolean convertInto(Thing from, Appendable into)
            throws ConversionException, IOException {

        var rdfModel = thingRdfConverter.convert(from);
        WritableResource resource = new AppendableResource(into, RdfMediaTypes.JSON_LD);
        rdfWriterConverter.convertIntoOrThrow(rdfModel, resource);
        return true;
    }
}

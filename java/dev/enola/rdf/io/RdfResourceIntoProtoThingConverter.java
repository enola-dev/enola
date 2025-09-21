/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.rdf.proto.RdfProtoThingsConverter;
import dev.enola.thing.proto.Thing;

import java.util.List;
import java.util.Optional;

public class RdfResourceIntoProtoThingConverter implements ResourceIntoProtoThingConverter {

    // TODO Also implement e.g. an MarkdownResourceIntoThingConverter
    // TODO Also implement e.g. JavaResourceIntoThingConverter

    private final RdfReaderConverter rdfReaderConverter;
    private final RdfProtoThingsConverter rdfThingConverter = new RdfProtoThingsConverter();

    public RdfResourceIntoProtoThingConverter(ResourceProvider rp) {
        this.rdfReaderConverter = new RdfReaderConverter(rp);
    }

    @Override
    public Optional<List<Thing.Builder>> convert(ReadableResource from) throws ConversionException {
        var optModel = rdfReaderConverter.convert(from);
        return optModel.map(rdfThingConverter::convertToList);
    }
}

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
package dev.enola.rdf;

import com.google.protobuf.Message;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.OptionalConverter;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.proto.Things;

import java.util.List;
import java.util.Optional;

public class RdfResourceIntoProtoThingConverter
        implements OptionalConverter<ReadableResource, List<Thing.Builder>> {

    // TODO Also implement e.g. an MarkdownResourceIntoThingConverter
    // TODO Also implement e.g. JavaResourceIntoThingConverter

    private final RdfReaderConverter rdfReaderConverter;
    private final RdfThingConverter rdfThingConverter = new RdfThingConverter();

    public RdfResourceIntoProtoThingConverter(ResourceProvider rp) {
        this.rdfReaderConverter = new RdfReaderConverter(rp);
    }

    @Override
    public Optional<List<Thing.Builder>> convert(ReadableResource from) throws ConversionException {
        var optModel = rdfReaderConverter.convert(from);
        if (optModel.isEmpty()) return Optional.empty();

        return Optional.of(rdfThingConverter.convertToList(optModel.get()));
    }

    /** This return thingsList as Thing (if there is 1) or a {@link Things} pb. */
    public Message.Builder asMessage(List<Thing.Builder> thingsList) {
        Message.Builder messageBuilder;
        if (thingsList.size() == 1) messageBuilder = thingsList.get(0);
        else {
            var thingsBuilder = Things.newBuilder();
            for (var thing : thingsList) {
                thingsBuilder.addThings(thing);
            }
            messageBuilder = thingsBuilder;
        }
        return messageBuilder;
    }
}

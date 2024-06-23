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

import com.google.common.collect.ImmutableList;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.thing.Thing;
import dev.enola.thing.Thing.Builder;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.io.ResourceIntoThingConverter;
import dev.enola.thing.message.ProtoThingIntoJavaThingBuilderConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class RdfResourceIntoThingConverter implements ResourceIntoThingConverter {

    private static final Logger LOG = LoggerFactory.getLogger(RdfResourceIntoThingConverter.class);

    private final RdfResourceIntoProtoThingConverter rdfResourceIntoProtoThingConverter =
            new RdfResourceIntoProtoThingConverter();

    private final ProtoThingIntoJavaThingBuilderConverter protoThingIntoJavaThingBuilderConverter;

    private final Supplier<Builder> builderSupplier;

    public RdfResourceIntoThingConverter(
            DatatypeRepository datatypeRepository, Supplier<Thing.Builder> builderSupplier) {
        this.protoThingIntoJavaThingBuilderConverter =
                new ProtoThingIntoJavaThingBuilderConverter(datatypeRepository);
        this.builderSupplier = builderSupplier;
    }

    public RdfResourceIntoThingConverter(DatatypeRepository datatypeRepository) {
        this(datatypeRepository, ImmutableThing::builder);
    }

    @Override
    public Optional<List<Builder>> convert(ReadableResource input) throws ConversionException {
        var optProtoList = rdfResourceIntoProtoThingConverter.convert(input);
        if (!optProtoList.isPresent()) return Optional.empty();

        var protoList = optProtoList.get();
        var listBuilder = ImmutableList.<Thing.Builder>builderWithExpectedSize(protoList.size());

        for (var protoThing : protoList) {
            try {
                var thingBuilder = builderSupplier.get();
                protoThingIntoJavaThingBuilderConverter.convertIntoOrThrow(
                        protoThing, thingBuilder);
                listBuilder.add(thingBuilder);
            } catch (ConversionException e) {
                LOG.error("Failed to convert Thing in: " + input.uri(), e);
            }
        }

        return Optional.of(listBuilder.build());
    }
}

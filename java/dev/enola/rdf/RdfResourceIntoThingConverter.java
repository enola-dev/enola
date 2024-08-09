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

import com.google.auto.service.AutoService;

import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.thing.Thing;
import dev.enola.thing.Thing.Builder;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.io.ResourceIntoThingConverter;
import dev.enola.thing.message.ProtoThingIntoJavaThingBuilderConverter;
import dev.enola.thing.repo.ThingsBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Supplier;

@SuppressWarnings("rawtypes")
@AutoService(ResourceIntoThingConverter.class)
public class RdfResourceIntoThingConverter<T extends Thing> implements ResourceIntoThingConverter {

    private static final Logger LOG = LoggerFactory.getLogger(RdfResourceIntoThingConverter.class);

    private final RdfResourceIntoProtoThingConverter rdfResourceIntoProtoThingConverter;

    private final ProtoThingIntoJavaThingBuilderConverter protoThingIntoJavaThingBuilderConverter;

    private final Supplier<Builder<T>> builderSupplier;

    private RdfResourceIntoThingConverter(
            ResourceProvider rp,
            DatatypeRepository datatypeRepository,
            Supplier<Thing.Builder<T>> builderSupplier) {
        this.rdfResourceIntoProtoThingConverter = new RdfResourceIntoProtoThingConverter(rp);
        this.protoThingIntoJavaThingBuilderConverter =
                new ProtoThingIntoJavaThingBuilderConverter(datatypeRepository);
        this.builderSupplier = builderSupplier;
    }

    @SuppressWarnings("unchecked")
    public RdfResourceIntoThingConverter(
            ResourceProvider rp, DatatypeRepository datatypeRepository) {
        // TODO Instead ImmutableThing.builder() it should look-up GenJavaThing subclass, if any
        this(rp, datatypeRepository, () -> (Builder<T>) ImmutableThing.builder());
    }

    public RdfResourceIntoThingConverter() {
        this(TLC.get(ResourceProvider.class), TLC.get(DatatypeRepository.class));
    }

    @Override
    public boolean convertInto(ReadableResource input, ThingsBuilder into)
            throws ConversionException, IOException {
        var optProtoList = rdfResourceIntoProtoThingConverter.convert(input);
        if (!optProtoList.isPresent()) return false;

        var protoList = optProtoList.get();

        for (var protoThing : protoList) {
            try {
                var thingBuilder = into.get(protoThing.getIri());
                protoThingIntoJavaThingBuilderConverter.convertIntoOrThrow(
                        protoThing, thingBuilder);
            } catch (ConversionException e) {
                LOG.error("Failed to convert Thing in: " + input.uri(), e);
            }
        }
        return true;
    }
}

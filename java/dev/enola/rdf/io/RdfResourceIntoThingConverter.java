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
package dev.enola.rdf.io;

import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.thing.Thing;
import dev.enola.thing.Thing.Builder;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.io.UriIntoThingConverter;
import dev.enola.thing.message.ProtoThingIntoJavaThingBuilderConverter;
import dev.enola.thing.repo.ThingsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.function.Supplier;

/**
 * RdfResourceIntoThingConverter "converts" (loads, really) RDF resources (e.g. *.ttl, et al.) into
 * Things.
 */
@SuppressWarnings("rawtypes")
public class RdfResourceIntoThingConverter<T extends Thing> implements UriIntoThingConverter {

    private final RdfResourceIntoProtoThingConverter rdfResourceIntoProtoThingConverter;

    private final ProtoThingIntoJavaThingBuilderConverter protoThingIntoJavaThingBuilderConverter;

    private final Supplier<Builder<T>> builderSupplier;
    private final ResourceProvider rp;

    private RdfResourceIntoThingConverter(
            ResourceProvider rp,
            DatatypeRepository datatypeRepository,
            Supplier<Thing.Builder<T>> builderSupplier) {
        this.rdfResourceIntoProtoThingConverter = new RdfResourceIntoProtoThingConverter(rp);
        this.protoThingIntoJavaThingBuilderConverter =
                new ProtoThingIntoJavaThingBuilderConverter(datatypeRepository);
        this.builderSupplier = builderSupplier;
        this.rp = rp;
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
    public boolean convertInto(URI uri, ThingsBuilder into)
            throws ConversionException, IOException {
        var resource = rp.getResource(uri);
        if (resource == null) return false;

        var optProtoList = rdfResourceIntoProtoThingConverter.convert(resource);
        if (!optProtoList.isPresent()) return false;

        var protoList = optProtoList.get();

        for (var protoThing : protoList) {
            var thingBuilder = into.getBuilder(protoThing.getIri());
            protoThingIntoJavaThingBuilderConverter.convertIntoOrThrow(protoThing, thingBuilder);
        }
        return true;
    }
}

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

import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.thing.Thing;
import dev.enola.thing.io.UriIntoThingConverter;
import dev.enola.thing.java.HasType;
import dev.enola.thing.message.ProtoThingIntoJavaThingBuilderConverter;
import dev.enola.thing.repo.ThingRepositoryStore;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;

/**
 * RdfResourceIntoThingConverter "converts" (loads, really) RDF resources (e.g. *.ttl, et al.) into
 * Things.
 */
@SuppressWarnings("rawtypes")
public class RdfResourceIntoThingConverter<T extends Thing> implements UriIntoThingConverter {

    private final RdfResourceIntoProtoThingConverter rdfResourceIntoProtoThingConverter;

    private final ProtoThingIntoJavaThingBuilderConverter protoThingIntoJavaThingBuilderConverter;

    private final ResourceProvider rp;

    public RdfResourceIntoThingConverter(
            ResourceProvider rp, DatatypeRepository datatypeRepository) {
        this.rdfResourceIntoProtoThingConverter = new RdfResourceIntoProtoThingConverter(rp);
        this.protoThingIntoJavaThingBuilderConverter =
                new ProtoThingIntoJavaThingBuilderConverter(datatypeRepository);
        this.rp = rp;
    }

    public RdfResourceIntoThingConverter() {
        this(TLC.get(ResourceProvider.class), TLC.get(DatatypeRepository.class));
    }

    @Override
    public boolean convertInto(URI from, ThingRepositoryStore into)
            throws ConversionException, IOException {
        var resource = rp.getResource(from);
        if (resource == null) return false;

        var optProtoList = rdfResourceIntoProtoThingConverter.convert(resource);
        if (!optProtoList.isPresent()) return false;

        var protoList = optProtoList.get();

        for (var protoThing : protoList) {
            Thing.Builder<?> thingBuilder;
            var thingIRI = protoThing.getIri();
            var typeIRI = typeIRI(protoThing);
            if (typeIRI != null) thingBuilder = into.getBuilder(thingIRI, typeIRI);
            else thingBuilder = into.getBuilder(thingIRI);
            protoThingIntoJavaThingBuilderConverter.convertIntoOrThrow(protoThing, thingBuilder);
            addOrigin(from, thingBuilder);
            into.store(thingBuilder.build());
        }
        return true;
    }

    private @Nullable String typeIRI(dev.enola.thing.proto.Thing.Builder protoThing) {
        var value = protoThing.getPropertiesMap().get(HasType.IRI);
        if (value == null) return null;
        if (value.hasLink()) return value.getLink();
        // TODO Support Proxy of Java interfaces for more than 1 @type:
        if (value.hasList()) return null;
        // TODO Handle other weird cases? Or also ignore by returning null?
        throw new IllegalArgumentException(protoThing.toString());
    }
}

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.core.rosetta;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Descriptors.Descriptor;

import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.io.resource.convert.CharResourceConverter;
import dev.enola.common.io.resource.convert.ResourceConverter;
import dev.enola.common.io.resource.convert.ResourceConverterChain;
import dev.enola.common.protobuf.DescriptorProvider;
import dev.enola.common.protobuf.MessageResourceConverter;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.common.protobuf.YamlJsonResourceConverter;
import dev.enola.data.iri.NamespaceConverter;
import dev.enola.format.tika.rdf.TikaResourceIntoRdfResourceConverter;
import dev.enola.format.xml.XmlResourceConverter;
import dev.enola.rdf.io.RdfResourceConverter;
import dev.enola.thing.gen.gexf.GexfGenerator;
import dev.enola.thing.gen.gexf.GexfResourceConverter;
import dev.enola.thing.gen.graphcommons.GraphCommonsJsonGenerator;
import dev.enola.thing.gen.graphcommons.GraphCommonsResourceConverter;
import dev.enola.thing.gen.graphviz.GraphvizGenerator;
import dev.enola.thing.gen.graphviz.GraphvizResourceConverter;
import dev.enola.thing.io.Loader;
import dev.enola.thing.metadata.ThingMetadataProvider;
import dev.enola.thing.repo.ThingProvider;

import java.io.IOException;

/**
 * <a href="https://en.wikipedia.org/wiki/Rosetta_Stone">Rosetta Stone</a> for converting between
 * different model resource and other formats.
 */
public class Rosetta implements ResourceConverter {

    // TODO Merge this with Canonicalizer (and move into dev.enola.common.canonicalize)

    // This class orig. was in dev.enola.core.rosetta originally, in order to have classpath access
    // to Entities.getDescriptor() and EntityKinds.getDescriptor() in the lookupDescriptor() below.
    // But all of this has meanwhile been removed, so this could now moved e.g. to a new
    // dev.enola[.common?].rosetta instead, and use a DescriptorProvider as generic proto Descriptor
    // registry. TODO Move dev.enola.core.rosetta to dev.enola.action.rosetta?

    private final ProtoIO protoIO = new ProtoIO();

    private static final DescriptorProvider DESCRIPTOR_PROVIDER =
            new DescriptorProvider() {

                @Override
                public Descriptor findByName(String messageTypeURL) {
                    throw new IllegalArgumentException(
                            "TODO Cannot find Descriptor for: " + messageTypeURL);
                }

                @Override
                public Descriptor getDescriptorForTypeUrl(String protoMessageFullyQualifiedName) {
                    throw new UnsupportedOperationException("Unimplemented method 'findByName'");
                }
            };

    private final MessageResourceConverter messageResourceConverter =
            new MessageResourceConverter(protoIO, DESCRIPTOR_PROVIDER);

    private final ResourceConverterChain resourceConverterChain;
    private final ResourceProvider rp;

    public Rosetta(ResourceProvider rp, Loader loader) {
        this.rp = rp;
        // TODO Remove!
        var tmp = new ThingMetadataProvider(ThingProvider.CTX, NamespaceConverter.CTX);
        this.resourceConverterChain =
                new ResourceConverterChain(
                        ImmutableList.of(
                                // TODO Use ServiceLoader with @AutoService
                                new RdfResourceIntoProtoThingResourceConverter(rp),
                                new RdfResourceConverter(rp),
                                new TikaResourceIntoRdfResourceConverter(rp),
                                messageResourceConverter,
                                new YamlJsonResourceConverter(),
                                new GraphvizResourceConverter(loader, new GraphvizGenerator(tmp)),
                                new GexfResourceConverter(loader, new GexfGenerator(tmp)),
                                new GraphCommonsResourceConverter(
                                        loader, new GraphCommonsJsonGenerator(tmp)),
                                new XmlResourceConverter(rp),
                                new CharResourceConverter()));
        // NOT new IdempotentCopyingResourceNonConverter()
    }

    @Override
    public boolean convertInto(ReadableResource from, WritableResource into)
            throws ConversionException, IOException {
        try (var ctx = TLC.open()) {
            ctx.push(ResourceProvider.class, rp);
            if (!resourceConverterChain.convertInto(from, into)) {
                throw new ConversionException(
                        "No Converter (registered on the Chain) accepted to transform from "
                                + from
                                + " into "
                                + into);
            }
            return true;
        }
    }
}

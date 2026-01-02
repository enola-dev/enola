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
import dev.enola.rdf.proto.AbstractModelConverter;
import dev.enola.rdf.proto.ProtoThingRdfConverter;
import dev.enola.thing.Thing;
import dev.enola.thing.message.JavaThingToProtoThingConverter;

import org.eclipse.rdf4j.rio.RDFHandler;

import java.io.IOException;
import java.util.stream.Stream;

public class JavaThingRdfConverter
        implements AbstractModelConverter<Stream<Thing>>, ConverterInto<Stream<Thing>, RDFHandler> {

    // TODO Implement this more directly, instead of indirect via Proto; with JavaThingRdfConverter?

    private final JavaThingToProtoThingConverter javaToProto = new JavaThingToProtoThingConverter();
    private final ProtoThingRdfConverter protoToRdf = new ProtoThingRdfConverter();

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean convertInto(Stream<Thing> from, RDFHandler into)
            throws ConversionException, IOException {

        from.forEach(
                thing -> {
                    var protoBuilder = javaToProto.convert(thing);
                    protoToRdf.convertInto(protoBuilder, into);
                });

        return true;
    }
}

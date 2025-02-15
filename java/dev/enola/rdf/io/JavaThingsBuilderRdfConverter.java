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

import com.google.common.collect.Iterables;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.ConverterInto;
import dev.enola.rdf.proto.AbstractModelConverter;
import dev.enola.thing.Thing;
import dev.enola.thing.repo.ThingsBuilders;

import org.eclipse.rdf4j.rio.RDFHandler;

import java.io.IOException;
import java.util.ArrayList;

public class JavaThingsBuilderRdfConverter
        implements AbstractModelConverter<ThingsBuilders>,
                ConverterInto<ThingsBuilders, RDFHandler> {

    private final JavaThingRdfConverter javaToRdf = new JavaThingRdfConverter();

    @Override
    public boolean convertInto(ThingsBuilders from, RDFHandler into)
            throws ConversionException, IOException {

        // TODO Is there a more efficient way to do this? Stream vs Iterable is a mess...
        var list = new ArrayList<Thing>(Iterables.size(from.builders()));
        for (var builder : from.builders()) {
            var thing = builder.build();
            list.add(thing);
        }
        return javaToRdf.convertInto(list.stream(), into);
    }
}

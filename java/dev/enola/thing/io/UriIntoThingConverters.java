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
package dev.enola.thing.io;

import static dev.enola.thing.io.UriIntoThingConverters.ContextKey.INPUT;

import com.google.common.collect.ImmutableList;

import dev.enola.common.context.Context;
import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.Converter;
import dev.enola.thing.KIRI;
import dev.enola.thing.Thing;
import dev.enola.thing.repo.ThingsBuilder;

import java.io.IOException;
import java.net.URI;

// TODO Make UriIntoThingConverters actually implement UriIntoThingConverter itself (less confusing)
public class UriIntoThingConverters implements Converter<URI, Iterable<Thing.Builder<?>>> {

    // TODO Load a resource with different converters multi-threaded, in parallel...

    private final ImmutableList<UriIntoThingConverter> converters;

    public UriIntoThingConverters(Iterable<UriIntoThingConverter> converters) {
        this.converters = ImmutableList.copyOf(converters);
    }

    public UriIntoThingConverters(UriIntoThingConverter... converters) {
        this.converters = ImmutableList.copyOf(converters);
    }

    public Iterable<Thing.Builder<?>> convert(URI input) throws ConversionException {
        ThingsBuilder thingsBuilder = new ThingsBuilder();
        try (var ctx = TLC.open()) {
            ctx.push(INPUT, input);
            for (var converter : converters) {
                converter.convertInto(input, thingsBuilder);
            }
            // TODO Make this configurable? (At least until sparql: query is supported)
            // This is "cool", but "very ugly and overwhelming" e.g. on graph visualizations...
            for (var builder : thingsBuilder.builders()) builder.set(KIRI.E.ORIGIN, input);
            return thingsBuilder.builders();
        } catch (IOException e) {
            throw new ConversionException("IOException on " + input, e);
        }
    }

    // TODO Actually TLC.get(INPUT) *read* this somewhere... ;-) else remove again later.
    enum ContextKey implements Context.Key<URI> {
        INPUT
    }
}

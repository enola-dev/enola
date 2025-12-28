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
package dev.enola.thing.io;

import static dev.enola.thing.io.UriIntoThingConverters.ContextKey.INPUT;

import com.google.common.collect.ImmutableList;

import dev.enola.common.context.Context;
import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.ConverterInto;
import dev.enola.thing.repo.ThingRepositoryStore;

import java.io.IOException;
import java.net.URI;

// TODO Make UriIntoThingConverters actually implement UriIntoThingConverter itself (less confusing)
public class UriIntoThingConverters implements ConverterInto<URI, ThingRepositoryStore> {

    // TODO Load a resource with different converters multi-threaded, in parallel...

    public enum Flags implements Context.Key<Boolean> {
        ORIGIN
    }

    private final ImmutableList<UriIntoThingConverter> converters;

    public UriIntoThingConverters(Iterable<UriIntoThingConverter> converters) {
        this.converters = ImmutableList.copyOf(converters);
    }

    public UriIntoThingConverters(UriIntoThingConverter... converters) {
        this.converters = ImmutableList.copyOf(converters);
    }

    @Override
    public boolean convertInto(URI from, ThingRepositoryStore into)
            throws ConversionException, IOException {
        try (var ctx = TLC.open()) {
            ctx.push(INPUT, from);
            for (var converter : converters) {
                if (converter.convertInto(from, into)) return true;
            }
        }
        return false;
    }

    // TODO Actually TLC.get(INPUT) *read* this somewhere... ;-) else remove again later.
    enum ContextKey implements Context.Key<URI> {
        INPUT
    }
}

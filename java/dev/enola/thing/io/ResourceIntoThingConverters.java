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

import static dev.enola.thing.io.ResourceIntoThingConverters.ContextKey.INPUT;

import com.google.common.collect.ImmutableList;

import dev.enola.common.context.Context;
import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.Converter;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.thing.Thing;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Stream;

public class ResourceIntoThingConverters<T extends Thing>
        implements Converter<ReadableResource, List<Thing.Builder<T>>> {

    private final ImmutableList<ResourceIntoThingConverter<T>> converters;

    public ResourceIntoThingConverters(Iterable<ResourceIntoThingConverter<T>> converters) {
        this.converters = ImmutableList.copyOf(converters);
    }

    public ResourceIntoThingConverters(ResourceIntoThingConverter... converters) {
        this(ImmutableList.copyOf(converters));
    }

    public ResourceIntoThingConverters() {
        this(ServiceLoader.load(ResourceIntoThingConverter.class).stream());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private ResourceIntoThingConverters(
            Stream<ServiceLoader.Provider<ResourceIntoThingConverter>> providers) {
        this(providers.map(p -> (ResourceIntoThingConverter<T>) p.get()).toList());
    }

    public List<Thing.Builder<T>> convert(ReadableResource input) throws ConversionException {
        try (var ctx = TLC.open()) {
            ctx.push(INPUT, input);
            for (var converter : converters) {
                var opt = converter.convert(input);
                // TODO Don't return after the 1st one, but run all, and merge
                if (opt.isPresent()) return opt.get();
            }
        }
        throw new ConversionException(
                "None of the registered Thing converters could read: " + input);
    }

    // TODO Actually TLC.get(INPUT) *read* this somewhere... ;-) else remove again later.
    enum ContextKey implements Context.Key<ReadableResource> {
        INPUT
    }
}

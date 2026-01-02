/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.data.id;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableList;

import dev.enola.common.convert.ConversionException;

import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.Optional;

/**
 * IdConverterChain is an {@link IdConverter} which delegates to a list of other such converters.
 */
public class IdConverterChain implements IdConverter<Object> {

    private final ImmutableList<IdConverter<?>> converters;
    private final ImmutableClassToInstanceMap<IdConverter<?>> convertersMap;

    public IdConverterChain(IdConverter<?>... converters) {
        this(ImmutableList.copyOf(converters));
    }

    public IdConverterChain(Iterable<IdConverter<?>> converters) {
        this.converters = ImmutableList.copyOf(converters);
        this.convertersMap = map(converters);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static ImmutableClassToInstanceMap<IdConverter<?>> map(
            Iterable<IdConverter<?>> converters) {
        var convertersMapBuilder = ImmutableClassToInstanceMap.<IdConverter<?>>builder();
        for (IdConverter converter : converters)
            convertersMapBuilder.put(converter.idClass(), converter);
        return convertersMapBuilder.build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean convertInto(Object from, Appendable into)
            throws ConversionException, IOException {
        Class<?> idClass = from.getClass();
        IdConverter<Object> converter = (IdConverter<Object>) convertersMap.get(idClass);
        if (converter == null)
            throw new IllegalStateException("No IdConverter registered for " + idClass);
        return converter.convertInto(from, into);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull String convertTo(@NonNull Object input) throws ConversionException {
        Class<?> idClass = input.getClass();
        IdConverter<Object> converter = (IdConverter<Object>) convertersMap.get(idClass);
        if (converter == null)
            throw new IllegalStateException("No IdConverter registered for " + idClass);
        return converter.convertTo(input);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Object> convert(String input) throws ConversionException {
        for (IdConverter<?> converter : converters) {
            var opt = converter.convert(input);
            if (opt.isPresent()) return (Optional<Object>) opt;
        }
        return Optional.empty();
    }

    @Override
    public Class<Object> idClass() {
        return Object.class;
    }
}

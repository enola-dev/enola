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
package dev.enola.common.convert;

import static java.util.Objects.requireNonNull;

import com.google.errorprone.annotations.Immutable;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

/**
 * An ObjectToStringBiConverter which uses {@link Object#toString()} for {@link
 * ObjectToStringBiConverter#convertTo(Object)}, and which also checks the type to convert from.
 */
@Immutable
public class ObjectToStringWithToStringBiConverter<T> implements ObjectToStringBiConverter<T> {

    // See also ObjectConverter, which is similar to this, but the "opposite" (can't unify them)

    private final Class<T> from;

    @SuppressWarnings("Immutable")
    private final Function<String, T> converter;

    public ObjectToStringWithToStringBiConverter(Class<T> clazz, Function<String, T> function) {
        this.from = requireNonNull(clazz);
        this.converter = function;
    }

    @Override
    public <X> Optional<X> convertToType(T input, Class<X> type) throws IOException {
        if (input != null && from.equals(input.getClass()))
            return ObjectToStringBiConverter.super.convertToType(input, type);
        else return Optional.empty();
    }

    @Override
    public final String convertTo(@Nullable T input) throws ConversionException {
        return input != null ? input.toString() : null;
    }

    @Override
    public @Nullable T convertFrom(@Nullable String input) throws ConversionException {
        return input != null ? converter.apply(input) : null;
    }
}

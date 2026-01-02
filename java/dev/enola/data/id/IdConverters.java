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
package dev.enola.data.id;

import static java.util.regex.Pattern.compile;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.ObjectToStringBiConverter;
import dev.enola.common.convert.ObjectToStringBiConverters;

import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * IdConverters offers a list of built-in {@link IdConverter}s for well-known standard ID types.
 *
 * <p>This is by no means exclusive.
 */
public final class IdConverters {

    public static final IdConverter<String> STRING =
            from(compile(".+"), ObjectToStringBiConverters.STRING, String.class);

    public static final IdConverter<URI> URI =
            from(
                    compile("[a-zA-Z][a-zA-Z0-9+-.]*://.+"),
                    ObjectToStringBiConverters.URI,
                    URI.class);

    private static <T> IdConverter<T> from(
            Pattern pattern, ObjectToStringBiConverter<T> converter, Class<T> klass) {
        return new IdConverter<T>() {

            @Override
            public @NonNull String convertTo(@NonNull T input) throws ConversionException {
                var nullableString = converter.convertTo(input);
                if (nullableString == null) throw new IllegalArgumentException(input.toString());
                return nullableString;
            }

            @Override
            public Optional<T> convert(String input) throws ConversionException {
                if (!pattern.matcher(input).matches()) return Optional.empty();
                var nullableT = converter.convertFrom(input);
                if (nullableT == null) throw new IllegalArgumentException(input);
                return Optional.of(nullableT);
            }

            @Override
            public Class<T> idClass() {
                return klass;
            }
        };
    }

    private IdConverters() {}
}

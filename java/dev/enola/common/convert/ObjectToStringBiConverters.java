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
package dev.enola.common.convert;

import org.jspecify.annotations.Nullable;

import java.net.URISyntaxException;
import java.time.LocalDate;

public final class ObjectToStringBiConverters {

    private static class IdentityObjectToStringBiConverter
            implements ObjectToStringBiConverter<String>, IdentityConverter<String> {}

    public static final ObjectToStringBiConverter<String> STRING =
            new IdentityObjectToStringBiConverter();

    // public static final ObjectToStringBiConverter<Boolean> BOOLEAN =
    //        new TypeCheckingObjectStringWithToStringBiConverter<>(
    //                Boolean.class, input -> input != null ? Boolean.valueOf(input) : null);

    public static final ObjectToStringBiConverter<Boolean> BOOLEAN =
            new ObjectToStringToStringBiConverter<>() {

                @Override
                public Boolean convertFrom(@Nullable String input) throws ConversionException {
                    return input != null ? Boolean.valueOf(input) : null;
                }
            };

    public static final ObjectToStringBiConverter<java.net.URI> URI =
            new ObjectToStringToStringBiConverter<>() {

                @Override
                public java.net.URI convertFrom(@Nullable String input) throws ConversionException {
                    try {
                        return input != null ? new java.net.URI(input) : null;
                    } catch (URISyntaxException e) {
                        throw new ConversionException(input, e);
                    }
                }
            };

    public static final ObjectToStringBiConverter<LocalDate> LOCAL_DATE =
            new ObjectToStringToStringBiConverter<>() {
                @Override
                public LocalDate convertFrom(@Nullable String input) throws ConversionException {
                    return input != null ? LocalDate.parse(input) : null;
                }
            };

    /**
     * An ObjectToStringBiConverter which uses {@link Object#toString()} for {@link
     * ObjectToStringBiConverter#convertTo(Object)}, and which also checks the type to convert from.
     */
    /*
        private static class TypeCheckingObjectStringWithToStringBiConverter<T>
                extends ObjectConverter<String, T> implements ObjectToStringBiConverter<T> {

            public TypeCheckingObjectStringWithToStringBiConverter(
                    Class<T> to, Function<String, T> converter) {
                super(String.class, to, converter);
            }

            @Override
            public @Nullable String convertTo(@Nullable T input) throws ConversionException {
                return input != null ? input.toString() : null;
            }

            @Override
            public @Nullable T convertFrom(@Nullable String input) throws ConversionException {
                try {
                    return convertFromType(input, to).orElse(null);
                } catch (IOException e) {
                    throw new ConversionException("Failed to convert: " + input, e);
                }
            }

            // TODO @Override
            public <T> Optional<T> convertFromType(String input, Class<T> type) throws IOException {
                if (to.equals(type) && input != null && from.equals(input.getClass())) {
                    return Optional.of((T) converter.apply((String) input));
                }
                return Optional.empty();
            }
        }
    */

    /**
     * An ObjectToStringBiConverter which uses {@link Object#toString()} for {@link
     * ObjectToStringBiConverter#convertTo(Object)}.
     */
    private abstract static class ObjectToStringToStringBiConverter<T>
            implements ObjectToStringBiConverter<T> {
        @Override
        public final String convertTo(@Nullable T input) throws ConversionException {
            return input != null ? input.toString() : null;
        }
    }

    private ObjectToStringBiConverters() {}
}

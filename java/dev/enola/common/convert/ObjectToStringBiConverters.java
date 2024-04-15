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

import java.net.URISyntaxException;
import java.time.LocalDate;

public final class ObjectToStringBiConverters {

    private static class IdentityObjectToStringBiConverter
            implements ObjectToStringBiConverter<String>, IdentityConverter<String> {}

    public static final ObjectToStringBiConverter<String> STRING =
            new IdentityObjectToStringBiConverter();

    public static final ObjectToStringBiConverter<Boolean> BOOLEAN =
            new ObjectToStringToStringBiConverter<Boolean>() {

                @Override
                public Boolean convertFrom(String input) throws ConversionException {
                    return Boolean.valueOf(input);
                }
            };

    public static final ObjectToStringBiConverter<java.net.URI> URI =
            new ObjectToStringToStringBiConverter<java.net.URI>() {

                @Override
                public java.net.URI convertFrom(String input) throws ConversionException {
                    try {
                        return new java.net.URI(input);
                    } catch (URISyntaxException e) {
                        throw new ConversionException(input, e);
                    }
                }
            };

    public static final ObjectToStringBiConverter<LocalDate> LOCAL_DATE =
            new ObjectToStringBiConverter<LocalDate>() {

                @Override
                public String convertTo(LocalDate input) throws ConversionException {
                    return input.toString();
                }

                @Override
                public LocalDate convertFrom(String input) throws ConversionException {
                    return LocalDate.parse(input);
                }
            };

    /**
     * An ObjectToStringBiConverter which uses {@link Object#toString()} for {@link
     * ObjectToStringBiConverter#convertTo(Object)}.
     */
    private abstract static class ObjectToStringToStringBiConverter<T>
            implements ObjectToStringBiConverter<T> {
        @Override
        public String convertTo(T input) throws ConversionException {
            return input.toString();
        }
    }

    private ObjectToStringBiConverters() {}
}

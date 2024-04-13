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

public final class PrimitiveObjectToStringBiConverters {
    // TODO Move into sub-package dev.enola.common.convert.string
    // TODO Rename misnamed Primitive* to ... Builtin*

    private static class IdentityObjectToStringBiConverter
            implements ObjectToStringBiConverter<String>, IdentityConverter<String> {}

    public static final ObjectToStringBiConverter<String> STRING =
            new IdentityObjectToStringBiConverter();

    public static final ObjectToStringBiConverter<Boolean> BOOLEAN =
            new PrimitiveObjectToStringBiConverter<Boolean>() {

                @Override
                public Boolean convertFrom(String input) throws ConversionException {
                    return Boolean.valueOf(input);
                }
            };

    public static final ObjectToStringBiConverter<java.net.URI> URI =
            new PrimitiveObjectToStringBiConverter<java.net.URI>() {

                @Override
                public java.net.URI convertFrom(String input) throws ConversionException {
                    try {
                        return new java.net.URI(input);
                    } catch (URISyntaxException e) {
                        throw new ConversionException(input, e);
                    }
                }
            };

    // TODO Rename misnamed Primitive* to ... Builtin*
    private abstract static class PrimitiveObjectToStringBiConverter<T>
            implements ObjectToStringBiConverter<T> {
        @Override
        public String convertTo(T input) throws ConversionException {
            return input.toString();
        }
    }

    private PrimitiveObjectToStringBiConverters() {}
}

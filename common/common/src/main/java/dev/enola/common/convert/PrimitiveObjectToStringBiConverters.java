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

public final class PrimitiveObjectToStringBiConverters {

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

    private abstract static class PrimitiveObjectToStringBiConverter<T>
            implements ObjectToStringBiConverter<T> {
        @Override
        public String convertTo(T input) throws ConversionException {
            return input.toString();
        }
    }

    private PrimitiveObjectToStringBiConverters() {}
}

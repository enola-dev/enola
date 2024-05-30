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
import java.time.Instant;
import java.time.LocalDate;

public final class ObjectToStringBiConverters {

    public static final ObjectToStringBiConverter<String> STRING =
            new ObjectToStringWithToStringBiConverter<>(String.class, input -> input);

    public static final ObjectToStringBiConverter<Boolean> BOOLEAN =
            new ObjectToStringWithToStringBiConverter<>(
                    Boolean.class, input -> Boolean.valueOf(input));

    public static final ObjectToStringBiConverter<Integer> INT =
            new ObjectToStringWithToStringBiConverter<>(
                    Integer.class, input -> Integer.valueOf(input));

    public static final ObjectToStringBiConverter<java.net.URI> URI =
            new ObjectToStringWithToStringBiConverter<>(
                    java.net.URI.class,
                    input -> {
                        try {
                            return new java.net.URI(input);
                        } catch (URISyntaxException e) {
                            throw new ConversionException(input, e);
                        }
                    });

    public static final ObjectToStringBiConverter<LocalDate> LOCAL_DATE =
            new ObjectToStringWithToStringBiConverter<>(
                    LocalDate.class, input -> LocalDate.parse(input));

    public static final ObjectToStringBiConverter<Instant> INSTANT =
            new ObjectToStringWithToStringBiConverter<>(
                    Instant.class, input -> Instant.parse(input));

    private ObjectToStringBiConverters() {}
}

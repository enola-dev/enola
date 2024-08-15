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

import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

public final class ObjectToStringBiConverters {

    public static final ObjectToStringBiConverter<String> STRING =
            new ObjectToStringWithToStringBiConverter<>(String.class, input -> input);

    public static final ObjectToStringBiConverter<Boolean> BOOLEAN =
            new ObjectToStringWithToStringBiConverter<>(
                    Boolean.class, input -> Boolean.valueOf(input));

    public static final ObjectToStringBiConverter<Byte> BYTE =
            new ObjectToStringWithToStringBiConverter<>(Byte.class, input -> Byte.valueOf(input));

    public static final ObjectToStringBiConverter<Short> SHORT =
            new ObjectToStringWithToStringBiConverter<>(Short.class, input -> Short.valueOf(input));

    // TODO UNSIGNED_BYTE

    public static final ObjectToStringBiConverter<Short> UNSIGNED_SHORT =
            new ObjectToStringBiConverter<>() {
                @Override
                public @Nullable String convertTo(@Nullable Short input)
                        throws ConversionException {
                    return Integer.toString(Short.toUnsignedInt(input));
                }

                @Override
                public @Nullable Short convertFrom(@Nullable String input)
                        throws ConversionException {
                    throw new IllegalStateException("TODO: Not implemented yet, add tests!");
                }
            };

    public static final ObjectToStringBiConverter<Integer> INT =
            new ObjectToStringWithToStringBiConverter<>(
                    Integer.class, input -> Integer.valueOf(input));

    public static final ObjectToStringBiConverter<UnsignedInteger> UNSIGNED_INTEGER =
            new ObjectToStringWithToStringBiConverter<>(
                    UnsignedInteger.class, input -> UnsignedInteger.valueOf(input));

    public static final ObjectToStringBiConverter<UnsignedLong> UNSIGNED_LONG =
            new ObjectToStringWithToStringBiConverter<>(
                    UnsignedLong.class, input -> UnsignedLong.valueOf(input));

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

    public static final ObjectToStringBiConverter<FileTime> FILE_TIME =
            new ObjectToStringWithToStringBiConverter<>(
                    // TODO According to FileTime#Instant(), it "can store points on the time-line
                    // further in the future and further in the past than Instant" - so no good?!
                    FileTime.class, input -> FileTime.from(Instant.parse(input))) {
                @Override
                @SuppressWarnings("unchecked")
                public <X> Optional<X> convertToType(FileTime input, Class<X> type)
                        throws IOException {
                    if (Instant.class.equals(type))
                        return (Optional<X>) Optional.of(input.toInstant());
                    return super.convertToType(input, type);
                }
            };

    private ObjectToStringBiConverters() {}
}

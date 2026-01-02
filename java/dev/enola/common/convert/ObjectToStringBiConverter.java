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

import com.google.errorprone.annotations.Immutable;

import java.io.IOException;
import java.util.Optional;

/**
 * Converts objects of type T to &amp; from String, if it can.
 *
 * @param <T> the type of objects to convert
 */
@Immutable
public interface ObjectToStringBiConverter<T>
        extends BiConverter<T, String>, ConverterIntoAppendable<T>, ObjectClassConverter<T> {

    @Override
    default boolean convertInto(T from, Appendable into) throws ConversionException, IOException {
        into.append(this.convertTo(from));
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    // TODO Remove throws IOException again (together with rm from super type)
    default <X> Optional<X> convertToType(T input, Class<X> type) throws IOException {
        // See also ObjectConverter's & other similar convertToType() implementations
        // TODO Re-consider class.equals -VS- isAssignableFrom, here & in ObjectConverter
        if (input != null && String.class.equals(type))
            return (Optional<X>) Optional.of(convertTo(input));
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    default <X> Optional<X> convertObjectToType(Object input, Class<X> type) throws IOException {
        return convertToType((T) input, type);
    }
}

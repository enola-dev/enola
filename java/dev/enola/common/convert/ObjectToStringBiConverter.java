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

import java.io.IOException;
import java.util.Optional;

public interface ObjectToStringBiConverter<T>
        extends BiConverter<T, String>, ConverterIntoAppendable<T>, ObjectClassConverter {

    @Override
    default boolean convertInto(T from, Appendable into) throws ConversionException, IOException {
        into.append(this.convertTo(from));
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    default <X> Optional<X> convertToType(Object input, Class<X> type) {
        if (String.class.equals(type)) return (Optional<X>) Optional.of(convertTo((T) input));
        return Optional.empty();
    }
}

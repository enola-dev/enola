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

import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.ThreadSafe;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.ObjectToStringBiConverter;
import dev.enola.common.convert.OptionalConverter;

import org.jspecify.annotations.NonNull;

@Immutable
@ThreadSafe
/** IdConverter converts {@link ID}s (I) to and from {@link String}. */
public interface IdConverter<I> extends ObjectToStringBiConverter<I>, OptionalConverter<String, I> {

    @Override
    @NonNull String convertTo(@NonNull I input) throws ConversionException;

    @Override
    default @NonNull I convertFrom(@NonNull String input) throws ConversionException {
        var opt = convert(input);
        if (opt.isPresent()) return opt.get();
        throw new ConversionException(input);
    }

    Class<I> idClass();
}

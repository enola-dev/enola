/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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

public interface OptionalBiConverterIntoAppendable<I>
        extends ConverterIntoAppendable<I>, OptionalConverter<String, I>, BiConverter<I, String> {

    @Override
    default String convertTo(I input) throws ConversionException {
        var sb = new StringBuilder();
        convertIntoOrThrow(input, sb);
        return sb.toString();
    }

    @Override
    default I convertFrom(String input) throws ConversionException {
        // If you are encountering this, you might want to use Optional<I> convert() instead?
        return convert(input).orElseThrow(() -> new ConversionException(input));
    }
}

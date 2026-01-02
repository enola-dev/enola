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

import java.io.IOException;

/**
 * A "chain" of {@link ConverterInto}s. It attempts the conversion in order until one is successful.
 *
 * @param <I> the type of input objects to convert from
 * @param <O> the type of output objects to convert into
 */
public class ConverterIntoChain<I, O> implements ConverterInto<I, O> {

    private final Iterable<ConverterInto<I, O>> converters;

    public ConverterIntoChain(Iterable<ConverterInto<I, O>> converters) {
        this.converters = converters;
    }

    @Override
    public boolean convertInto(I from, O into) throws ConversionException, IOException {
        for (var converter : converters) {
            if (converter.convertInto(from, into)) return true;
        }
        return false;
    }
}

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

import java.util.Optional;

/**
 * A "chain" of {@link OptionalConverter}s. It attempts the conversion in order until one is
 * successful.
 */
public class OptionalConverterChain<I, O> implements OptionalConverter<I, O> {

    private final Iterable<OptionalConverter<I, O>> converters;

    public OptionalConverterChain(Iterable<OptionalConverter<I, O>> converters) {
        this.converters = converters;
    }

    @Override
    public Optional<O> convert(I input) throws ConversionException {
        for (var converter : converters) {
            var opt = converter.convert(input);
            if (opt.isPresent()) return opt;
        }
        return Optional.empty();
    }
}

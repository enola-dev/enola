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

import com.google.common.collect.ImmutableList;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;

public class ObjectClassConverterChain implements ObjectClassConverter<Object> {

    private final Iterable<ObjectClassConverter> converters;

    public ObjectClassConverterChain(Iterable<ObjectClassConverter> converters) {
        this.converters = converters;
    }

    public ObjectClassConverterChain(ObjectClassConverter... converters) {
        this(ImmutableList.copyOf(converters));
    }

    @Override
    public <T> Optional<T> convertToType(@Nullable Object input, Class<T> type) throws IOException {
        if (input == null) return Optional.empty();
        for (ObjectClassConverter converter : converters) {
            Optional<T> converted = converter.convertToType(input, type);
            if (converted.isPresent()) {
                return converted;
            }
        }
        return Optional.empty();
    }
}

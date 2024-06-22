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
import java.util.function.Function;

public class ObjectConverter<X, Y> implements ObjectClassConverter {

    // See also similar ObjectToStringWithToStringBiConverter

    private final Class<Y> to;
    private final Class<X> from;
    protected Function<X, Y> converter;

    public ObjectConverter(Class<X> from, Class<Y> to, Function<X, Y> converter) {
        this.to = to;
        this.from = from;
        this.converter = converter;
    }

    @Override
    public <T> Optional<T> convertToType(Object input, Class<T> type) throws IOException {
        // See also ObjectToStringBiConverter's & other similar convertToType() implementations
        // TODO Re-consider class.equals -VS- isAssignableFrom, here & in ObjectToStringBiConverter
        if (to.equals(type) && input != null && from.equals(input.getClass())) {
            return Optional.of((T) converter.apply((X) input));
        }
        return Optional.empty();
    }
}

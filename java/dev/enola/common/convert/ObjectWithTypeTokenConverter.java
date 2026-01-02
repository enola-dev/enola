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

import com.google.common.reflect.TypeToken;

import dev.enola.common.ImmutableObjectWithTypeToken;
import dev.enola.common.MutableObjectWithTypeToken;
import dev.enola.common.ObjectWithTypeToken;

import java.io.IOException;
import java.util.Optional;

public interface ObjectWithTypeTokenConverter
        extends ObjectClassConverter<Object>,
                ConverterInto<ObjectWithTypeToken<?>, MutableObjectWithTypeToken<?>> {

    @Override
    boolean convertInto(ObjectWithTypeToken<?> from, MutableObjectWithTypeToken<?> into)
            throws ConversionException, IOException;

    default <T> ObjectWithTypeToken<T> convertToTypeOrThrow(Object input, TypeToken<T> intoType)
            throws ConversionException {
        var inputTypedObject = new ImmutableObjectWithTypeToken<>(input);
        var convertedObject = new MutableObjectWithTypeToken<>(intoType);
        convertIntoOrThrow(inputTypedObject, convertedObject);
        return convertedObject;
    }

    @Override
    default <T> Optional<T> convertToType(Object input, Class<T> type) throws IOException {
        var from = new ImmutableObjectWithTypeToken<>(input);
        var into = new MutableObjectWithTypeToken<>(TypeToken.of(type));
        if (convertInto(from, into)) return Optional.of(into.object());
        else return Optional.empty();
    }
}

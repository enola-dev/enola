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

import dev.enola.common.MutableObjectWithTypeToken;
import dev.enola.common.ObjectWithTypeToken;

import java.io.IOException;

public class ObjectWithTypeTokenConverterChain implements ObjectWithTypeTokenConverter {

    private final Iterable<ObjectWithTypeTokenConverter> converters;

    public ObjectWithTypeTokenConverterChain(Iterable<ObjectWithTypeTokenConverter> converters) {
        this.converters = converters;
    }

    @Override
    public boolean convertInto(ObjectWithTypeToken<?> from, MutableObjectWithTypeToken<?> into)
            throws ConversionException, IOException {
        for (var converter : converters) {
            if (converter.convertInto(from, into)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ObjectWithTypeTokenConverterChain[" + converters + ']';
    }
}

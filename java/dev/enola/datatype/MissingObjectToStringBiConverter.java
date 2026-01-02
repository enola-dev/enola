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
package dev.enola.datatype;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.ObjectToStringBiConverter;

class MissingObjectToStringBiConverter<T> implements ObjectToStringBiConverter<T> {

    private final String iri;

    public MissingObjectToStringBiConverter(String iri) {
        this.iri = iri;
    }

    @Override
    public String convertTo(T input) throws ConversionException {
        throw new UnsupportedOperationException(
                "This Datatype has not been configured to convert to String: " + iri);
    }

    @Override
    public T convertFrom(String input) throws ConversionException {
        throw new UnsupportedOperationException(
                "This Datatype has not been configured to convert from String: " + iri);
    }
}

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

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;

public class ObjectToStringBiConverterWithFormat
        implements BiConverter<Object, String>, ConverterInto<Object, StringBuffer> {
    // TODO Or better ConverterIntoAppendable<Object> instead ConverterInto<Object, StringBuffer>?

    private final Format format;

    public ObjectToStringBiConverterWithFormat(Format format) {
        this.format = format;
    }

    @Override
    public String convertTo(Object input) {
        return format.format(input);
    }

    @Override
    public Object convertFrom(String input) throws IllegalArgumentException {
        try {
            return format.parseObject(input);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Failed to convert: " + input, e);
        }
    }

    @Override
    public boolean convertInto(Object from, StringBuffer into) throws ConversionException {
        // TODO Test if this really works like this...
        format.format(from, into, ALL_FIELD_POSITIONS);
        return true;
    }

    private static final FieldPosition ALL_FIELD_POSITIONS = new FieldPosition(-1);
}

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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

import dev.enola.common.convert.ConversionException;

import java.util.Optional;
import java.util.regex.Pattern;

/** This is an example {@link ID}. */
@ID // TODO (converter = TestID.Converter.class)
public record TestID(long kind, String data) /* TODO implements HasIRI */ {

    public static final Pattern PATTERN = Pattern.compile("([0-9a-z]+)-(.+)");

    public static final IdConverter<TestID> CONVERTER = new Converter();

    // TODO @AutoService(IdConverter.class)
    public static final class Converter implements IdConverter<TestID> {

        private final int KIND_STRING_RADIX = 36;
        private final int MAX_KIND_STRING_LENGTH =
                Long.toUnsignedString(Long.MAX_VALUE, KIND_STRING_RADIX).length();

        @Override
        @SuppressWarnings("StringBufferReplaceableByString")
        public String convertTo(TestID input) throws ConversionException {
            return new StringBuilder(MAX_KIND_STRING_LENGTH + 1 + input.data.length())
                    .append(Long.toUnsignedString(input.kind, KIND_STRING_RADIX))
                    .append('-')
                    .append(input.data)
                    .toString();
        }

        @Override
        public Optional<TestID> convert(String input) throws ConversionException {
            var matcher = PATTERN.matcher(input);
            if (!matcher.matches()) return Optional.empty();
            var kindString = matcher.group(1);
            var kind = Long.parseUnsignedLong(kindString, KIND_STRING_RADIX);
            var data = matcher.group(2);
            return Optional.of(new TestID(kind, data));
        }

        @Override
        public Class<TestID> idClass() {
            return TestID.class;
        }
    }

    // TODO @Override
    public String iri() {
        return CONVERTER.convertTo(this);
    }
}

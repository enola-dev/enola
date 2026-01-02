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
package dev.enola.data.id;

import com.google.errorprone.annotations.Immutable;

import dev.enola.common.MoreStrings;
import dev.enola.common.convert.ConversionException;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

/** This is an example {@link ID}. */
@ID // TODO (converter = TestID.Converter.class)
@Immutable
public record TestID(long kind, String data) implements Comparable<TestID> {

    @Override
    public int compareTo(TestID other) {
        int kindComparison = Long.compare(kind, other.kind);
        if (kindComparison != 0) {
            return kindComparison;
        }
        return MoreStrings.compare(data, other.data);
    }

    public static final Pattern PATTERN = Pattern.compile("([0-9a-z]+)-(.+)");

    public static final IdConverter<TestID> CONVERTER = new Converter();

    // TODO @AutoService(IdConverter.class)
    private static final class Converter implements IdConverter<TestID> {
        private final int KIND_STRING_RADIX = 36;
        private final int MAX_KIND_STRING_LENGTH =
                Long.toUnsignedString(Long.MAX_VALUE, KIND_STRING_RADIX).length();

        @Override
        public boolean convertInto(TestID from, Appendable into)
                throws ConversionException, IOException {
            into.append(Long.toUnsignedString(from.kind, KIND_STRING_RADIX))
                    .append('-')
                    .append(from.data);
            return true;
        }

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
}

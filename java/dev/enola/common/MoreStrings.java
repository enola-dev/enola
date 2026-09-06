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
package dev.enola.common;

import com.google.common.base.Splitter;

import org.jspecify.annotations.Nullable;

import java.util.regex.Pattern;

public final class MoreStrings {

    private static final Splitter WORD_SPLITTER =
            Splitter.on(Pattern.compile("[-_\\s]+")).omitEmptyStrings();

    public static int compare(@Nullable String s1, @Nullable String s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        return s1.compareTo(s2);
    }

    /**
     * Converts a delimiter-separated string (e.g. kebab-case, snake_case, or space-separated) into
     * generic Title Case with space separators.
     */
    public static String toTitleCase(@Nullable String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        var words = WORD_SPLITTER.split(input);
        var sb = new StringBuilder();
        for (var word : words) {
            if (!sb.isEmpty()) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                sb.append(word.substring(1));
            }
        }
        return sb.toString();
    }

    /**
     * Returns the trimmed string, or {@code null} if the input is null, empty, or whitespace-only.
     *
     * <p>Complementary to Guava's {@link com.google.common.base.Strings#emptyToNull(String)}.
     */
    public static @Nullable String trimToNull(@Nullable String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        return input.trim();
    }

    /**
     * Replaces consecutive whitespace characters with a single space and trims leading and trailing
     * whitespace. Returns {@code null} if input is null, empty, or whitespace-only.
     *
     * @param input the string to normalize
     * @return the normalized string, or {@code null} if input is blank or null
     */
    public static @Nullable String normalizeWhitespace(@Nullable String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        return input.replaceAll("\\s+", " ").trim();
    }

    private MoreStrings() {}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2026 The Enola <https://enola.dev> Authors
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

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

class MoreStringsTest {

    @Test
    void toTitleCase() {
        assertThat(MoreStrings.toTitleCase(null)).isEmpty();
        assertThat(MoreStrings.toTitleCase("")).isEmpty();
        assertThat(MoreStrings.toTitleCase("antigravity")).isEqualTo("Antigravity");
        assertThat(MoreStrings.toTitleCase("vector-search")).isEqualTo("Vector Search");
        assertThat(MoreStrings.toTitleCase("memory_systems")).isEqualTo("Memory Systems");
        assertThat(MoreStrings.toTitleCase("hello world")).isEqualTo("Hello World");
        assertThat(MoreStrings.toTitleCase("multi-part-kebab-slug"))
                .isEqualTo("Multi Part Kebab Slug");
        assertThat(MoreStrings.toTitleCase("ai")).isEqualTo("Ai");
    }

    @Test
    void trimToNull() {
        assertThat(MoreStrings.trimToNull(null)).isNull();
        assertThat(MoreStrings.trimToNull("")).isNull();
        assertThat(MoreStrings.trimToNull("   ")).isNull();
        assertThat(MoreStrings.trimToNull("  hello  ")).isEqualTo("hello");
        assertThat(MoreStrings.trimToNull("hello world")).isEqualTo("hello world");
    }

    @Test
    void normalizeWhitespace() {
        assertThat(MoreStrings.normalizeWhitespace(null)).isNull();
        assertThat(MoreStrings.normalizeWhitespace("")).isNull();
        assertThat(MoreStrings.normalizeWhitespace("   \n\t  ")).isNull();
        assertThat(MoreStrings.normalizeWhitespace("  hello   world  ")).isEqualTo("hello world");
        assertThat(MoreStrings.normalizeWhitespace("line 1\nline 2\r\n\tline 3"))
                .isEqualTo("line 1 line 2 line 3");
    }
}

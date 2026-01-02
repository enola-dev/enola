/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.core.meta.docgen;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.core.meta.docgen.StringUtil.capitalize;

import org.junit.Test;

public class StringUtilTest {
    @Test
    public void testCapitalize() {
        assertThat(capitalize(null)).isNull();
        assertThat(capitalize("")).isEmpty();
        assertThat(capitalize("a")).isEqualTo("A");
        assertThat(capitalize("ab")).isEqualTo("Ab");
        assertThat(capitalize("abc")).isEqualTo("Abc");
    }
}

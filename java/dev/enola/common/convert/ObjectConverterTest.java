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

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.io.IOException;

public class ObjectConverterTest {

    private static record TestRecord(String name) {}

    ObjectClassConverter converter =
            new ObjectConverter<>(TestRecord.class, String.class, input -> input.name);

    @Test
    public void name() throws IOException {
        var testRecord = new TestRecord("dodo");
        var result = converter.convertToType(testRecord, String.class);
        assertThat(result).hasValue("dodo");
    }

    @Test
    public void isNull() throws IOException {
        var result = converter.convertToType(null, String.class);
        assertThat(result).isEmpty();
    }

    @Test
    public void other() throws IOException {
        var result = converter.convertToType(123L, String.class);
        assertThat(result).isEmpty();
    }
}

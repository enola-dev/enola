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

import static com.google.common.truth.Truth.assertThat;

import com.google.common.primitives.UnsignedLong;

import dev.enola.common.convert.ConversionException;
import dev.enola.data.iri.IRIConverter;

import org.junit.Test;

public class TestIRITest {

    IRIConverter<TestIRI> c = TestIRI.CONVERTER;
    TestID testId = new TestID(UnsignedLong.MAX_VALUE.longValue(), "test");
    String testIriString = "https://example.org/thing/3w5e11264sgsf-test";

    @Test
    public void convertToFrom() {
        var testIRI = c.convertFrom(testIriString);
        assertThat(c.convertTo(testIRI)).isEqualTo(testIriString);
        assertThat(testIRI.toString()).isEqualTo(testIriString);
        assertThat(testIRI.id()).isEqualTo(testId);
    }

    @Test(expected = ConversionException.class)
    public void convertMismatch() {
        c.convertFrom("https://example.org/other/xyz");
    }

    // TODO equals(), compareTo(), hashCode(), toString()
}

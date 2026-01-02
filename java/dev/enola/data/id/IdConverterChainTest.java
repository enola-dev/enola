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

import org.junit.Test;

import java.net.URI;

public class IdConverterChainTest {

    IdConverterChain chain =
            new IdConverterChain(TestID.CONVERTER, IdConverters.URI, IdConverters.STRING);

    @Test
    public void testID() {
        assertThat(chain.convert(TestIDTest.testIdString)).hasValue(TestIDTest.testId);
        assertThat(chain.convertTo(TestIDTest.testId)).isEqualTo(TestIDTest.testIdString);
    }

    @Test
    public void uri() {
        var uri = URI.create("https://docs.enola.dev/");
        assertThat(chain.convert(uri.toString())).hasValue(uri);
        assertThat(chain.convertTo(uri)).isEqualTo(uri.toString());
    }

    @Test
    public void string() {
        var string = "hello, world";
        assertThat(chain.convert(string)).hasValue(string);
        assertThat(chain.convertTo(string)).isEqualTo(string);
    }
}

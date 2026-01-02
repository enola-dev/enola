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
package dev.enola.common.linereader;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TestIOTest {

    @Test
    public void empty() {
        var io = new TestIO(List.of());
        assertThat(io.readLine()).isNull();
        assertThat(io.readLine("prompt> ")).isNull();
    }

    @Test
    public void in() {
        var io = new TestIO(List.of("hello", "world"));
        assertThat(io.readLine()).isEqualTo("hello");
        assertThat(io.readLine()).isEqualTo("world");
        assertThat(io.readLine()).isNull();
    }

    @Test
    public void out() {
        var io = new TestIO(List.of());
        io.printf("hello, %s\nTHE END", "world");
        assertThat(io.getOutput()).isEqualTo("hello, world\nTHE END");
    }

    @Test
    public void env() {
        var io = new TestIO(List.of());
        assertThat(io.ctx().environment()).isEmpty();
    }

    @Test
    public void is() throws IOException {
        var io = new TestIO(List.of("hello", "world"));
        var text = new String(io.ctx().input().readAllBytes(), TestIO.CHARSET);
        assertThat(text).isEqualTo("hello\nworld");
    }
}

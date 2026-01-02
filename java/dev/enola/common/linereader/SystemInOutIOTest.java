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

public class SystemInOutIOTest {

    @Test
    public void stdin() {
        SystemStdinStdoutTester.pipeIn(
                "hello\nworld\nend", // Intentionally no last \n at the end!
                () -> {
                    IO io = new SystemInOutIO();
                    assertThat(io.readLine()).isEqualTo("hello");
                    assertThat(io.readLine()).isEqualTo("world");
                    assertThat(io.readLine()).isEqualTo("end");
                });
    }

    @Test
    public void stdout() {
        var out =
                SystemStdinStdoutTester.captureOut(
                        () -> {
                            System.out.println("hello");
                            IO io = new SystemInOutIO();
                            io.printf("world");
                        });
        assertThat(out).isEqualTo("hello\nworld");
    }
}

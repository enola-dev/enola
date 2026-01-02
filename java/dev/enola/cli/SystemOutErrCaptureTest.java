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
package dev.enola.cli;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class SystemOutErrCaptureTest {
    @Test
    public void check() throws Exception {
        try (var capture = new SystemOutErrCapture()) {
            assertThat(capture.getSystemOut()).isEmpty();
            assertThat(capture.getSystemErr()).isEmpty();

            System.out.println("hello, world");
            assertThat(capture.getSystemOut()).isEqualTo("hello, world\n");
            assertThat(capture.getSystemErr()).isEmpty();

            System.err.print("hello, world");
            assertThat(capture.getSystemOut()).isEqualTo("hello, world\n");
            assertThat(capture.getSystemErr()).isEqualTo("hello, world");
        }

        try (var capture = new SystemOutErrCapture()) {
            System.err.print("goodbye");
            assertThat(capture.getSystemErr()).isEqualTo("goodbye");
            assertThat(capture.getSystemOut()).isEmpty();
        }
    }
}

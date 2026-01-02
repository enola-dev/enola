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
package dev.enola.common.exec.pty;

import static com.google.common.truth.Truth.assertThat;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.junit.Test;

import java.io.IOException;

public class AppendableOutputStreamTest {

    @Test
    public void euroSignWithSingleWrite() throws IOException {
        var sb = new StringBuilder();
        try (var aos = new AppendableOutputStream(sb, UTF_8)) {
            aos.write(new byte[] {(byte) 0xE2, (byte) 0x82, (byte) 0xAC});
        }
        assertThat(sb.toString()).isEqualTo("€"); // "\u20AC"
    }

    @Test
    public void euroSignWithThreeByteWrites() throws IOException {
        var sb = new StringBuilder();
        try (var aos = new AppendableOutputStream(sb, UTF_8)) {
            aos.write(0xE2);
            aos.write(0x82);
            aos.write(0xAC);
        }
        assertThat(sb.toString()).isEqualTo("€"); // "\u20AC"
    }
}

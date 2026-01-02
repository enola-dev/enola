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
package dev.enola.common.exec;

import static com.google.common.truth.Truth.assertThat;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.collect.ImmutableMap;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Path;

public class MockProcessLauncherTest {

    @Test
    public void success() {
        var env = ImmutableMap.<String, String>of();
        var launcher = new MockProcessLauncher(0, "hello".getBytes(UTF_8), new byte[0]);
        var baos = new ByteArrayOutputStream();
        Exec.run(launcher, env, Path.of("/"), InputStream.nullInputStream(), baos, "ls");
        assertThat(baos.toString(UTF_8)).isEqualTo("hello");
    }

    // TODO @Test void failure() {}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2024 The Enola <https://enola.dev> Authors
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
package dev.enola.common.markdown.exec;

import static org.junit.Assert.assertEquals;

import static java.nio.file.Path.of;
import static java.time.Duration.ofSeconds;

import com.google.common.truth.Truth;

import org.junit.Test;

public class RunnerTest {

    Runner runner = new VorburgerExecRunner(); // NuProcessRunner();

    void check(String command, boolean expectNonZeroExitCode, String expectedOutput)
            throws Exception {
        var sb = new StringBuffer();
        var actualExitCode = runner.bash(expectNonZeroExitCode, of("."), command, sb, ofSeconds(3));
        Truth.assertThat(sb.toString()).contains(expectedOutput);
        assertEquals(expectNonZeroExitCode, actualExitCode != 0);
    }

    @Test
    public void testTrue() throws Exception {
        check("true", false, "");
    }

    @Test
    public void testEcho() throws Exception {
        check("echo hi", false, "hi\n");
    }

    @Test
    public void testFalse() throws Exception {
        check("false", true, "");
    }

    @Test
    public void testInexistantCommand() throws Exception {
        check("does-not-exist", true, "does-not-exist: command not found\n");
    }
}

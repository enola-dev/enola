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
package dev.enola.common.exec.vorburger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static java.nio.file.Path.of;
import static java.time.Duration.ofSeconds;

import com.google.common.truth.Truth;

import org.junit.Test;

public class RunnerTest {

    // TODO Merge with PtyRunnerTest

    Runner runner = new VorburgerExecRunner(); // NuProcessRunner();

    void check(String command, ExpectedExitCode expectedExitCode, String expectedOutput)
            throws Exception {
        var sb = new StringBuilder();
        var actualExitCode = runner.bash(expectedExitCode, of("."), command, sb, ofSeconds(3));
        Truth.assertThat(sb.toString()).contains(expectedOutput);

        switch (expectedExitCode) {
            case SUCCESS -> assertEquals(0, actualExitCode);
            case FAIL -> assertTrue(actualExitCode != 0);
            case IGNORE -> {}
        }
    }

    @Test
    public void testEcho() throws Exception {
        check("echo hi", ExpectedExitCode.SUCCESS, "hi\n");
    }

    @Test
    public void testTrue() throws Exception {
        check("true", ExpectedExitCode.SUCCESS, "");
    }

    @Test
    public void testFalse() throws Exception {
        check("false", ExpectedExitCode.FAIL, "");
    }

    @Test
    public void testIgnoreExitCode() throws Exception {
        check("true", ExpectedExitCode.IGNORE, "");
        check("false", ExpectedExitCode.IGNORE, "");
    }

    @Test
    public void testInexistantCommand() throws Exception {
        check("does-not-exist", ExpectedExitCode.FAIL, "does-not-exist: command not found\n");
    }
}

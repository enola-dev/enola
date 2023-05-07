/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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

import org.junit.Test;

import java.nio.file.Path;
import java.time.Duration;

public class RunnerTest {

    Runner runner = new VorburgerExecRunner(); // NuProcessRunner();

    void check(String command, int expectedExitCode, String expectedOutput) throws Exception {
        var sb = new StringBuffer();
        var actualExitCode = runner.bash(Path.of("."), command, sb, Duration.ofSeconds(3));
        assertEquals(expectedOutput, sb.toString());
        assertEquals(expectedExitCode, actualExitCode);
    }

    @Test
    public void testTrue() throws Exception {
        check("true", 0, "");
    }

    @Test
    public void testEcho() throws Exception {
        check("echo hi", 0, "hi\n");
    }

    @Test
    public void testFalse() throws Exception {
        check("false", 1, "");
    }

    @Test
    public void testInexistantCommand() throws Exception {
        int exitValue = 127; // or Integer.MIN_VALUE for NuProcess
        check("does-not-exist", exitValue, "bash: line 1: does-not-exist: command not found\n");
    }
}

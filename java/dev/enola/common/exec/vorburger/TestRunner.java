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
package dev.enola.common.exec.vorburger;

import com.google.common.annotations.VisibleForTesting;

import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

@VisibleForTesting
public class TestRunner implements Runner {

    // See also (TBD) https://github.com/vorburger/ch.vorburger.exec/issues/10

    // TODO Remove when replaced with MockProcessLauncher

    private final int exitCode;
    public final String output;
    private final @Nullable Exception exception;

    public ExpectedExitCode expectedExitCode;
    public Path dir;
    public List<String> command;
    public Duration timeout;

    public TestRunner(int exitCode, String output) {
        this.exitCode = exitCode;
        this.output = output;
        this.exception = null;
    }

    public TestRunner(Exception e) {
        this.exitCode = 0;
        this.output = null;
        this.exception = e;
    }

    @Override
    public int exec(
            ExpectedExitCode expectedExitCode,
            Path dir,
            List<String> command,
            Appendable output,
            Duration timeout)
            throws Exception {

        if (exception != null) throw exception;

        this.expectedExitCode = expectedExitCode;
        this.dir = dir;
        this.command = command;
        this.timeout = timeout;

        output.append(this.output);

        return exitCode;
    }
}

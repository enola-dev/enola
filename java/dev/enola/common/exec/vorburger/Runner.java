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

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

/**
 * API to easily run an external process. Implementations could be using e.g. java.lang.{@link
 * ProcessBuilder}, or <a
 * href="https://github.com/vorburger/ch.vorburger.exec">ch.vorburger.exec</a>, or <a
 * href="https://github.com/brettwooldridge/NuProcess">NuProcess</a>, or <a
 * href="https://github.com/zeroturnaround/zt-exec">zt-exec</a>.
 */
public interface Runner {

    // TODO Fully replace Runner exec() with ProcessLauncher eventually

    int exec(
            ExpectedExitCode expectedExitCode,
            Path dir,
            List<String> command,
            Appendable output,
            Duration timeout)
            throws Exception;

    // TODO Replace bash() with a new BashProcessLauncher? Or just replace usages.

    default int bash(
            ExpectedExitCode expectedExitCode,
            Path dir,
            String command,
            Appendable output,
            Duration timeout)
            throws Exception {
        return exec(
                expectedExitCode,
                dir,
                // TODO if(IS_WINDOWS) "cmd.exe", "/c"
                List.of("/usr/bin/env", "bash", "-c", command),
                output,
                timeout);
    }
}

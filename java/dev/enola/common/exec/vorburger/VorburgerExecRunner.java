/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2025 The Enola <https://enola.dev> Authors
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

import static java.nio.charset.StandardCharsets.UTF_8;

import ch.vorburger.exec.ManagedProcess;
import ch.vorburger.exec.ManagedProcessBuilder;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

public class VorburgerExecRunner implements Runner {

    @Override
    public int exec(
            ExpectedExitCode expectedExitCode,
            Path dir,
            List<String> command,
            Appendable output,
            Duration timeout)
            throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ManagedProcessBuilder pb =
                new ManagedProcessBuilder(command.get(0))
                        .setWorkingDirectory(dir.toFile())
                        .setDestroyOnShutdown(true)
                        .addStdOut(baos)
                        .addStdErr(baos);
        for (var arg : command.subList(1, command.size())) {
            pb.addArgument(arg, false);
        }
        switch (expectedExitCode) {
            case SUCCESS -> pb.setIsSuccessExitValueChecker(exitValue -> exitValue == 0);
            case FAIL -> pb.setIsSuccessExitValueChecker(exitValue -> exitValue != 0);
            case IGNORE -> pb.setIsSuccessExitValueChecker(exitValue -> true);
        }
        ManagedProcess p = pb.build();

        try {
            return p.start().waitForExitMaxMs(timeout.toMillis());
        } finally {
            output.append(baos.toString(UTF_8));
        }
    }
}

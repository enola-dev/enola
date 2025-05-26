/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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

import static java.util.Objects.requireNonNull;

import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.nio.file.Path;

public class Demo {

    public static void main(String[] args) throws IOException, InterruptedException {
        // TODO system(false)
        try (Terminal terminal = TerminalBuilder.builder().system(true).build()) {
            // TODO .systemOutput(ForcedSysOut) ?

            // TODO Fix the (ugly) "echo" which "re-displays" all Fish shell input again
            // This does not work well, see https://github.com/JetBrains/pty4j/issues/170;
            // Disabling echo does NOT help: terminal.echo(false);
            // Do NOT enter raw, it's worse: terminal.enterRawMode();

            int result;
            // TODO Read from $SHELL (and use cmd.exe on Windows)
            String[] cmd = {"/usr/bin/fish", "-li"};
            System.out.println("Starting: " + String.join(" ", cmd));
            try (var runner =
                    new PtyRunner(
                            true,
                            Path.of("."),
                            cmd,
                            System.getenv(),
                            requireNonNull(terminal.input(), "terminal.input"),
                            requireNonNull(terminal.output(), "terminal.output"),
                            null,
                            true)) {
                // System.out.println("Running, and awaiting exit of: " + String.join(" ", cmd));
                resize(terminal, runner);
                terminal.handle(Terminal.Signal.WINCH, signal -> resize(terminal, runner));
                result = runner.waitForExit();
            }
            System.out.println("PTY demo exits!");
            System.exit(result);
        }
    }

    private static void resize(Terminal terminal, PtyRunner runner) {
        Size size = terminal.getSize();
        var cols = size.getColumns();
        var rows = size.getRows();
        if (cols > 0 && rows > 0) runner.size(cols, rows);
        // Do NOT "terminal.writer().flush()" - when exec, this destroys the child process updates!
    }
}

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
package dev.enola.cli.common;

import picocli.CommandLine;

public class QuietExecutionExceptionHandler implements CommandLine.IExecutionExceptionHandler {

    private final Application app;

    public QuietExecutionExceptionHandler(Application app) {
        this.app = app;
    }

    @Override
    public int handleExecutionException(
            Exception ex, CommandLine cmd, CommandLine.ParseResult parseResult) throws Exception {
        if (app.loggingVerbosity > 0) {
            cmd.getErr().println(cmd.getColorScheme().richStackTraceString(ex));
        } else {
            var intro = "Internal Problem occurred, add -vvv flags for technical details: ";
            cmd.getErr().print(cmd.getColorScheme().optionText(intro));
            Throwable e = ex;
            while (e != null) {
                var type = e.getClass().getSimpleName();
                var msg = e.getMessage();
                var full = type + (msg != null ? ": " + msg : "");
                cmd.getErr().println(cmd.getColorScheme().errorText(full));
                e = e.getCause();
                if (e != null) {
                    cmd.getErr().print("caused by: ");
                }
            }
        }
        cmd.getErr().flush();
        return cmd.getExitCodeExceptionMapper() != null
                ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
                : cmd.getCommandSpec().exitCodeOnExecutionException();
    }
}

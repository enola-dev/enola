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
package dev.enola.ai.adk.tool.builtin;

import static dev.enola.common.SuccessOrError.error;
import static dev.enola.common.SuccessOrError.success;

import com.google.adk.tools.Annotations;
import com.google.adk.tools.BaseTool;
import com.google.adk.tools.FunctionTool;

import dev.enola.ai.adk.tool.Tools;
import dev.enola.common.SuccessOrError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ExecTool {

    // TODO Make allowed commands configurable

    // TODO Use stuff from package dev.enola.common.exec

    public BaseTool createTool() {
        return FunctionTool.create(this, "executeCommand");
    }

    @Annotations.Schema(
            description =
                    "Executes a shell command and captures its standard output and standard error.")
    public Map<String, ?> executeCommand(
            @Annotations.Schema(description = "The command to execute (e.g., 'ls -l').")
                    String command) {
        return Tools.toMap(executeCommandHelper(command));
    }

    private SuccessOrError<String> executeCommandHelper(String command) {
        try {
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
            pb.redirectErrorStream(true); // Combine stdout and stderr
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            if (!process.waitFor(10, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                return error("Command timed out after 10 seconds.");
            }

            int exitCode = process.exitValue();
            return success(
                    String.format(
                            "Exit Code: %d%nOutput:%n%s", exitCode, output.toString().trim()));
        } catch (IOException | InterruptedException e) {
            return error("Failed to execute command: " + e.getMessage());
        }
    }
}

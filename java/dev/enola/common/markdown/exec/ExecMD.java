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
package dev.enola.common.markdown.exec;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.io.Files;

import dev.enola.common.exec.vorburger.ExpectedExitCode;
import dev.enola.common.exec.vorburger.Runner;
import dev.enola.common.exec.vorburger.VorburgerExecRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.stream.Collectors;

public class ExecMD {

    private static final String LS = System.lineSeparator();
    private final Runner runner = new VorburgerExecRunner();

    public void process(File mdFile, boolean inplace)
            throws IOException, MarkdownProcessingException {
        var markdownIn = Files.asCharSource(mdFile, UTF_8).read();
        var directory = mdFile.getAbsoluteFile().getParentFile();
        var output = process(directory.toPath(), markdownIn);

        var script = new File(directory, "script");
        Files.asCharSink(script, UTF_8).write(output.script);

        if (inplace) {
            Files.asCharSink(mdFile, UTF_8).write(output.markdown);
        } else {
            System.out.println(output.markdown);
        }
    }

    Pair process(Path dir, String markdown) throws MarkdownProcessingException, IOException {
        var outScript = new StringBuilder();
        var outMD = new StringBuilder();
        var markdownLines = markdown.lines().collect(Collectors.toList());
        var iterator = markdownLines.iterator();
        while (iterator.hasNext()) {
            var line = iterator.next();
            if (!line.startsWith("```bash")) {
                outMD.append(line);
                outMD.append(LS);
                continue;
            }
            var preamble = line;
            if (!iterator.hasNext()) {
                throw new MarkdownProcessingException("```bash cannot be last line!");
            }
            // TODO https://github.com/squidfunk/mkdocs-material/issues/5473
            // outMD.append(line);
            outMD.append("```bash");
            outMD.append(LS);
            var command = new StringBuilder();
            var commandLine = iterator.next().trim();
            if (!commandLine.startsWith("$ ")) {
                throw new MarkdownProcessingException(
                        "First line after ```bash must start with '$ ' (with space) and a command"
                                + " to execute!");
            }
            outMD.append(commandLine);
            outMD.append(LS);
            command.append(commandLine.substring(2));
            command.append(LS);
            while (commandLine.trim().endsWith("\\")) {
                commandLine = iterator.next();
                command.append(commandLine);
                command.append(LS);
                outMD.append(commandLine);
                outMD.append(LS);
            }
            while (iterator.hasNext() && !commandLine.trim().endsWith("```")) {
                commandLine = iterator.next();
                // Do *NOT* out.append(commandLine) - because we want to skip existing output!
            }

            exec(dir, preamble, command.toString(), outScript, outMD);
            if (!endsWith(outMD, '\n')) outMD.append("\n");
            outMD.append("```\n");
        }

        outScript.append("sleep ${SLEEP:-7}\n");

        var pair = new Pair();
        pair.markdown = outMD.toString();
        pair.script = outScript.toString();
        return pair;
    }

    private boolean endsWith(CharSequence cs, char trailing) {
        return cs.charAt(cs.length() - 1) == trailing;
    }

    int exec(Path dir, String preamble, String command, Appendable script, Appendable md)
            throws MarkdownProcessingException, IOException {

        if (!preamble.startsWith("```bash")) throw new IllegalArgumentException(preamble);
        preamble = preamble.substring("```bash".length()).trim();

        ExpectedExitCode expectedExitCode;
        if (preamble.startsWith("$?")) {
            expectedExitCode = ExpectedExitCode.FAIL;
            preamble = preamble.substring("$?".length()).trim();
        } else if (preamble.startsWith("$%")) {
            expectedExitCode = ExpectedExitCode.IGNORE;
            preamble = preamble.substring("$%".length()).trim();
        } else expectedExitCode = ExpectedExitCode.SUCCESS;

        String fullCommand;
        script.append(":CWD=$(pwd)\n");
        if (!preamble.trim().isEmpty()) {
            script.append(":");
            script.append(preamble);
            script.append("\n");

            fullCommand = preamble + " && " + command;
        } else {
            fullCommand = command;
        }
        script.append(command);
        script.append("\n");
        script.append(":cd $CWD\n");
        script.append("\n\n");

        // TODO Allow current hard-coded timeout to be configured in MD preamble, or CLI option?
        Duration timeout = Duration.ofSeconds(7);

        try {
            return runner.bash(expectedExitCode, dir, fullCommand, md, timeout);
        } catch (Exception e) {
            throw new MarkdownProcessingException(
                    "exec failed (use ```bash $? marker if that's expected): " + fullCommand, e);
        }
    }

    static class Pair {
        String markdown;
        String script;
    }
}

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

import static com.google.common.base.Charsets.UTF_8;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class ExecMD {

    private static final String LS = System.lineSeparator();
    private final Runner runner = new VorburgerExecRunner(); // NuProcessRunner();

    public int process(List<File> mdFiles, boolean inplace)
            throws IOException, MarkdownProcessingException {
        for (var file : mdFiles) {
            // TODO Parallelize instead of blocking?
            process(file, inplace);
        }
        return 0;
    }

    void process(File mdFile, boolean inplace) throws IOException, MarkdownProcessingException {
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
        StringBuffer outScript = new StringBuffer();
        StringBuffer outMD = new StringBuffer();
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
            StringBuffer command = new StringBuffer();
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
            outMD.append("```\n");
        }

        outScript.append("sleep ${SLEEP:-7}\n");

        var pair = new Pair();
        pair.markdown = outMD.toString();
        pair.script = outScript.toString();
        return pair;
    }

    void exec(Path dir, String preamble, String command, Appendable script, Appendable md)
            throws MarkdownProcessingException, IOException {

        if (!preamble.startsWith("```bash")) throw new IllegalArgumentException(preamble);
        preamble = preamble.substring("```bash".length()).trim();

        boolean expectFailure;
        if (preamble.startsWith("$?")) {
            expectFailure = true;
            preamble = preamble.substring("$?".length()).trim();
        } else expectFailure = false;

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
            var exitCode = runner.bash(dir, fullCommand, md, timeout);
            if (exitCode != 0 && !expectFailure) {
                throw new MarkdownProcessingException(
                        exitCode
                                + " exit code (use ```bash $? marker if that's expected): "
                                + fullCommand);
            }
            if (exitCode == 0 && expectFailure) {
                throw new MarkdownProcessingException(
                        "exit code 0, but was expected to fail: " + fullCommand);
            }

        } catch (Exception e) {
            throw new MarkdownProcessingException("exec failed: " + fullCommand, e);
        }
    }

    static class Pair {
        String markdown;
        String script;
    }
}

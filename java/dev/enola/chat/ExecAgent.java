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
package dev.enola.chat;

import dev.enola.common.markdown.exec.Runner;
import dev.enola.common.markdown.exec.VorburgerExecRunner;
import dev.enola.identity.Hostnames;
import dev.enola.identity.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExecAgent extends AbstractAgent {

    // TODO Support running programs like "nano" or "fish" which need STDIN to be a TTY Terminal

    private static final Logger LOG = LoggerFactory.getLogger(ExecAgent.class);

    // TODO Offer tab completion of all available commands in Chat

    private final Runner runner = new VorburgerExecRunner();

    private final Map<String, File> executables = ExecPATH.scan();

    // TODO Support changing working directory with a built-in "cd" command
    //   (which must be handled BEFORE /usr/bin/cd is invoked)
    private final Path cwd = Path.of(".");

    public ExecAgent(Switchboard pbx) {
        super(
                tbf.create(Subject.Builder.class, Subject.class)
                        .iri("http://" + Hostnames.LOCAL)
                        .label(Hostnames.LOCAL)
                        .comment("Executes Commands")
                        .build(),
                pbx);
    }

    @Override
    public void accept(Message message) {
        var executable = executable(message.content());
        var executableFile = executables.get(executable);
        if (executableFile == null) return;

        // Replace first part of command line with executable file path
        var commandArray = message.content().split(" ");
        var commandList = new ArrayList<String>(commandArray.length);
        commandList.addAll(List.of(commandArray));
        commandList.set(0, executableFile.getAbsolutePath());

        // TODO Allow running without timeout?
        var timeout = Duration.ofDays(1);

        // TODO Support streaming outputBuilder into Chat (see also LangChain4jAgent)
        var outputBuilder = new StringBuilder();
        try {
            // TODO Feedback exitCode also to Chat (somewhow; but how?!)
            var exitCode = runner.exec(false, cwd, commandList, outputBuilder, timeout);
            LOG.debug("Executed: {} => {}", executableFile.getAbsolutePath(), exitCode);
            var output = outputBuilder.toString();
            if (!output.isEmpty()) {
                reply(message, output);
            }

        } catch (Exception e) {
            LOG.warn("Failed to execute: {}", commandList, e);
            reply(
                    message,
                    "Failed to execute: " + executable + "; due to " + e.getMessage() + "\n");
        }
    }

    private String executable(String messageContent) {
        var idx = messageContent.indexOf(' ');
        if (idx == -1) {
            return messageContent;
        } else {
            return messageContent.substring(0, idx);
        }
    }
}

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

import static java.nio.file.Files.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import dev.enola.common.exec.ExecPATH;
import dev.enola.common.exec.vorburger.ExpectedExitCode;
import dev.enola.common.exec.vorburger.Runner;
import dev.enola.common.exec.vorburger.VorburgerExecRunner;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.linereader.IO;
import dev.enola.identity.Hostnames;
import dev.enola.identity.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;

public class ExecAgent extends AbstractAgent {

    // TODO Must check if command is a path or on the PATH

    // TODO If PATH contains "." then this won't really work yet as-is

    // TODO Offer tab completion of all available commands in Chat
    //   see https://jline.org/docs/tab-completion

    // TODO Ctrl-R FZF History Search Widget; see https://github.com/jline/jline3/issues/1246

    // TODO cd without argument should CWD to $HOME
    // TODO implicit cd with dirname (UNLESS it's also a command on PATH), like Fish Shell does

    // TODO Support "who am i"; see https://github.com/vorburger/ch.vorburger.exec/issues/269

    private static final Logger LOG = LoggerFactory.getLogger(ExecAgent.class);

    private final Runner runner;
    private final Map<String, File> executablesMap;
    private final List<String> executables;
    private final Set<String> commandWords = loadCommandWords();

    private Path cwd = Path.of(".");
    private final String forceExecPrefix;

    /**
     * Constructor.
     *
     * @param pbx the PBX
     * @param runner the exec runner
     * @param executablesMap the executables on PATH
     * @param forceExecPrefix the prefix to force execution of a command, e.g. "$ " or "!", or
     *     whatever. (Note that <a href="https://github.com/jline/jline3/issues/1218">JLine handles
     *     "!" as history expansion, so that needs to disabled</a>.)
     */
    public ExecAgent(
            Switchboard pbx,
            Runner runner,
            Map<String, File> executablesMap,
            String forceExecPrefix) {
        super(
                tbf.create(Subject.Builder.class, Subject.class)
                        .iri("http://" + Hostnames.LOCAL)
                        .label(Hostnames.LOCAL)
                        .comment("Executes Commands on " + Hostnames.LOCAL)
                        .build(),
                pbx);
        this.runner = runner;
        this.executablesMap = ImmutableMap.copyOf(executablesMap); // skipcq: JAVA-E1086
        // skipcq: JAVA-E1086
        this.executables = ImmutableList.copyOf(executablesMap.keySet().stream().sorted().toList());
        this.forceExecPrefix = forceExecPrefix;
    }

    public ExecAgent(Switchboard pbx, IO io) {
        this(pbx, new VorburgerExecRunner(), ExecPATH.scan(), "$ ");
    }

    @Override
    public void accept(Message message) {
        // Skip processing self-replies from itself; this might need some more thought?
        if (message.from().iri().equals(subject().iri())) return;

        // /commands is inspired e.g. by Fish's "command --all",
        //   see https://fishshell.com/docs/current/cmds/command.html
        if (handle(message, "/commands", () -> reply(message, String.join("\n", executables))))
            return;

        if (handle(message, "cd", this::cd)) return;

        // See https://github.com/enola-dev/enola/issues/1354
        var potentialCommand = message.content();
        if (potentialCommand.startsWith(forceExecPrefix)
                && potentialCommand.length() > forceExecPrefix.length()) {
            execute(potentialCommand.substring(forceExecPrefix.length()), false, message);
            return;
        }

        execute(potentialCommand, true, message);
    }

    private void cd(String path) {
        cwd = cwd.resolve(path.trim());
    }

    private void execute(String potentialCommand, boolean checkCommandWords, Message replyTo) {
        var executable = executable(potentialCommand);
        var executablePath = cwd.resolve(executable);
        if (!executable.startsWith("/")
                && !executable.startsWith("./")
                && !executable.startsWith("../")) {
            if (checkCommandWords && commandWords.contains(executable)) return;
            var executableFile = executablesMap.get(executable);
            if (executableFile == null) {
                LOG.info("Unknown executable: {}", executable);
                return;
            }
        } else if (!isRegularFile(executablePath)
                || !isReadable(executablePath)
                || !isExecutable(executablePath)) return;

        // TODO Allow running without timeout?
        var timeout = Duration.ofDays(1);

        // TODO Support streaming outputBuilder into Chat (see also LangChain4jAgent)
        var outputBuilder = new StringBuilder();
        try {
            // TODO Feedback exitCode also to Chat (somehow; but how?!)
            //   Well, just like in Bash/Fish, with Emoji emoji (😊 for success, 😞 for failure)
            //   in the NEXT prompt... how how to "generalize" this here?
            // TODO Run command in $SHELL instead of hard-coding bash -c.
            var exitCode =
                    runner.bash(
                            ExpectedExitCode.SUCCESS,
                            cwd,
                            potentialCommand,
                            outputBuilder,
                            timeout);
            LOG.debug("Executed: {} => {}", potentialCommand, exitCode);
            var output = outputBuilder.toString();
            if (!output.trim().isEmpty()) {
                reply(replyTo, output);
            }

        } catch (Exception e) {
            LOG.warn("Failed to execute: {}", potentialCommand, e);
            reply(
                    replyTo,
                    "Failed to execute: " + executable + "; due to " + e.getMessage() + "\n");
        }
    }

    private String executable(String messageContent) {
        String command;
        var idx = messageContent.indexOf(' ');
        if (idx == -1) {
            command = messageContent.trim();
        } else {
            command = messageContent.substring(0, idx).trim();
        }

        idx = command.indexOf(';');
        if (idx > -1) {
            command = command.substring(0, idx).trim();
        }

        idx = command.indexOf('|');
        if (idx > -1) {
            command = command.substring(0, idx).trim();
        }

        idx = command.indexOf("&&");
        if (idx > -1) {
            command = command.substring(0, idx + 1).trim();
        }

        idx = command.indexOf('<');
        if (idx > -1) {
            command = command.substring(0, idx).trim();
        }

        idx = command.indexOf('>');
        if (idx > -1) {
            command = command.substring(0, idx).trim();
        }

        return command;
    }

    // See https://github.com/enola-dev/enola/issues/1354
    private Set<String> loadCommandWords() {
        var builder = ImmutableSet.<String>builder();
        try {
            new ClasspathResource("command-words.txt").charSource().forEachLine(builder::add);
        } catch (IOException e) {
            throw new IllegalStateException("Processing /command-words.txt failed?!", e);
        }
        return builder.build();
    }
}

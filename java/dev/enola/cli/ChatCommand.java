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
package dev.enola.cli;

import com.google.common.collect.ImmutableMap;

import dev.enola.chat.AbstractAgent;
import dev.enola.chat.Message;
import dev.enola.chat.Prompter;
import dev.enola.chat.Switchboard;
import dev.enola.common.context.TLC;
import dev.enola.common.linereader.SystemInOutIO;
import dev.enola.common.linereader.jline.JLineAgent;
import dev.enola.common.linereader.jline.JLineBuiltinCommandsProcessor;
import dev.enola.common.linereader.jline.JLineIO;
import dev.enola.common.secret.InMemorySecretManager;
import dev.enola.identity.Subject;
import dev.enola.identity.Subjects;
import dev.enola.rdf.io.JavaThingIntoRdfAppendableConverter;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.io.ThingIntoAppendableConverter;
import dev.enola.thing.java.ProxyTBF;

import org.jline.console.SystemRegistry;
import org.jline.console.impl.SystemRegistryImpl;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.TerminalBuilder;

import picocli.CommandLine;
import picocli.shell.jline3.PicocliCommands;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "chat",
        description = "Chat with Enola, LLMs, Bots, Tools, Agents, and more.")
public class ChatCommand implements Callable<Integer> {

    // TODO Merge the new Chat2Command with this!!

    @CommandLine.Spec CommandLine.Model.CommandSpec spec;

    @Override
    public Integer call() throws IOException {
        try (var ctx = TLC.open()) {
            var tbf = new ProxyTBF(ImmutableThing.FACTORY);
            var subject = new Subjects(tbf).local();
            ctx.push(ThingIntoAppendableConverter.class, new JavaThingIntoRdfAppendableConverter());
            if (System.console() != null) {
                // TODO Parser configuration should be in class JLineIO, not here
                DefaultParser parser = new DefaultParser();
                parser.setEofOnUnclosedQuote(false);
                parser.setEofOnEscapedNewLine(false);
                parser.setEofOnUnclosedBracket((DefaultParser.Bracket[]) null);
                parser.setRegexVariable(null); // We do not have console variables!

                try (var terminal = TerminalBuilder.terminal()) {
                    var parentCommandLine = spec.commandLine().getParent();
                    var picocliCommands = new PicocliCommands(parentCommandLine);
                    var builtinCmdsProcessor = new JLineBuiltinCommandsProcessor(terminal);
                    var cwdSupplier = builtinCmdsProcessor.cwdSupplier();
                    SystemRegistry systemRegistry =
                            new SystemRegistryImpl(parser, terminal, cwdSupplier, null);
                    systemRegistry.setCommandRegistries(
                            builtinCmdsProcessor.commandRegistry(), picocliCommands);

                    try (var io =
                            new JLineIO(
                                    System.getenv(),
                                    terminal,
                                    parser,
                                    systemRegistry.completer(),
                                    ImmutableMap.of(),
                                    systemRegistry::commandDescription,
                                    true)) {
                        builtinCmdsProcessor.lineReader(io.lineReader());
                        var prompter = new Prompter(new InMemorySecretManager());
                        var pbx = prompter.getSwitchboard();
                        prompter.addAgent(new JLineAgent(pbx, builtinCmdsProcessor));
                        prompter.addAgent(new EnolaAgent(pbx, parentCommandLine));
                        prompter.chatLoop(io, subject, true);
                    }
                }
            } else {
                var io = new SystemInOutIO();
                new Prompter(new InMemorySecretManager()).chatLoop(io, subject, true);
            }
        }
        return 0;
    }

    /** EnolaAgent runs Enola's own CLI sub-commands. */
    private static class EnolaAgent extends AbstractAgent {
        private final CommandLine picocliCommandLine;
        private final Set<String> commands;

        public EnolaAgent(Switchboard pbx, CommandLine commandLine) {
            super(
                    tbf.create(Subject.Builder.class, Subject.class)
                            .iri("https://enola.dev")
                            .label("enola")
                            .comment("Enola's own CLI sub-commands.")
                            .build(),
                    pbx);
            this.picocliCommandLine = commandLine;
            this.commands = picocliCommandLine.getSubcommands().keySet();
        }

        @Override
        public void accept(Message message) {
            var commandLine = message.content();
            // split() won't handle quoted arguments correctly, which is fine here (for simple
            // Builtins), but don't re-use this as-is for other more complex external commands.
            var splitCommandLine = List.of(commandLine.split("\\s+"));

            var command = splitCommandLine.get(0);
            if (!commands.contains(command)) return;

            // TODO Do something with exit code (just like for exec, where it's also still ignored)
            int exit = picocliCommandLine.execute(splitCommandLine.toArray(new String[0]));
        }
    }
}

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

import com.google.adk.agents.BaseAgent;
import com.google.adk.events.Event;
import com.google.common.collect.ImmutableMap;
import com.google.genai.types.Content;
import com.google.genai.types.Part;

import dev.enola.ai.adk.core.CLI;
import dev.enola.ai.adk.core.UserSessionRunner;
import dev.enola.cli.AiOptions.WithAgentName;
import dev.enola.common.context.TLC;
import dev.enola.common.linereader.IO;
import dev.enola.common.linereader.jline.JLineBuiltinCommandsProcessor;
import dev.enola.common.linereader.jline.JLineIO;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import org.jline.console.SystemRegistry;
import org.jline.console.impl.SystemRegistryImpl;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.TerminalBuilder;
import org.jspecify.annotations.Nullable;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;
import picocli.shell.jline3.PicocliCommands;

import java.io.IOException;

@CommandLine.Command(
        name = "chat2",
        hidden = true,
        description = "Chat with Enola, LLMs, Bots, Tools, Agents, and more.")
public class Chat2Command extends CommandWithResourceProvider {

    // TODO Use https://jline.org/docs/advanced/interactive-features/ LineReader.printAbove()

    // TODO Replace original ChatCommand with this, and then rm hidden = true

    @Spec @Nullable CommandSpec spec;

    @CommandLine.ArgGroup(exclusive = false)
    @Nullable WithAgentName aiOptions;

    @Override
    public Integer call() throws Exception {
        super.run();
        try (var ctx = TLC.open()) {
            setup(ctx);
            runInContext();
        }
        return 0;
    }

    private void runInContext() throws Exception {
        if (spec == null) throw new IllegalStateException("spec is null?!");
        if (System.console() == null)
            throw new IOException(
                    "Cannot chat because no console available, consider using 'ai' instead"
                            + " 'chat'?");

        var agent = AI.load1(rp, aiOptions);

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
                ai(io, agent);
            }
        }
    }

    // TODO Factor some of this out into new class
    //   dev.enola.ai.adk.jline.CLI; see also dev.enola.ai.adk.core.CLI
    private void ai(IO io, BaseAgent agent) throws IOException {
        var disposables = new CompositeDisposable();
        try (var runner = new UserSessionRunner(CLI.userID(), agent)) {
            do {
                var prompt = io.readLine(agent.name() + ">");
                if (prompt == null
                        || prompt.equalsIgnoreCase("quit")
                        || prompt.equalsIgnoreCase("exit")
                        || prompt.equalsIgnoreCase("/quit")
                        || prompt.equalsIgnoreCase("/exit")) {
                    break;
                }

                // TODO Make this "streaming" and trickle in text, instead of wait & print all...
                Content userMsg = Content.fromParts(Part.fromText(prompt));
                Flowable<Event> eventsFlow = runner.runAsync(userMsg);

                // TODO stringifyContent() will need to be improved... see also AiCommand!
                disposables.add(
                        eventsFlow.subscribe(event -> io.printf("%s\n", event.stringifyContent())));

            } while (true); // TODO Add /quit ?
        } finally {
            disposables.dispose();
        }
    }

    // TODO Equivalent of ExecAgent !!

    // TODO Rewrite Prompter's SystemAgent, as an ADK Agent...

    // TODO Rewrite JLineAgent as an ADK Agent...

    // TODO Rewrite EnolaAgent as an ADK Agent...

    /**
     * EnolaAgent runs Enola's own CLI sub-commands. * / private static class EnolaAgent extends
     * AbstractAgent { private final CommandLine picocliCommandLine; private final Set<String>
     * commands;
     *
     * <p>public EnolaAgent(Switchboard pbx, CommandLine commandLine) { super(
     * tbf.create(Subject.Builder.class, Subject.class) .iri("https://enola.dev") .label("enola")
     * .comment("Enola's own CLI sub-commands.") .build(), pbx); this.picocliCommandLine =
     * commandLine; this.commands = picocliCommandLine.getSubcommands().keySet(); } @Override public
     * void accept(Message message) { var commandLine = message.content(); // split() won't handle
     * quoted arguments correctly, which is fine here (for simple // Builtins), but don't re-use
     * this as-is for other more complex external commands. var splitCommandLine =
     * List.of(commandLine.split("\\s+"));
     *
     * <p>var command = splitCommandLine.get(0); if (!commands.contains(command)) return;
     *
     * <p>// TODO Do something with exit code (just like for exec, where it's also still ignored)
     * int exit = picocliCommandLine.execute(splitCommandLine.toArray(new String[0])); } }
     */
}

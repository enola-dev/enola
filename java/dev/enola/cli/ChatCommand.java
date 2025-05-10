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
package dev.enola.cli;

import com.google.common.collect.ImmutableMap;

import dev.enola.chat.Prompter;
import dev.enola.chat.SystemInOutIO;
import dev.enola.chat.jline.JLineAgent;
import dev.enola.chat.jline.JLineBuiltinShellCommandsProcessor;
import dev.enola.chat.jline.JLineIO;
import dev.enola.common.context.TLC;
import dev.enola.identity.Subjects;
import dev.enola.rdf.io.JavaThingIntoRdfAppendableConverter;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.io.ThingIntoAppendableConverter;
import dev.enola.thing.java.ProxyTBF;

import org.jline.terminal.TerminalBuilder;

import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "chat",
        description = "Chat with Enola, LLMs, Bots, Tools, Agents, and more.")
public class ChatCommand implements Callable<Integer> {

    @Override
    public Integer call() throws IOException {
        try (var ctx = TLC.open()) {
            var tbf = new ProxyTBF(ImmutableThing.FACTORY);
            var subject = new Subjects(tbf).local();
            ctx.push(ThingIntoAppendableConverter.class, new JavaThingIntoRdfAppendableConverter());
            if (System.console() != null) {
                try (var terminal = TerminalBuilder.terminal()) {
                    var consumer = new JLineBuiltinShellCommandsProcessor(terminal);
                    try (var io =
                            new JLineIO(terminal, consumer.completers(), ImmutableMap.of(), true)) {
                        consumer.lineReader(io.lineReader());
                        var chat = new Prompter();
                        chat.addAgent(new JLineAgent(chat.getSwitchboard(), consumer));
                        chat.chat(io, subject, true);
                    }
                }
            } else {
                var io = new SystemInOutIO();
                new Prompter().chat(io, subject, true);
            }
        }
        return 0;
    }
}

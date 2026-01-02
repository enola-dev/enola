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
package dev.enola.chat;

import dev.enola.common.Net;
import dev.enola.common.context.TLC;
import dev.enola.common.linereader.IO;
import dev.enola.common.linereader.SystemInOutIO;
import dev.enola.common.secret.InMemorySecretManager;
import dev.enola.common.secret.SecretManager;
import dev.enola.identity.Subject;
import dev.enola.identity.SubjectContextKey;
import dev.enola.identity.Subjects;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.ProxyTBF;

import java.net.URI;

public class Prompter {

    // TODO See DemoTest and avoid hard-coding Net.portAvailable(11434) but use a constructor

    // TODO MOTD with LLM? ;-)
    static final String MOTD = "Welcome here! Type /help if you're lost.\n\n";

    public static void main(String[] args) {
        var localSubject = new Subjects(new ProxyTBF(ImmutableThing.FACTORY)).local();
        // NB: We're intentionally using SystemInOutIO instead of ConsoleIO (or even JLineIO, like
        // in ChatCommand) here, because System.console() == null when we run this under a Debugger
        // in some IDEs!
        new Prompter(new InMemorySecretManager()).chatLoop(new SystemInOutIO(), localSubject, true);
    }

    private final SecretManager secretManager;
    private final Switchboard sw;

    public Prompter(SecretManager secretManager) {
        this.secretManager = secretManager;
        this.sw = new SimpleInMemorySwitchboard();
    }

    public Switchboard getSwitchboard() {
        return sw;
    }

    public void addAgent(Agent agent) {
        sw.watch(agent);
    }

    public void chatLoop(IO io, Subject user, boolean allowLocalExec) {
        var room = new Room("#Lobby");

        sw.watch(
                message -> {
                    if (!message.from().iri().equals(user.iri()))
                        io.printf("%s> %s\n", message.from().labelOrIRI(), message.content());
                });
        sw.watch(new SystemAgent(sw));
        sw.watch(new EchoAgent(sw));
        sw.watch(new PingPongAgent(sw));
        if (allowLocalExec) sw.watch(new ExecAgent(sw, io));

        // TODO Make this configurable, and support to /invite several of them to chit chat!
        var llmURL = URI.create("http://localhost:11434?type=ollama&model=gemma3:1b");
        if (Net.portAvailable(11434)) sw.watch(new LangChain4jAgent(llmURL, secretManager, sw));

        io.printf(MOTD);
        String input;
        do {
            input = io.readLine("%s in %s> ", user.labelOrIRI(), room.label());
            if (input == null || input.isEmpty()) break;

            var msg = new MessageImpl.Builder();
            msg.content(input);
            msg.to(room);

            try (var ignored = TLC.open().push(SubjectContextKey.USER, user)) {
                sw.post(msg);
            }
        } while (true);
    }
}

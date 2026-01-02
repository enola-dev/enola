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
package dev.enola.chat.sshd;

import com.google.common.collect.ImmutableMap;

import dev.enola.chat.Prompter;
import dev.enola.common.linereader.jline.JLineIO;
import dev.enola.common.secret.InMemorySecretManager;
import dev.enola.identity.Subjects;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.ProxyTBF;

import org.jline.builtins.ssh.Ssh;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.NullCompleter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

class ChatShell {

    private static final Logger LOG = LoggerFactory.getLogger(ChatShell.class);

    public ChatShell(Ssh.ShellParams shellParams) {
        var terminal = shellParams.getTerminal();
        var closer = shellParams.getCloser();

        var pubKey =
                NonLoggingAcceptAllPublickeyAuthenticator.getPublicKey(shellParams.getSession());
        var username = shellParams.getSession().getUsername();

        try {
            var subjects = new Subjects(new ProxyTBF(ImmutableThing.FACTORY));
            var subject = subjects.fromPublicKey(pubKey, username);
            // TODO Create JLine Completer & Tail Tips from Agents, and use instead of NullCompleter
            var io =
                    new JLineIO(
                            shellParams.getEnv(),
                            terminal,
                            new DefaultParser(),
                            NullCompleter.INSTANCE,
                            ImmutableMap.of(),
                            null,
                            false);
            new Prompter(new InMemorySecretManager()).chatLoop(io, subject, false);

        } finally {
            try {
                closer.run();
                terminal.close();
            } catch (IOException e) {
                LOG.error("IOException while closing Terminal", e);
                shellParams.getSession().exceptionCaught(e);
            }
        }
    }
}

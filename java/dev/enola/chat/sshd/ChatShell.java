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
package dev.enola.chat.sshd;

import dev.enola.chat.Demo;
import dev.enola.chat.jline.JLineIO;
import dev.enola.identity.Subjects;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.ProxyTBF;

import org.jline.builtins.ssh.Ssh;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

class ChatShell {

    private static final Logger LOG = LoggerFactory.getLogger(ChatShell.class);

    private final Subjects subjects = new Subjects(new ProxyTBF(ImmutableThing.FACTORY));

    public ChatShell(
            Ssh.ShellParams shellParams,
            NonLoggingAcceptAllPublickeyAuthenticator pubKeyAuthenticator) {
        var terminal = shellParams.getTerminal();
        var closer = shellParams.getCloser();

        var pubKey = pubKeyAuthenticator.getPublicKey(shellParams.getSession());
        var username = shellParams.getSession().getUsername();

        // TODO Read secrets from shellParams.getEnv()

        try {
            var subject = subjects.fromPublicKey(pubKey, username);
            var io = new JLineIO(terminal);
            Demo.chat(io, subject);

        } catch (IOException e) {
            LOG.error("IOException from JLineIO", e);
            shellParams.getSession().exceptionCaught(e);

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

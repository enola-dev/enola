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
package dev.enola.common.linereader.jline;

import dev.enola.chat.AbstractAgent;
import dev.enola.chat.Message;
import dev.enola.chat.Switchboard;
import dev.enola.identity.Subject;

import org.slf4j.Logger;

/** JLineAgent runs the JLine builtin commands. */
public class JLineAgent extends AbstractAgent {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(JLineAgent.class);

    private final JLineBuiltinCommandsProcessor proc;

    public JLineAgent(Switchboard pbx, JLineBuiltinCommandsProcessor proc) {
        super(
                tbf.create(Subject.Builder.class, Subject.class)
                        .iri("https://jline.org")
                        .label("jline")
                        .comment("JLine builtin commands.")
                        .build(),
                pbx);
        this.proc = proc;
    }

    @Override
    public void accept(Message message) {
        var commandLine = message.content();
        try {
            proc.accept(commandLine);
        } catch (Exception e) {
            LOG.error("Exception while processing JLine builtin command: {}", commandLine, e);
        }
    }
}

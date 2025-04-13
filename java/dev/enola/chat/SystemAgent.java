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

import dev.enola.common.Version;
import dev.enola.common.context.TLC;
import dev.enola.identity.Subject;
import dev.enola.identity.SubjectContextKey;
import dev.enola.thing.Thing;

public class SystemAgent extends AbstractAgent {

    public SystemAgent(Switchboard pbx) {
        super(_subject(), pbx);
    }

    @Override
    public void accept(Message message) {
        handle(message, "/help", () -> reply(message, help()));
        handle(message, "/whoami", () -> reply(message, whoami()));

        // TODO /help, /invite, /join, /leave, /quit, /who
    }

    private String help() {
        return "Enola.dev v"
                + Version.get()
                + """
                 -- Commands:
                  /whoami - Show your user details.
                  /help - This help.
                """;
    }

    private String whoami() {
        var user = TLC.get(SubjectContextKey.USER);
        return toString(user);
    }

    private static String toString(Thing thing) {
        // TODO: Print as TTL instead of internal representation!
        return thing.toString();
    }

    private static Subject _subject() {
        return tbf.create(Subject.Builder.class, Subject.class)
                .iri("https://enola.dev/system")
                .label("System")
                .comment("Enola.dev System Agent.")
                .build();
    }
}

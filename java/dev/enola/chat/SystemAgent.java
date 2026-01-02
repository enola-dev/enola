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

import dev.enola.common.Version;
import dev.enola.common.context.TLC;
import dev.enola.identity.Subject;
import dev.enola.identity.SubjectContextKey;
import dev.enola.thing.Thing;
import dev.enola.thing.io.ThingIntoAppendableConverter;
import dev.enola.thing.io.ToStringThingIntoAppendableConverter;

public class SystemAgent extends AbstractAgent {

    private final ThingIntoAppendableConverter thingIntoAppendableConverter;

    public SystemAgent(Switchboard pbx) {
        super(_subject(), pbx);

        thingIntoAppendableConverter =
                TLC.optional(ThingIntoAppendableConverter.class)
                        .orElse(new ToStringThingIntoAppendableConverter());
    }

    @Override
    public void accept(Message message) {
        handle(message, "/help", () -> reply(message, help()));
        handle(message, "/whoami", () -> reply(message, whoami()));

        // TODO /invite, /join, /leave
        // TODO /who, like /whoami but for a room; see https://en.wikipedia.org/wiki/Who_(Unix)

        // No /quit
    }

    private String help() {
        return "Enola.dev v"
                + Version.get()
                + """
                 -- Commands:
                  /whoami - Show your user details.
                  /commands - Available commands.
                  /help - This help.
                 Say "ping" to get a "pong" back.
                 @echo ... echoes your message back to you.
                """;
    }

    private String whoami() {
        var user = TLC.get(SubjectContextKey.USER);
        return toString(user);
    }

    private String toString(Thing thing) {
        // TODO Pass through "text/turtle", so that e.g. UI can syntax highlight, etc.
        var appendable = new StringBuilder();
        thingIntoAppendableConverter.convertIntoOrThrow(thing, appendable);
        return appendable.toString();
    }

    private static Subject _subject() {
        return tbf.create(Subject.Builder.class, Subject.class)
                .iri("https://enola.dev/system")
                .label("System")
                .comment("Enola.dev System Agent.")
                .build();
    }
}

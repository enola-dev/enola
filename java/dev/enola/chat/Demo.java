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

import dev.enola.common.context.TLC;
import dev.enola.identity.Subject;
import dev.enola.identity.SubjectContextKey;
import dev.enola.identity.Subjects;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.ProxyTBF;

public class Demo {

    public static void main(String[] args) {
        chat(IO.CONSOLE, new Subjects(new ProxyTBF(ImmutableThing.FACTORY)).local());
    }

    public static void chat(IO io, Subject user) {
        var room = new Room("#Lobby");

        Switchboard sw = new SimpleInMemorySwitchboard();
        sw.watch(
                message -> {
                    if (!message.from().iri().equals(user.iri()))
                        io.printf("%s> %s\n", message.from().labelOrIRI(), message.content());
                });
        // NOT sw.watch(new SystemAgent(sw, new EchoAgent(sw)));
        sw.watch(new SystemAgent(sw));
        sw.watch(new EchoAgent(sw));

        String input;
        do {
            io.printf("%s in %s> ", user.labelOrIRI(), room.label());
            input = io.readLine();
            if (input == null || input.isEmpty()) break;

            var msg = new MessageImpl.Builder();
            msg.content(input);
            msg.to(room);

            try (var ignored = TLC.open().push(SubjectContextKey.USER, user)) {
                sw.post(msg);
            }
        } while (!"quit".equals(input));
    }
}

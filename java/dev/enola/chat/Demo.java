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
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.ProxyTBF;

public class Demo {

    public static void main(String[] args) {
        chat(IO.CONSOLE);
    }

    static void chat(IO io) {
        var room = new Room("Chat #1");

        var tbf = new ProxyTBF(ImmutableThing.FACTORY);
        var sb = tbf.create(Subject.Builder.class, Subject.class);
        var user = sb.iri("https://example.com/alice").label("Alice").build();

        Switchboard sw = new SimpleInMemorySwitchboard();
        sw.watch(
                message -> {
                    if (!message.from().iri().equals(user.iri()))
                        io.printf("%s> %s\n", message.from().label(), message.content());
                });

        String input;
        do {
            io.printf("%s> ", user.label());
            input = io.readLine();

            var msg = new MessageImpl.Builder();
            msg.content(input);
            msg.to(room);

            try (var ignored = TLC.open().push(SubjectContextKey.USER, user)) {
                sw.post(msg);
            }
        } while (!"quit".equals(input));
    }
}

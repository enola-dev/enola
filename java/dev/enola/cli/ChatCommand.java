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

import dev.enola.chat.DelegatingIO;
import dev.enola.chat.Demo;
import dev.enola.chat.jline.JLineIO;
import dev.enola.common.context.TLC;
import dev.enola.identity.Subjects;
import dev.enola.rdf.io.JavaThingIntoRdfAppendableConverter;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.io.ThingIntoAppendableConverter;
import dev.enola.thing.java.ProxyTBF;

import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "chat",
        description = "Chat with Enola, LLMs, Bots, Tools, Agents, and more.")
public class ChatCommand implements Callable<Integer> {

    @Override
    public Integer call() throws IOException {
        try (var jLineIO = new JLineIO();
                var ctx = TLC.open()) {
            var tbf = new ProxyTBF(ImmutableThing.FACTORY);
            ctx.push(ThingIntoAppendableConverter.class, new JavaThingIntoRdfAppendableConverter());
            Demo.chat(new DelegatingIO(jLineIO), new Subjects(tbf).local(), true);
        }
        return 0;
    }
}

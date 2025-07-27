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

import static com.google.common.base.Strings.isNullOrEmpty;

import com.google.adk.events.Event;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.runner.Runner;
import com.google.genai.types.Content;
import com.google.genai.types.Part;

import dev.enola.cli.AiOptions.WithAgentName;

import io.reactivex.rxjava3.core.Flowable;

import org.jspecify.annotations.Nullable;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.net.URI;

@CommandLine.Command(name = "ai", description = "Run AI, print response (and then exit)")
public class AiCommand extends CommandWithResourceProvider {

    @Spec CommandSpec spec;

    @CommandLine.ArgGroup(exclusive = false)
    @Nullable WithAgentName aiOptions;

    @CommandLine.Option(
            names = {"--inURL"},
            description = "URL to Input (e.g. prompt.txt)")
    @Nullable URI promptURL;

    @CommandLine.Option(
            names = {"--in"},
            description = "Text Input (e.g. 'hello, world')")
    @Nullable String prompt;

    // TODO Input? For consistency, check other commands...

    @Override
    public void run() throws Exception {
        super.run();
        var out = spec.commandLine().getOut();

        var agent = AI.load1(rp, aiOptions);
        var userID = AI.userID();

        // NB: Similarly in Chat2Command & AgentTester... TODO reduce copy/paste?
        Runner runner = new InMemoryRunner(agent);
        var sessionService = runner.sessionService();
        var session = sessionService.createSession(agent.name(), userID).blockingGet();

        if (isNullOrEmpty(prompt))
            if (promptURL != null) prompt = rp.getReadableResource(promptURL).charSource().read();
            else throw new IllegalArgumentException("No prompt; use --in or --inURL");

        Content userMsg = Content.fromParts(Part.fromText(prompt));
        Flowable<Event> eventsFlow = runner.runAsync(userID, session.id(), userMsg);

        eventsFlow.blockingSubscribe(
                event -> {
                    // TODO stringifyContent() will need to be improved... see also Chat2Command!
                    out.println(event.stringifyContent());
                },
                e -> {
                    throw e;
                });

        // TODO Close, like in AgentTester? No need, as we're just about to exit the JVM anyways...
    }
}

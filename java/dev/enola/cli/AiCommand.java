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

import com.google.adk.agents.BaseAgent;
import com.google.adk.events.Event;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.runner.Runner;
import com.google.genai.types.Content;
import com.google.genai.types.Part;

import dev.enola.ai.adk.core.Agents;

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
    @Nullable AiOptions aiOptions;

    @CommandLine.Option(
            names = {"-d", "--default-agent"},
            description = "Agent Name; see https://docs.enola.dev/use/ai/")
    @Nullable String agentName;

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
        var out = spec.commandLine().getOut();

        BaseAgent agent;
        var agents = AI.load(rp, aiOptions);
        var agentsMap = Agents.toMap(agents);
        if (agentsMap.size() == 1) agent = agentsMap.values().iterator().next();
        else agent = agentsMap.get(agentName);
        if (agent == null)
            throw new IllegalArgumentException(
                    "No such agent: " + agentName + "; only: " + agentsMap.keySet());

        // NB: Similarly in AgentTester... TODO reduce copy/paste?
        String userID = System.getProperty("user.name");
        if (userID == null) userID = "CLI";
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
                    // TODO This will need to be improved later...
                    out.println(event.stringifyContent());
                },
                e -> {
                    throw e;
                });

        // TODO Close, like in AgentTester? No need, as we're just about to exit the JVM anyways...
    }
}

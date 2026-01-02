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
package dev.enola.ai.dotagent;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.models.BaseLlm;
import com.google.adk.tools.BaseToolset;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.net.MediaType;
import com.google.genai.types.Schema;

import dev.enola.ai.adk.tool.ToolsetProvider;
import dev.enola.ai.dotprompt.DotPromptLoader;
import dev.enola.ai.iri.Provider;
import dev.enola.common.function.MoreStreams;
import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.object.ObjectWriter;
import dev.enola.common.io.object.jackson.JsonObjectReaderWriter;
import dev.enola.common.io.resource.ResourceProvider;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class AgentsLoader {

    // TODO Detect Agent name/id conflicts between agents in DIFFERENT resources

    private final ToolsetProvider toolsProvider;
    private final Provider<BaseLlm> llmProvider;
    private final BaseLlm defaultLLM;

    private final DotPromptLoader dotPromptLoader;
    // TODO private final DotPrompt2LlmAgentConverter dotPrompt2LlmAgentConverter;
    private final ObjectWriter objectWriter = new JsonObjectReaderWriter();

    private final AgentsModelLoader agentsModelLoader;

    public AgentsLoader(
            ResourceProvider resourceProvider,
            URI defaultLLM,
            Provider<BaseLlm> llmProvider,
            ToolsetProvider toolsProvider) {
        this.dotPromptLoader = new DotPromptLoader(resourceProvider, defaultLLM);
        this.agentsModelLoader = new AgentsModelLoader(resourceProvider);
        this.llmProvider = llmProvider;
        this.defaultLLM = llmProvider.get(defaultLLM);
        this.toolsProvider = toolsProvider;
    }

    public Iterable<BaseAgent> load(Stream<URI> uris) throws IOException {
        var allLoadedAgents = new ArrayList<BaseAgent>();
        // TODO Load in parallel! (Req. using ConcurrentLinkedQueue instead of ArrayList.)
        MoreStreams.forEach(
                uris,
                uri -> {
                    var agents = load(uri);
                    allLoadedAgents.addAll(agents);
                });
        return ImmutableList.copyOf(allLoadedAgents);
    }

    private List<BaseAgent> load(URI uri) throws IOException {
        if (URIs.hasExtension(uri, ".prompt", ".prompt.md")) {
            var loadedDotPrompt = dotPromptLoader.load(uri);
            throw new UnsupportedOperationException(
                    "https://github.com/google/adk-java/issues/288");
        }

        var agentsModel = agentsModelLoader.load(uri);
        var agents = ImmutableList.<BaseAgent>builderWithExpectedSize(agentsModel.agents.size());
        for (var agent : agentsModel.agents) {
            // TODO Support SequentialAgent, ParallelAgent, LoopAgent
            var agentBuilder = new LlmAgent.Builder();

            // TODO Share this code with DotPromptLoader / DotPrompt2LlmAgent !!

            agentBuilder.name(agent.name);
            // TODO Handle agent.variant ...

            agentBuilder.description(agent.description);
            agentBuilder.instruction(agent.instruction);

            if (Strings.isNullOrEmpty(agent.model)) agentBuilder.model(defaultLLM);
            else agentBuilder.model(llmProvider.get(agent.model, uri));

            // TODO Validate both input & output JSON Schemas!!
            if (agent.input != null) agentBuilder.inputSchema(schema(agent.input.schema));
            if (agent.output != null) agentBuilder.outputSchema(schema(agent.output.schema));

            var tools = new ArrayList<BaseToolset>(agent.tools.size());
            for (var toolName : agent.tools) {
                tools.add(toolsProvider.get(toolName, uri));
            }
            agentBuilder.tools(tools);

            agents.add(agentBuilder.build());
        }

        return agents.build();
    }

    private Schema schema(Map<String, Object> jsonSchemaMap) throws IOException {
        var jsonText = objectWriter.write(jsonSchemaMap, MediaType.JSON_UTF_8).get();
        return Schema.fromJson(jsonText);
    }
}

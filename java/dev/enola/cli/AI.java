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

import static java.net.URI.create;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.common.base.Strings;

import dev.enola.ai.adk.core.Agents;
import dev.enola.ai.adk.iri.LlmProviders;
import dev.enola.ai.adk.tool.Tools;
import dev.enola.ai.dotagent.AgentsLoader;
import dev.enola.ai.mcp.McpLoader;
import dev.enola.ai.mcp.cli.McpOptions;
import dev.enola.cli.AiOptions.WithAgentName;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.secret.auto.AutoSecretManager;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class AI {

    private static final String AGENTS_BASE_URI =
            "https://raw.githubusercontent.com/enola-dev/enola/refs/heads/main/test/agents/%s.agent.yaml";

    private static URI createAgentURI(String agentName) {
        return create(AGENTS_BASE_URI.formatted(agentName));
    }

    private static final Map<String, URI> AGENT_NAME_TO_URI;

    static {
        // can't use Map.of() because of 10 limit
        var map = new HashMap<String, URI>();
        map.put("chef-optimist", createAgentURI("chef-optimist"));
        map.put("chefs-opposites-map", createAgentURI("chefs-opposites-map"));
        map.put("chefs-opposites-stream", createAgentURI("chefs-opposites-stream"));
        map.put("clock", createAgentURI("clock"));
        map.put("everything", createAgentURI("everything"));
        map.put("fetch", createAgentURI("fetch"));
        map.put("filesystem", createAgentURI("filesystem"));
        map.put("commit", createAgentURI("git-commit-message"));
        map.put("git", createAgentURI("git"));
        map.put("google", createAgentURI("google"));
        map.put("memory", createAgentURI("memory"));
        map.put("person", createAgentURI("person"));
        // TODO Remove this once we have a weather agent renamed to weather.agent.yaml
        map.put(
                "weather",
                create(
                        "https://raw.githubusercontent.com/enola-dev/enola/refs/heads/main/test/agents/weather.yaml"));
        AGENT_NAME_TO_URI = Map.copyOf(map);
    }

    static Iterable<BaseAgent> load(ResourceProvider rp, @Nullable AiOptions aiOptions)
            throws IOException {
        if (aiOptions == null) aiOptions = new AiOptions();

        if (aiOptions.agentName != null) {
            URI agentURI = AGENT_NAME_TO_URI.get(aiOptions.agentName);
            if (agentURI == null)
                throw new IllegalArgumentException("No such agent: " + aiOptions.agentName);
            aiOptions.agentURIs = List.of(agentURI);
        }

        var modelProvider = new LlmProviders(AutoSecretManager.INSTANCE());

        String defaultModelURI;
        if (isNullOrEmpty(aiOptions.defaultLanguageModelURI))
            defaultModelURI = AiOptions.DEFAULT_MODEL;
        else defaultModelURI = aiOptions.defaultLanguageModelURI;

        if (aiOptions.agentURIs != null && !aiOptions.agentURIs.isEmpty()) {
            var mcpLoader = new McpLoader();
            aiOptions.mcpOptions = McpOptions.handleDefault(aiOptions.mcpOptions);
            aiOptions.mcpOptions.load(mcpLoader, rp);
            var tools = Tools.mcp(mcpLoader.configs(), mcpLoader);
            var agentsLoader = new AgentsLoader(rp, create(defaultModelURI), modelProvider, tools);
            return agentsLoader.load(aiOptions.agentURIs.stream());

        } else {
            var model = modelProvider.get(defaultModelURI, "CLI");
            var agent = LlmAgent.builder().name(model.model()).model(model).build();
            return Set.of(agent);
        }
    }

    static BaseAgent load1(ResourceProvider rp, @Nullable WithAgentName aiOptions)
            throws IOException {
        BaseAgent agent = null;
        var agents = load(rp, aiOptions);
        var agentsMap = Agents.toMap(agents);
        if (agentsMap.size() == 1) agent = agentsMap.values().iterator().next();
        if (agent == null)
            if (aiOptions == null || Strings.isNullOrEmpty(aiOptions.agentName))
                throw new IllegalArgumentException(
                        "Use --default-agent to pick the agent: " + agentsMap.keySet());
            else agent = agentsMap.get(aiOptions.agentName);
        if (agent == null)
            throw new IllegalArgumentException(
                    "No such agent: " + aiOptions.agentName + "; only: " + agentsMap.keySet());
        return agent;
    }

    private AI() {}
}

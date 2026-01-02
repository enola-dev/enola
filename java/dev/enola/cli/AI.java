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
package dev.enola.cli;

import static com.google.common.base.Strings.isNullOrEmpty;

import static java.net.URI.create;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.common.base.Strings;

import dev.enola.ai.adk.core.Agents;
import dev.enola.ai.adk.iri.LlmProviders;
import dev.enola.ai.adk.tool.builtin.BuiltinTools;
import dev.enola.ai.dotagent.AgentsLoader;
import dev.enola.ai.mcp.McpLoader;
import dev.enola.ai.mcp.cli.McpOptions;
import dev.enola.cli.AiOptions.WithAgentName;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.secret.auto.AutoSecretManager;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

final class AI {

    private static final Pattern AGENT_SHORT_NAME = Pattern.compile("^[a-zA-Z0-9\\-]+$");

    static Iterable<BaseAgent> load(ResourceProvider rp, @Nullable AiOptions aiOptions)
            throws IOException {
        var modelProvider = new LlmProviders(AutoSecretManager.INSTANCE());

        String defaultModelURI;
        if (aiOptions == null) aiOptions = new AiOptions();
        if (isNullOrEmpty(aiOptions.defaultLanguageModelURI))
            defaultModelURI = AiOptions.DEFAULT_MODEL;
        else defaultModelURI = aiOptions.defaultLanguageModelURI;

        if (aiOptions.agentURIs != null && !aiOptions.agentURIs.isEmpty()) {
            var mcpLoader = new McpLoader(AutoSecretManager.INSTANCE());
            aiOptions.mcpOptions = McpOptions.handleDefault(aiOptions.mcpOptions);
            aiOptions.mcpOptions.load(mcpLoader, rp);
            var tools = BuiltinTools.builtinAndMcp(mcpLoader);
            var agentsLoader = new AgentsLoader(rp, create(defaultModelURI), modelProvider, tools);
            replaceShortAgentNames(aiOptions.agentURIs);
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

    static void replaceShortAgentNames(List<URI> agentURLs) {
        for (int i = 0; i < agentURLs.size(); i++) {
            var agent = agentURLs.get(i).toString();
            if (AGENT_SHORT_NAME.asMatchPredicate().test(agent)) {
                var realURL =
                        "https://raw.githubusercontent.com/enola-dev/"
                                + agent
                                + "-agent/refs/heads/main/enola.agent.yaml";
                agentURLs.set(i, URI.create(realURL));
            }
        }
    }

    private AI() {}
}

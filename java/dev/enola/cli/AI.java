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
import com.google.adk.agents.LlmAgent;
import com.google.common.base.Strings;

import dev.enola.ai.adk.core.Agents;
import dev.enola.ai.adk.iri.LlmProviders;
import dev.enola.ai.dotagent.AgentsLoader;
import dev.enola.cli.AiOptions.WithAgentName;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.secret.auto.AutoSecretManager;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

final class AI {

    // TODO Create an AdkChatCommand and use this there as well

    static Iterable<BaseAgent> load(ResourceProvider rp, @Nullable AiOptions aiOptions)
            throws IOException {
        var modelProvider = new LlmProviders(AutoSecretManager.INSTANCE());
        var defaultModelURI = AiOptions.DEFAULT_MODEL;
        if (aiOptions != null && !isNullOrEmpty(aiOptions.defaultLanguageModelURI)) {
            defaultModelURI = aiOptions.defaultLanguageModelURI;
        }
        var agentsLoader = new AgentsLoader(rp, URI.create(defaultModelURI), modelProvider);
        if (aiOptions != null && aiOptions.agentURIs != null && !aiOptions.agentURIs.isEmpty()) {
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
        var agents = AI.load(rp, aiOptions);
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

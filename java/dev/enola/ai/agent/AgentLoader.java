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
package dev.enola.ai.agent;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.models.BaseLlm;

import dev.enola.ai.dotprompt.DotPromptLoader;
import dev.enola.ai.iri.Provider;
import dev.enola.common.io.resource.ResourceProvider;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AgentLoader {
    // TODO Rename AgentLoader to DotPromptAgentifier
    // TODO Move AgentLoader (DotPromptAgentifier) to package dotprompt.adk

    private final DotPromptLoader dotPromptLoader;
    private final Provider<BaseLlm> llmProvider;

    public AgentLoader(
            ResourceProvider resourceProvider, URI defaultLLM, Provider<BaseLlm> llmProvider) {
        this.dotPromptLoader = new DotPromptLoader(resourceProvider, defaultLLM);
        this.llmProvider = llmProvider;
    }

    public BaseAgent load(URI uri) throws IOException {
        Object input = null; // TODO ??
        var dotPrompt = dotPromptLoader.load(uri);
        var dot = dotPrompt.frontMatter();
        BaseLlm model = null;
        try {
            model = llmProvider.get(new URI(dot.model));
        } catch (URISyntaxException e) {
            throw new IOException(
                    "Invalid Model URI '"
                            + dot.model
                            + "' (raw input: '"
                            + e.getInput()
                            + "') found in agent definition: "
                            + uri,
                    e);
        }
        // TODO Use description from AgentsModel.Agent
        // TODO Discuss upstream why description is mandatory, and change that?
        var description = "TODO Extend Dotprompt with Description!";
        var instruction = dotPrompt.template().apply(input);
        return LlmAgent.builder()
                .name(dot.name)
                .model(model)
                .description(description)
                .instruction(instruction)
                .build();
    }
}

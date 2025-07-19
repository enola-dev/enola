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
package dev.enola.ai.dotagent;

import com.google.adk.agents.BaseAgent;
import com.google.adk.models.BaseLlm;
import com.google.common.collect.ImmutableMap;

import dev.enola.ai.dotprompt.DotPromptLoader;
import dev.enola.ai.iri.Provider;
import dev.enola.common.function.MoreStreams;
import dev.enola.common.io.resource.ResourceProvider;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.stream.Stream;

public class AgentsLoader {

    private final Provider<BaseLlm> llmProvider;

    private final DotPromptLoader dotPromptLoader;

    // TODO private final DotPrompt2LlmAgentConverter dotPrompt2LlmAgentConverter;

    // TODO AgentsModelLoader

    public AgentsLoader(
            ResourceProvider resourceProvider, URI defaultLLM, Provider<BaseLlm> llmProvider) {
        this.dotPromptLoader = new DotPromptLoader(resourceProvider, defaultLLM);
        this.llmProvider = llmProvider;
    }

    public Map<String, BaseAgent> load(Stream<URI> uris) throws IOException {
        var agents = ImmutableMap.<String, BaseAgent>builder();
        // TODO Load in parallel!
        MoreStreams.forEach(
                uris,
                uri -> {
                    var agent = load(uri);
                    agents.put(agent.name(), agent);
                });
        return agents.build();
    }

    public BaseAgent load(URI uri) throws IOException {
        if (uri.getPath() != null && uri.getPath().endsWith(".prompt")) {
            var loadedDotPrompt = dotPromptLoader.load(uri);
            // TODO loadedDotPrompt.
        }
        // TODO if (uri.getPath().endsWith(".agents.yaml")) {

        throw new IllegalArgumentException("Unknown extension on URI: " + uri);
    }
}

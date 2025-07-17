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
package dev.enola.ai.dotprompt.adk;

import com.google.adk.agents.LlmAgent;
import com.google.adk.models.BaseLlm;
import com.google.common.net.MediaType;
import com.google.genai.types.Schema;

import dev.enola.ai.dotprompt.DotPrompt;
import dev.enola.ai.dotprompt.DotPromptLoader;
import dev.enola.ai.iri.Provider;
import dev.enola.common.io.object.ObjectWriter;
import dev.enola.common.io.object.jackson.JsonObjectReaderWriter;
import dev.enola.common.io.resource.ResourceProvider;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class DotPrompt2LlmAgent {

    // TODO Fix Warning: Invalid config for agent person: outputSchema cannot co-exist with agent
    // transfer configurations. Setting disallowTransferToParent=true and
    // disallowTransferToPeers=true.

    private final DotPromptLoader dotPromptLoader;
    private final Provider<BaseLlm> llmProvider;
    private final ObjectWriter objectWriter = new JsonObjectReaderWriter();

    public DotPrompt2LlmAgent(ResourceProvider rp, URI defaultLLM, Provider<BaseLlm> llmProvider) {
        this.dotPromptLoader = new DotPromptLoader(rp, defaultLLM);
        this.llmProvider = llmProvider;
    }

    public LlmAgent load(URI dotPromptURL) throws IOException {
        var dotPrompt = dotPromptLoader.load(dotPromptURL);
        return convert(dotPrompt);
    }

    // TODO Better just return BaseAgent instead of LlmAgent ?
    public LlmAgent convert(DotPrompt dotPrompt) throws IOException {
        var model = llmProvider.get(dotPrompt.model, dotPrompt.id);

        // TODO Use description from AgentsModel.Agent
        // TODO Discuss upstream why description is mandatory, and change that?
        var description = "TODO Extend Dotprompt with Description!";

        Object input = null; // TODO ??
        var instruction = dotPrompt.template.apply(input);

        var builder =
                LlmAgent.builder()
                        .name(dotPrompt.name)
                        .model(model)
                        .description(description)
                        .instruction(instruction);

        if (dotPrompt.input != null) builder.inputSchema(schema(dotPrompt.input.schema));
        if (dotPrompt.output != null) builder.outputSchema(schema(dotPrompt.output.schema));

        return builder.build();
    }

    private Schema schema(Map<String, Object> jsonSchemaMap) throws IOException {
        var jsonText = objectWriter.write(jsonSchemaMap, MediaType.JSON_UTF_8).get();
        return Schema.fromJson(jsonText);
    }
}

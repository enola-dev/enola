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
package dev.enola.ai.dotprompt.adk;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.models.BaseLlm;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;
import com.google.genai.types.Schema;

import dev.enola.ai.adk.core.UserContentReplacingAgent;
import dev.enola.ai.dotprompt.DotPrompt;
import dev.enola.ai.dotprompt.DotPromptLoader;
import dev.enola.ai.iri.Provider;
import dev.enola.common.function.CheckedFunction;
import dev.enola.common.io.object.ObjectWriter;
import dev.enola.common.io.object.jackson.JsonObjectReaderWriter;
import dev.enola.common.io.resource.ResourceProvider;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class DotPrompt2LlmAgent {
    // TODO Rename DotPrompt2LlmAgent to DotPromptAgentLoader
    // TODO Split DotPromptAgentLoader from DotPromptAgentConverter implements Converter<DotPrompt,
    // DotPromptAgent>

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

    public BaseAgent load(URI dotPromptURL) throws IOException {
        var dotPrompt = dotPromptLoader.load(dotPromptURL);
        return convert(dotPrompt);
    }

    public BaseAgent convert(DotPrompt dotPrompt) throws IOException {
        // TODO Share this code with ADK AgentsLoader !!
        //   Actually, DotPrompt may soon be completely abandoned, in favour of DotAgent ..

        var model = llmProvider.get(dotPrompt.model, dotPrompt.id);
        var llmAgentBuilder = LlmAgent.builder().name(dotPrompt.name).model(model);

        // Ask about this on https://github.com/google/dotprompt/issues ...
        //   TODO llmAgentBuilder.instruction( ? )
        //   TODO llmAgentBuilder.globalInstruction( ? )

        // TODO Validate both input & output JSON Schemas!!
        if (dotPrompt.input != null) llmAgentBuilder.inputSchema(schema(dotPrompt.input.schema));
        if (dotPrompt.output != null) llmAgentBuilder.outputSchema(schema(dotPrompt.output.schema));

        var llmAgent = llmAgentBuilder.build();
        CheckedFunction<String, String, IOException> replacer =
                text -> {
                    var templateInput = ImmutableMap.<String, Object>builder();
                    templateInput.put("text", text);
                    // TODO Add more context variables than only "text", see:
                    // https://google.github.io/dotprompt/reference/template/#context-variables
                    return dotPrompt.template.apply(templateInput.build());
                };
        return new UserContentReplacingAgent(
                dotPrompt.name, dotPrompt.id.toString(), replacer, llmAgent);
    }

    private Schema schema(Map<String, Object> jsonSchemaMap) throws IOException {
        var jsonText = objectWriter.write(jsonSchemaMap, MediaType.JSON_UTF_8).get();
        return Schema.fromJson(jsonText);
    }
}

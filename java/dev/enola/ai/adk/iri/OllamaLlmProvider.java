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
package dev.enola.ai.adk.iri;

import com.google.adk.models.BaseLlm;
import com.google.adk.models.langchain4j.LangChain4j;

import dev.enola.ai.iri.ModelConfig;
import dev.enola.ai.iri.OllamaModelProvider;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;

public class OllamaLlmProvider extends OllamaModelProvider<BaseLlm> {

    @Override
    protected BaseLlm create(String baseURL, String modelName, ModelConfig config) {
        var sync =
                OllamaChatModel.builder()
                        .logRequests(true)
                        .logResponses(true)
                        .baseUrl(baseURL)
                        .modelName(modelName)
                        .build();

        var streaming =
                OllamaStreamingChatModel.builder()
                        .logRequests(true)
                        .logResponses(true)
                        .baseUrl(baseURL)
                        .modelName(modelName)
                        .build();

        var langChain4jLlm = new LangChain4j(sync, streaming, modelName);
        return WrappedBaseLlm.wrap(langChain4jLlm, config);
    }
}

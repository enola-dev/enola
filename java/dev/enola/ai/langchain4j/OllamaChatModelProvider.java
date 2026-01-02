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
package dev.enola.ai.langchain4j;

import dev.enola.ai.iri.ModelConfig;
import dev.enola.ai.iri.OllamaModelProvider;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;

public class OllamaChatModelProvider extends OllamaModelProvider<StreamingChatModel> {

    @Override
    protected StreamingChatModel create(String baseURL, String modelName, ModelConfig config) {
        var builder =
                OllamaStreamingChatModel.builder()
                        .logRequests(true)
                        .logResponses(true)
                        .baseUrl(baseURL)
                        .modelName(modelName);

        config.topP().ifPresent(builder::topP);
        config.topK().map(Double::intValue).ifPresent(builder::topK);
        config.temperature().ifPresent(builder::temperature);
        // TODO How? config.maxOutputTokens().ifPresent(builder::maxOutputTokens);
        return builder.build();
    }
}

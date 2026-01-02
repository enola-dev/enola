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

import dev.enola.ai.iri.AnthropicModelProvider;
import dev.enola.ai.iri.ModelConfig;
import dev.enola.common.secret.SecretManager;
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;

public class AnthropicChatModelProvider extends AnthropicModelProvider<StreamingChatModel> {

    public AnthropicChatModelProvider(SecretManager secretManager) {
        super(secretManager);
    }

    @Override
    protected StreamingChatModel create(String apiKey, String modelName, ModelConfig config) {
        var builder = AnthropicStreamingChatModel.builder();
        builder.modelName(modelName);
        builder.apiKey(apiKey);
        config.topP().ifPresent(builder::topP);
        config.topK().map(Double::intValue).ifPresent(builder::topK);
        config.temperature().ifPresent(builder::temperature);
        config.maxOutputTokens().ifPresent(builder::maxTokens);
        return builder.build();
    }
}

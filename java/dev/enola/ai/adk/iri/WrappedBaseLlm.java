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

import com.google.adk.models.*;
import com.google.genai.types.GenerateContentConfig;

import dev.enola.ai.iri.ModelConfig;

import io.reactivex.rxjava3.core.Flowable;

class WrappedBaseLlm extends BaseLlm {

    private final BaseLlm delegate;
    private final ModelConfig config;

    private WrappedBaseLlm(BaseLlm delegate, ModelConfig config) {
        super(delegate.model());
        this.delegate = delegate;
        this.config = config;
    }

    static BaseLlm wrap(BaseLlm delegate, ModelConfig config) {
        if (config.isEmpty()) return delegate;
        else return new WrappedBaseLlm(delegate, config);
    }

    @Override
    public Flowable<LlmResponse> generateContent(LlmRequest llmRequest, boolean stream) {
        if (config.isEmpty()) return delegate.generateContent(llmRequest, stream);
        return delegate.generateContent(reconfigure(llmRequest), stream);
    }

    @Override
    public BaseLlmConnection connect(LlmRequest llmRequest) {
        if (config.isEmpty()) return delegate.connect(llmRequest);
        return delegate.connect(reconfigure(llmRequest));
    }

    private LlmRequest reconfigure(LlmRequest llmRequest) {
        var llmRequestBuilder = llmRequest.toBuilder();
        var liveConnectConfigBuilder = llmRequest.liveConnectConfig().toBuilder();

        GenerateContentConfig.Builder generateContentConfigBuilder;
        if (llmRequest.config().isPresent()) {
            generateContentConfigBuilder = llmRequest.config().get().toBuilder();
        } else {
            generateContentConfigBuilder = GenerateContentConfig.builder();
        }

        config.topP()
                .map(Double::floatValue)
                .ifPresent(
                        v -> {
                            liveConnectConfigBuilder.topP(v);
                            generateContentConfigBuilder.topP(v);
                        });
        config.topK()
                .map(Double::floatValue)
                .ifPresent(
                        v -> {
                            liveConnectConfigBuilder.topK(v);
                            generateContentConfigBuilder.topK(v);
                        });
        config.temperature()
                .map(Double::floatValue)
                .ifPresent(
                        v -> {
                            liveConnectConfigBuilder.temperature(v);
                            generateContentConfigBuilder.temperature(v);
                        });
        config.maxOutputTokens()
                .ifPresent(
                        v -> {
                            liveConnectConfigBuilder.maxOutputTokens(v);
                            generateContentConfigBuilder.maxOutputTokens(v);
                        });

        llmRequestBuilder.liveConnectConfig(liveConnectConfigBuilder.build());
        llmRequestBuilder.config(generateContentConfigBuilder.build());

        return llmRequestBuilder.build();
    }
}

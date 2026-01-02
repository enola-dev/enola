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
import com.google.adk.models.Gemini;

import dev.enola.ai.iri.GoogleModelProvider;
import dev.enola.ai.iri.ModelConfig;
import dev.enola.ai.iri.Provider;
import dev.enola.common.secret.Secret;
import dev.enola.common.secret.SecretManager;

/**
 * GoogleBaseLlmProvider is a {@link Provider} of an ADK {@link BaseLlm} based on the <a
 * href="https://docs.enola.dev/specs/aiuri#google-ai">Enola.dev Google AI URI spec</a>.
 */
public class GoogleLlmProvider extends GoogleModelProvider<BaseLlm> {

    public GoogleLlmProvider(SecretManager secretManager) {
        super(secretManager);
    }

    @Override
    protected BaseLlm create(Secret apiKey, String modelName, ModelConfig config) {
        var gemini = Gemini.builder().apiKey(apiKey.map(String::new)).modelName(modelName).build();
        return WrappedBaseLlm.wrap(gemini, config);
    }
}

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
package dev.enola.ai.adk.iri;

import com.google.adk.models.BaseLlm;

import dev.enola.ai.iri.AnthropicModelProvider;
import dev.enola.ai.iri.Provider;

/**
 * AnthropicBaseLlmProvider is a {@link Provider} of an ADK {@link BaseLlm} based on the <a
 * href="https://docs.enola.dev/specs/aiuri#anthropic">Enola.dev Anthropic AI URI spec</a>.
 */
public abstract class AnthropicLlmProvider extends AnthropicModelProvider<BaseLlm> {

    // TODO ...
}

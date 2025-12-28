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

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.ai.iri.AnthropicModelProvider.ANTHROPIC_API_KEY_SECRET_NAME;
import static dev.enola.ai.iri.GoogleModelProvider.GOOGLE_AI_API_KEY_SECRET_NAME;

import com.google.adk.models.BaseLlm;
import com.google.adk.models.Gemini;

import dev.enola.ai.adk.test.ModelTester;
import dev.enola.ai.iri.AnthropicModelProvider;
import dev.enola.ai.iri.EchoModelProvider;
import dev.enola.ai.iri.GoogleModelProvider;
import dev.enola.ai.iri.MockModelProvider;
import dev.enola.ai.iri.Provider;
import dev.enola.common.Net;
import dev.enola.common.secret.InMemorySecretManager;
import dev.enola.common.secret.SecretManager;
import dev.enola.common.secret.auto.TestSecretManager;

import org.junit.Test;

import java.io.IOException;

public class LlmProvidersTest {
    // See also the similarly structured ChatModelProviderTest

    @Test
    public void geminiUnitTest() {
        var secretManager = new InMemorySecretManager(GOOGLE_AI_API_KEY_SECRET_NAME, "...");
        var provider = new LlmProviders(secretManager);
        var uri = provider.uriExamples().iterator().next();
        assertThat(provider.get(uri)).isInstanceOf(Gemini.class);
    }

    SecretManager sm = new TestSecretManager();
    Provider<BaseLlm> p = new LlmProviders(sm);

    @Test
    public void mock() {
        var model = p.get(MockModelProvider.EXAMPLE_URI);
        new ModelTester(model).assertTextResponseContains("What up?", "hello");
    }

    @Test
    public void echo() {
        var model = p.get(EchoModelProvider.ECHO_URI);
        new ModelTester(model).assertTextResponseContains("What up?", "What up?");
    }

    @Test
    public void geminiFlashLiteIntegrationTest() throws IOException {
        if (sm.getOptional(GOOGLE_AI_API_KEY_SECRET_NAME).isPresent())
            check(p.get(GoogleModelProvider.FLASH_LITE));
    }

    @Test
    public void ollamaGemma31bIntegrationTest() {
        if (Net.portAvailable(11434)) check(p.get(OllamaLlmProvider.GEMMA3_1B));
    }

    @Test
    public void claudeIntegrationTest() throws IOException {
        if (sm.getOptional(ANTHROPIC_API_KEY_SECRET_NAME).isPresent())
            check(p.get(AnthropicModelProvider.CLAUDE_HAIKU_3));
    }

    private void check(BaseLlm model) {
        new ModelTester(model)
                .assertTextResponseContains(
                        "List top 3 cities in Switzerland", "Zurich", "Bern", "Geneva");
    }
}

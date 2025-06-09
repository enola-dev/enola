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

import static dev.enola.ai.iri.GoogleModelProvider.GOOGLE_AI_API_KEY_SECRET_NAME;

import com.google.adk.models.BaseLlm;
import com.google.adk.models.Gemini;

import dev.enola.ai.adk.test.ModelTester;
import dev.enola.ai.iri.GoogleModelProvider;
import dev.enola.ai.iri.Provider;
import dev.enola.common.secret.InMemorySecretManager;
import dev.enola.common.secret.SecretManager;
import dev.enola.common.secret.auto.TestSecretManager;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class LlmProvidersTest {
    // See also the similarly structured ChatModelProviderTest

    @Test
    public void mock() {
        var provider = new LlmProviders(new InMemorySecretManager());
        var model = provider.get(URI.create("mocklm:hello"));
        new ModelTester(model).assertTextResponseContains("What up?", "hello");
    }

    @Test
    public void geminiUnitTest() {
        var secretManager = new InMemorySecretManager(GOOGLE_AI_API_KEY_SECRET_NAME, "...");
        var provider = new LlmProviders(secretManager);
        var exampleURI = provider.uriExamples().iterator().next();
        assertThat(provider.get(exampleURI)).isInstanceOf(Gemini.class);
    }

    @Test
    public void geminiIntegrationTest() throws IOException {
        SecretManager secretManager = new TestSecretManager();
        if (!secretManager.getOptional(GOOGLE_AI_API_KEY_SECRET_NAME).isPresent()) return;

        Provider<BaseLlm> p = new LlmProviders(secretManager);
        new ModelTester(p.get(GoogleModelProvider.FLASH))
                .assertTextResponseContains("List top 3 cites in Switzerland", "Zurich");
    }
}

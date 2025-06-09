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
import com.google.adk.models.LlmRequest;
import com.google.genai.types.Content;
import com.google.genai.types.Part;

import dev.enola.ai.iri.GoogleModelProvider;
import dev.enola.ai.iri.Provider;
import dev.enola.common.secret.SecretManager;
import dev.enola.common.secret.auto.TestSecretManager;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class BaseLlmProvidersTest {
    // See also the similarly structured ChatModelProviderTest

    SecretManager secretManager = new TestSecretManager();
    Provider<BaseLlm> p = new BaseLlmProviders(secretManager);

    void checkGenerateContent(BaseLlm llm) {
        var content = Content.fromParts(Part.fromText("List top 3 cites in Switzerland"));
        var request = LlmRequest.builder().contents(List.of(content)).build();
        var response = llm.generateContent(request, false).blockingFirst();
        assertThat(response.content().get().text()).contains("Zurich");
    }

    @Test
    public void gemini() throws IOException {
        if (!secretManager.getOptional(GOOGLE_AI_API_KEY_SECRET_NAME).isPresent()) return;
        checkGenerateContent(p.get(GoogleModelProvider.FLASH));
    }
}

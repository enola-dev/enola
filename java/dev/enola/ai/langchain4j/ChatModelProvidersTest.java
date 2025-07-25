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
package dev.enola.ai.langchain4j;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.ai.langchain4j.GoogleChatModelProvider.GOOGLE_AI_API_KEY_SECRET_NAME;

import dev.enola.ai.iri.GoogleModelProvider;
import dev.enola.ai.iri.Provider;
import dev.enola.common.Net;
import dev.enola.common.secret.SecretManager;
import dev.enola.common.secret.auto.TestSecretManager;
import dev.langchain4j.model.chat.StreamingChatModel;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class ChatModelProvidersTest {
    // See also the similarly structured BaseLlmProvidersTest

    SecretManager secretManager = new TestSecretManager();
    Provider<StreamingChatModel> p = new ChatModelProviders(secretManager);

    void check(StreamingChatModel model) {
        var answer = new TestStreamingChatResponseHandler();
        model.chat("List top 3 cites in Switzerland", answer);
        assertThat(answer.awaitChatResponse().aiMessage().text()).contains("Zurich");
    }

    @Test(expected = IllegalArgumentException.class)
    public void bad() {
        p.get(URI.create("http://www.google.com"));
    }

    @Test
    public void mock() {
        check(p.get(URI.create("mocklm:Zurich")));
    }

    @Test
    public void gemmaOnOllama() {
        if (!Net.portAvailable(11434)) return;
        var model = p.get(URI.create("http://localhost:11434?type=ollama&model=gemma3:1b"));
        check(model);
    }

    @Test
    public void gemmaOnGCP() throws IOException {
        if (secretManager.getOptional(GOOGLE_AI_API_KEY_SECRET_NAME).isPresent())
            check(p.get(URI.create("google://?model=gemma-3-1b-it")));
    }

    @Test
    public void gemini() throws IOException {
        if (secretManager.getOptional(GOOGLE_AI_API_KEY_SECRET_NAME).isPresent())
            check(p.get(GoogleModelProvider.FLASH));
    }
}

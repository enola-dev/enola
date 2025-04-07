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

import dev.enola.data.Provider;
import dev.langchain4j.model.chat.ChatLanguageModel;

import org.junit.Test;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;

public class ChatLanguageModelProviderTest {

    // TODO Switch from ChatLanguageModel to StreamingChatLanguageModel

    Provider<URI, ChatLanguageModel> provider = new ChatLanguageModelProvider();

    void check(ChatLanguageModel model) {
        String answer = model.chat("List top 3 cites in Switzerland");
        assertThat(answer).contains("Zurich");
    }

    @Test(expected = IllegalArgumentException.class)
    public void bad() {
        provider.get(URI.create("http://www.google.com"));
    }

    @Test
    public void fake() {
        check(provider.get(URI.create("llm:fake:Zurich")));
    }

    @Test
    public void ollama() {
        if (!portAvailable(11434)) return;

        check(provider.get(URI.create("llm:ollama:http://localhost:11434:gemma3:1b")));
    }

    private boolean portAvailable(int port) {
        try (var ignored = new Socket("localhost", port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}

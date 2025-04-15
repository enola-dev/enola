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

import dev.enola.common.Net;
import dev.enola.data.Provider;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;

import org.junit.Test;

import java.net.URI;

public class ChatLanguageModelProviderTest {

    Provider<URI, StreamingChatLanguageModel> provider = new ChatLanguageModelProvider();

    void check(StreamingChatLanguageModel model) {
        var answer = new TestStreamingChatResponseHandler();
        model.chat("List top 3 cites in Switzerland", answer);
        assertThat(answer.awaitChatResponse().aiMessage().text()).contains("Zurich");
    }

    @Test(expected = IllegalArgumentException.class)
    public void bad() {
        provider.get(URI.create("http://www.google.com"));
    }

    @Test
    public void fake() {
        check(provider.get(URI.create("mockllm:Zurich")));
    }

    @Test
    public void ollama() {
        if (!Net.portAvailable(11434)) return;

        check(provider.get(URI.create("http://localhost:11434?type=ollama&model=gemma3:1b")));
    }
}

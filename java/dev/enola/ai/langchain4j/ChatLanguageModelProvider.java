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

import dev.enola.data.Provider;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.io.UncheckedIOException;
import java.net.URI;

public class ChatLanguageModelProvider implements Provider<URI, ChatLanguageModel> {

    // TODO Switch from ChatLanguageModel to StreamingChatLanguageModel

    // TODO Support ?topP / topK, temperature, seed etc. as query parameters!

    @Override
    public ChatLanguageModel get(URI uri) throws IllegalArgumentException, UncheckedIOException {
        if (!"llm".equals(uri.getScheme())) throw new IllegalArgumentException(uri.toString());
        var schemeSpecificPart = uri.getSchemeSpecificPart();

        if (schemeSpecificPart.startsWith("fake:")) {
            var reply = schemeSpecificPart.substring("fake:".length());
            return new TestChatLanguageModel(reply);
        }

        if (schemeSpecificPart.startsWith("ollama:")) {
            var tail = schemeSpecificPart.substring("ollama:".length());
            var split = tail.split(":");
            var baseURL = split[0] + ":" + split[1] + ":" + split[2];
            var model = split[3] + ":" + split[4];
            return OllamaChatModel.builder().baseUrl(baseURL).modelName(model).build();
        }

        throw new IllegalArgumentException(schemeSpecificPart);
    }
}

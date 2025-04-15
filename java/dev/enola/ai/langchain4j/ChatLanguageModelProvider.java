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

import dev.enola.common.io.iri.URIs;
import dev.enola.data.Provider;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;

import java.io.UncheckedIOException;
import java.net.URI;

public class ChatLanguageModelProvider implements Provider<URI, StreamingChatLanguageModel> {

    // TODO CachingChatLanguageModelProvider

    // TODO Support Vertex AI Cloud LLM API

    // TODO Support ?topP / topK, temperature, seed etc. as query parameters!
    //   Or is there no need to set that as default, because it will only be set on requests?

    @Override
    public StreamingChatLanguageModel get(URI uri)
            throws IllegalArgumentException, UncheckedIOException {
        if ("mockllm".equalsIgnoreCase(uri.getScheme())) {
            var reply = uri.getSchemeSpecificPart();
            return new TestChatLanguageModel(reply);
        }

        var queryMap = URIs.getQueryMap(uri);
        if ("ollama".equalsIgnoreCase(queryMap.get("type"))) {
            var baseURL = uri.getScheme() + "://" + uri.getAuthority();
            var model = queryMap.get("model");
            // TODO ollamaAPI.pullModel(model);
            return OllamaStreamingChatModel.builder()
                    .logRequests(true)
                    .logResponses(true)
                    .baseUrl(baseURL)
                    .modelName(model)
                    .build();
        }

        throw new IllegalArgumentException(uri.toString());
    }
}

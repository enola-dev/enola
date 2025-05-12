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

import com.google.common.base.Strings;

import dev.enola.common.io.iri.URIs;
import dev.enola.data.Provider;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiStreamingChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;

import java.io.UncheckedIOException;
import java.net.URI;

public class ChatLanguageModelProvider implements Provider<URI, StreamingChatModel> {

    // TODO Document LLM URI Spec (and link from here)

    // TODO CachingChatLanguageModelProvider

    // TODO Support ?topP / topK, temperature, seed etc. as query parameters!
    //   Or is there no need to set that as default, because it will only be set on requests?

    public static final String GOOGLE_AI_API_KEY_SECRET_NAME = "GOOGLE_AI_API_KEY";

    @Override
    public StreamingChatModel get(URI uri) throws IllegalArgumentException, UncheckedIOException {
        var queryMap = URIs.getQueryMap(uri);

        if ("mockllm".equalsIgnoreCase(uri.getScheme())) {
            var reply = uri.getSchemeSpecificPart();
            return new TestChatLanguageModel(reply);
        }

        if ("google".equalsIgnoreCase(uri.getScheme())) {
            var model = queryMap.get("model");
            if (Strings.isNullOrEmpty(model.trim()))
                throw new IllegalArgumentException(
                        "google://?model=$MODEL, see https://ai.google.dev/gemini-api/docs/models");

            var apiKey = "..."; // TODO secretManager.getSecret(GOOGLE_AI_API_KEY_SECRET_NAME)

            return GoogleAiGeminiStreamingChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(model.trim())
                    .logRequestsAndResponses(true)
                    .build();
        }

        if ("ollama".equalsIgnoreCase(queryMap.get("type"))) {
            var model = queryMap.get("model");
            if (Strings.isNullOrEmpty(model.trim()))
                throw new IllegalArgumentException(
                        "http://localhost:11434?type=ollama&model=$MODEL, see https://ollama.com/search");

            var baseURL = uri.getScheme() + "://" + uri.getAuthority();
            // TODO ollamaAPI.pullModel(model);
            // TODO ollamaAPI ping()

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

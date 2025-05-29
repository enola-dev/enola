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
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class OllamaChatModelProvider implements ChatModelProvider {

    @Override
    public String name() {
        return "Ollama 🦙";
    }

    @Override
    public String uriTemplate() {
        return "http://{HOST}:{PORT}?type=ollama&model={MODEL}";
    }

    @Override
    public URI uriExample() {
        return URI.create("http://localhost:11434?type=ollama&model=gemma3:1b");
    }

    @Override
    public Optional<StreamingChatModel> getOptional(URI uri)
            throws IllegalArgumentException, UncheckedIOException {
        var queryMap = URIs.getQueryMap(uri);
        if (!"ollama".equalsIgnoreCase(queryMap.get("type"))) return Optional.empty();

        var model = queryMap.get("model");
        if (Strings.isNullOrEmpty(model.trim()))
            throw new IllegalArgumentException(
                    "http://localhost:11434?type=ollama&model=$MODEL, see"
                            + " https://ollama.com/search");

        var baseURL = uri.getScheme() + "://" + uri.getAuthority();
        var ollamaAPI = new OllamaAPI(baseURL);
        ollamaAPI.setVerbose(true);
        if (!ollamaAPI.ping())
            throw new IllegalStateException("Failed to ping Ollama at " + ollamaAPI);
        try {
            ollamaAPI.pullModel(model);
        } catch (OllamaBaseException | IOException | URISyntaxException e) {
            throw new IllegalArgumentException(model, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupt status
            throw new IllegalArgumentException(model, e);
        }

        return Optional.of(
                OllamaStreamingChatModel.builder()
                        .logRequests(true)
                        .logResponses(true)
                        .baseUrl(baseURL)
                        .modelName(model)
                        .build());
    }
}

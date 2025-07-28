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
package dev.enola.ai.iri;

import com.google.common.base.Strings;

import dev.enola.common.io.iri.URIs;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

public abstract class OllamaModelProvider<T> implements Provider<T> {

    public static final URI GEMMA3_1B =
            URI.create("http://localhost:11434?type=ollama&model=gemma3:1b");

    @Override
    public String name() {
        return "Ollama 🦙";
    }

    @Override
    public Iterable<String> uriTemplates() {
        return List.of("http://{HOST}:{PORT}?type=ollama&model={MODEL}");
    }

    @Override
    public Iterable<URI> uriExamples() {
        return List.of(GEMMA3_1B);
    }

    @Override
    public final Optional<T> optional(URI uri)
            throws IllegalArgumentException, UncheckedIOException {
        var queryMap = URIs.getQueryMap(uri);
        if (!"ollama".equalsIgnoreCase(queryMap.get("type"))) return Optional.empty();

        var model = queryMap.get("model");
        if (Strings.isNullOrEmpty(model))
            throw new IllegalArgumentException(
                    uri
                            + "; use e.g. http://localhost:11434?type=ollama&model=$MODEL, see"
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

        return Optional.of(create(baseURL, model));
    }

    protected abstract T create(String baseURL, String modelName);
}

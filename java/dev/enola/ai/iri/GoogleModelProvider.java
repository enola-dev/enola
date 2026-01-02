/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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

import dev.enola.common.io.iri.URIs;
import dev.enola.common.secret.Secret;
import dev.enola.common.secret.SecretManager;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Base class for <a href="https://docs.enola.dev/specs/aiuri#google-ai">Enola.dev Google AI URI</a>
 * implementations.
 *
 * @param <T> The class specific to the implementing technical framework.
 */
public abstract class GoogleModelProvider<T> implements Provider<T> {

    public static final String GOOGLE_AI_API_KEY_SECRET_NAME = "GOOGLE_AI_API_KEY";

    private static final String SCHEME = "google";
    public static final URI GEMMA3_1B = URI.create(SCHEME + "://?model=gemma-3-1b-it");
    public static final URI FLASH_LITE = URI.create(SCHEME + "://?model=gemini-2.5-flash-lite");
    public static final URI FLASH = URI.create(SCHEME + "://?model=gemini-2.5-flash");
    public static final URI PRO = URI.create(SCHEME + "://?model=gemini-2.5-pro");

    protected final SecretManager secretManager;

    protected GoogleModelProvider(SecretManager secretManager) {
        this.secretManager = secretManager;
    }

    @Override
    public String name() {
        return "Google AI 🔮";
    }

    @Override
    public String docURL() {
        return "https://docs.enola.dev/specs/aiuri/#google-ai";
    }

    @Override
    public Iterable<String> uriTemplates() {
        return List.of(SCHEME + "://?model={MODEL}");
    }

    @Override
    public Iterable<URI> uriExamples() {
        return List.of(FLASH);
    }

    @Override
    public final Optional<T> optional(URI uri)
            throws IllegalArgumentException, UncheckedIOException {
        if (!SCHEME.equalsIgnoreCase(uri.getScheme())) return Optional.empty();
        var queryMap = URIs.getQueryMap(uri);
        var model = Providers.model(uri, queryMap, this);
        var config = ModelConfig.from(queryMap);

        try (var apiKey = secretManager.get(GOOGLE_AI_API_KEY_SECRET_NAME)) {
            return Optional.of(create(apiKey, model, config));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected abstract T create(Secret apiKey, String modelName, ModelConfig config);
}

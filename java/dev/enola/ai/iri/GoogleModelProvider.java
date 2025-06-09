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
import dev.enola.common.secret.Secret;
import dev.enola.common.secret.SecretManager;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Optional;

/**
 * Base class for <a href="https://docs.enola.dev/specs/aiuri/#google-ai">Enola.dev Google AI
 * URI</a> implementations.
 *
 * @param <T> The class specific to the implementing technical framework.
 */
public abstract class GoogleModelProvider<T> implements Provider<T> {

    public static final String GOOGLE_AI_API_KEY_SECRET_NAME = "GOOGLE_AI_API_KEY";

    protected final SecretManager secretManager;

    public GoogleModelProvider(SecretManager secretManager) {
        this.secretManager = secretManager;
    }

    @Override
    public String name() {
        return "Google AI 🔮";
    }

    @Override
    public String uriTemplate() {
        return "google://?model={MODEL}";
    }

    @Override
    public URI uriExample() {
        return URI.create("google://?model=gemini-2.5-flash-preview-04-17");
    }

    @Override
    public final Optional<T> optional(URI uri)
            throws IllegalArgumentException, UncheckedIOException {
        if (!"google".equalsIgnoreCase(uri.getScheme())) return Optional.empty();
        var queryMap = URIs.getQueryMap(uri);
        var model = queryMap.get("model");
        if (Strings.isNullOrEmpty(model.trim()))
            throw new IllegalArgumentException(
                    "google://?model=$MODEL, see https://ai.google.dev/gemini-api/docs/models");

        try (var apiKey = secretManager.get(GOOGLE_AI_API_KEY_SECRET_NAME)) {
            return Optional.of(create(apiKey, model.trim()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected abstract T create(Secret apiKey, String modelName);
}

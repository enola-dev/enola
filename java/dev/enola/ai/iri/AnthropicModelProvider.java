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
import dev.enola.common.secret.SecretManager;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Base class for <a href="https://docs.enola.dev/specs/aiuri#anthropic">Enola.dev Anthropic AI
 * URI</a> implementations.
 *
 * @param <T> The class specific to the implementing technical framework.
 */
public abstract class AnthropicModelProvider<T> implements Provider<T> {

    // See https://github.com/anthropics/anthropic-sdk-java
    // and https://docs.anthropic.com/en/api/client-sdks#java

    // TODO Test and document how to use Claude/s on GCP Vertex AI, and AWS Bedrock,
    //   instead of via Anthropic's own API? .baseUrl() ? Or .backend(VertexBackend.builder()) ?
    // See also https://cloud.google.com/vertex-ai/generative-ai/docs/partner-models/claude

    public static final String ANTHROPIC_API_KEY_SECRET_NAME = "ANTHROPIC_API_KEY";

    private static final String SCHEME = "claude";

    // NB: The default maxOutputTokens of 8192 (in AnthropicLlmProvider) fails with Haiku 3;
    //     as its max. is 4096; but for initial simple tests, let's cap it at even just 1024:
    public static final URI CLAUDE_HAIKU_3 =
            URI.create(SCHEME + "://?model=claude-3-haiku-20240307&maxOutputTokens=1024");

    protected final SecretManager secretManager;

    protected AnthropicModelProvider(SecretManager secretManager) {
        this.secretManager = secretManager;
    }

    @Override
    public String name() {
        return "Anthropic's Claudes";
    }

    @Override
    public String docURL() {
        return "https://docs.enola.dev/specs/aiuri/#anthropic-claude";
    }

    @Override
    public Iterable<String> uriTemplates() {
        return List.of(SCHEME + "://?model={MODEL}");
    }

    @Override
    public Iterable<URI> uriExamples() {
        return List.of(CLAUDE_HAIKU_3);
    }

    @Override
    public final Optional<T> optional(URI uri) {
        if (!SCHEME.equalsIgnoreCase(uri.getScheme())) return Optional.empty();
        var queryMap = URIs.getQueryMap(uri);
        var model = Providers.model(uri, queryMap, this);
        var config = ModelConfig.from(queryMap);

        try (var apiKeySecret = secretManager.get(ANTHROPIC_API_KEY_SECRET_NAME)) {
            var apiKey = apiKeySecret.map(String::new);
            return Optional.of(create(apiKey, model, config));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected abstract T create(String apiKey, String modelName, ModelConfig config);
}

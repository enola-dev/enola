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
package dev.enola.ai.adk.iri;

import com.google.adk.models.BaseLlm;

import dev.enola.ai.iri.DelegatingProvider;
import dev.enola.common.secret.auto.TestSecretManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Optional;

public class TestsLlmProvider extends DelegatingProvider<BaseLlm> {

    private static final Logger LOG = LoggerFactory.getLogger(TestsLlmProvider.class);

    public TestsLlmProvider() {
        super(new LlmProviders(new TestSecretManager()));
    }

    @Override
    public Optional<BaseLlm> optional(URI uri) {
        try {
            return delegate.optional(uri);
        } catch (Exception e) {
            LOG.warn("Failed to create LLM for URI: {}", uri, e);
            return Optional.empty();
        }
    }
}

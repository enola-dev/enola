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

import com.google.common.collect.ImmutableList;

import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Optional;

public class ProviderChain<X, T extends Provider<X>> implements Provider<X> {

    private final Iterable<T> providers;

    public ProviderChain(Iterable<T> providers) {
        this.providers = ImmutableList.copyOf(providers);
    }

    @Override
    public String name() {
        return "ProviderChain";
    }

    @Override
    public String docURL() {
        return "https://docs.enola.dev/specs/aiuri/";
    }

    @Override
    public Iterable<String> uriTemplates() {
        var uriTemplates = new ImmutableList.Builder<String>();
        uriTemplates.add("https://docs.enola.dev/specs/aiuri");
        providers.forEach(provider -> uriTemplates.addAll(provider.uriTemplates()));
        return uriTemplates.build();
    }

    public Iterable<URI> uriExamples() {
        var uriExamples = new ImmutableList.Builder<URI>();
        providers.forEach(provider -> uriExamples.addAll(provider.uriExamples()));
        return uriExamples.build();
    }

    @Override
    public Optional<X> optional(URI uri) throws IllegalArgumentException, UncheckedIOException {
        for (var provider : providers) {
            var opt = provider.optional(uri);
            if (opt.isPresent()) return opt;
        }
        return Optional.empty();
    }
}

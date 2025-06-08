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

import com.google.common.collect.ImmutableList;

import dev.langchain4j.model.chat.StreamingChatModel;

import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Optional;

public class ChatModelProviderChain implements ChatModelProvider {
    // TODO Genericify and move to package dev.enola.ai.iri

    // TODO CachingChatLanguageModelProvider

    // TODO Support ?topP / topK, temperature, seed etc. as query parameters!
    //   Or is there no need to set that as default, because it will only be set on requests?

    private final Iterable<ChatModelProvider> providers;

    public ChatModelProviderChain(Iterable<ChatModelProvider> providers) {
        this.providers = ImmutableList.copyOf(providers);
    }

    public ChatModelProviderChain(ChatModelProvider... providers) {
        this(ImmutableList.copyOf(providers));
    }

    @Override
    public String uriTemplate() {
        return "...";
    }

    @Override
    public URI uriExample() {
        return URI.create("https://docs.enola.dev/specs/aiuri");
    }

    @Override
    public StreamingChatModel get(URI uri) throws IllegalArgumentException, UncheckedIOException {
        return optional(uri)
                .orElseThrow(() -> new IllegalArgumentException("No LM provider handles " + uri));
    }

    @Override
    public Optional<StreamingChatModel> optional(URI uri)
            throws IllegalArgumentException, UncheckedIOException {
        for (var provider : providers) {
            var opt = provider.optional(uri);
            if (opt.isPresent()) return opt;
        }
        return Optional.empty();
    }
}

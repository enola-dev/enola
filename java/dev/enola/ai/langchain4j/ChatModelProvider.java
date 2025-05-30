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

import dev.enola.data.Provider;
import dev.langchain4j.model.chat.StreamingChatModel;

import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Optional;

/**
 * ChatLanguageModelProvider provides a LM based on the <a
 * href="https://docs.enola.dev/specs/aiuri/">Enola.dev AI URI spec</a>.
 */
public interface ChatModelProvider extends Provider<URI, StreamingChatModel> {

    default String name() {
        return getClass().getSimpleName();
    }

    String uriTemplate();

    URI uriExample();

    // TODO Set<String> secrets();

    @Override
    default StreamingChatModel get(URI uri) throws IllegalArgumentException, UncheckedIOException {
        return getOptional(uri)
                .orElseThrow(() -> new IllegalArgumentException(name() + " cannot provide " + uri));
    }

    Optional<StreamingChatModel> getOptional(URI uri)
            throws IllegalArgumentException, UncheckedIOException;
}

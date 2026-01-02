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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Provider of an AI Thing based on the <a href="https://docs.enola.dev/specs/aiuri/">Enola.dev AI
 * URI spec</a>.
 */
public interface Provider<T> {

    String name();

    String docURL();

    Iterable<String> uriTemplates();

    Iterable<URI> uriExamples();

    // TODO Set<String> secrets();

    /**
     * Return AI Thing if it's available. Empty if its configuration like secrets etc. is missing.
     */
    Optional<T> optional(URI uri);

    default T get(URI uri) throws IllegalArgumentException {
        return optional(uri)
                .orElseThrow(() -> new IllegalArgumentException(name() + " cannot provide " + uri));
    }

    default T get(String uri, Object context) throws IllegalArgumentException {
        try {
            return get(new URI(uri));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(
                    "Invalid Model URI (see https://docs.enola.dev/specs/aiuri/) '"
                            + uri
                            + "' (raw input: '"
                            + e.getInput()
                            + "') in: "
                            + context,
                    e);
        }
    }
}

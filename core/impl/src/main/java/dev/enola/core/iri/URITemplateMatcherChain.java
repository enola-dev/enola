/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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
package dev.enola.core.iri;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

public class URITemplateMatcherChain<T> {

    private final Collection<Entry<URITemplateSplitter, T>> splitters =
            new ConcurrentLinkedQueue<>();

    public void add(String uriTemplate, T key) {
        splitters.add(new SimpleEntry<>(new URITemplateSplitter(uriTemplate), key));
    }

    public Optional<Entry<T, Map<String, String>>> match(String uri) {
        for (var splitter : splitters) {
            var optMap = splitter.getKey().fromString(uri);
            if (optMap.isPresent()) {
                return Optional.of(new SimpleEntry<>(splitter.getValue(), optMap.get()));
            }
        }
        return Optional.empty();
    }
}

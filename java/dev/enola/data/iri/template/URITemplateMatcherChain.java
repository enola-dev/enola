/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.data.iri.template;

import static java.util.Objects.requireNonNull;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

public class URITemplateMatcherChain<T> {

    private final Comparator<Entry<URITemplateSplitter, T>> COMPARATOR =
            new Comparator<>() {
                @Override
                public int compare(
                        Entry<URITemplateSplitter, T> o1, Entry<URITemplateSplitter, T> o2) {
                    return o2.getKey().getLength() - o1.getKey().getLength();
                }
            };

    // Beware: This needs to be List and not a Set! (Because of how COMPARATOR is implemented;
    // note how URI Template need to be ordered by length - but two *different* template of
    // the *SAME* length still both need to be added!
    private final List<Entry<URITemplateSplitter, T>> splitters;
    private final List<String> templates;

    public static <T> Builder<T> builder() {
        return new Builder<T>();
    }

    public static <T> Builder<T> builderWithExpectedSize(int size) {
        return new Builder<T>(size);
    }

    private URITemplateMatcherChain(List<Entry<URITemplateSplitter, T>> splitters) {
        splitters.sort(COMPARATOR);
        this.splitters = splitters;
        this.templates =
                Collections.unmodifiableList(
                        splitters.stream()
                                .map(entry -> entry.getKey().getTemplate())
                                .collect(Collectors.toList()));
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

    public List<String> listTemplates() {
        // Un-comment for debugging:
        // return splitters.stream()
        //         .map(entry -> entry.getKey().toString())
        //         .collect(Collectors.toSet());
        return templates;
    }

    // skipcq: JAVA-E0169
    public static class Builder<T> implements dev.enola.common.Builder<URITemplateMatcherChain<T>> {
        private Builder() {
            splitters = new ArrayList<>();
        }

        private Builder(int initialCapacity) {
            splitters = new ArrayList<>(initialCapacity);
        }

        private final List<Entry<URITemplateSplitter, T>> splitters;

        @Override
        public URITemplateMatcherChain<T> build() {
            return new URITemplateMatcherChain<>(splitters);
        }

        public Builder<T> add(String uriTemplate, T key) {
            requireNonNull(key);
            requireNonNull(uriTemplate);
            if (has(uriTemplate)) {
                throw new IllegalArgumentException("Already added: " + uriTemplate);
            }
            splitters.add(new SimpleEntry<>(new URITemplateSplitter(uriTemplate), key));
            return this;
        }

        private boolean has(String uriTemplate) {
            requireNonNull(uriTemplate);
            for (var splitter : splitters) {
                var it = splitter.getKey().getTemplate();
                if (it.equals(uriTemplate)) {
                    return true;
                }
            }
            return false;
        }
    }
}

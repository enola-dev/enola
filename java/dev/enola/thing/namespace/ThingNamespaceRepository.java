/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.thing.namespace;

import dev.enola.data.iri.namespace.repo.CachingNamespaceRepository;
import dev.enola.data.iri.namespace.repo.ImmutableNamespace;
import dev.enola.data.iri.namespace.repo.Namespace;
import dev.enola.data.iri.namespace.repo.NamespaceRepository;
import dev.enola.thing.PredicatesObjects;
import dev.enola.thing.repo.ThingProvider;

import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.Optional;

/**
 * {@link NamespaceRepository} based on reading {@link #ACTIVE_NAMESPACES_IRI} from a {@link
 * ThingProvider}.
 *
 * <p>This is (much!) slower than the {@link
 * dev.enola.data.iri.namespace.repo.NamespaceRepositoryEnolaDefaults#INSTANCE}, and should only
 * ever be used indirectly through {@link CachingNamespaceRepository}.
 *
 * @param thingProvider the provider of things used to retrieve namespace information
 */
public record ThingNamespaceRepository(ThingProvider thingProvider) implements NamespaceRepository {

    public static final String ACTIVE_NAMESPACES_IRI = "https://enola.dev/namespaces";

    public ThingNamespaceRepository() {
        this(ThingProvider.CTX);
    }

    @Override
    public @Nullable Namespace get(String iri) {
        return thingProvider
                .getOptional(ACTIVE_NAMESPACES_IRI)
                .flatMap(
                        ns ->
                                ns.getOptional(iri, String.class)
                                        .map(prefix -> new ImmutableNamespace(prefix, iri)))
                .orElse(null);
    }

    @Override
    public Optional<String> getIRI(String prefix) {
        var optNamespaces = thingProvider.getOptional(ACTIVE_NAMESPACES_IRI);
        if (optNamespaces.isEmpty()) return Optional.empty();
        var namespaces = optNamespaces.get();
        // TODO Replace with ThingVisitor, when available.
        for (var iri : namespaces.predicateIRIs()) {
            var object = namespaces.get(iri);
            if (prefix.equals(object)) return Optional.of(iri);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<String> listIRI() {
        return thingProvider
                .getOptional(ACTIVE_NAMESPACES_IRI)
                .map(PredicatesObjects::predicateIRIs)
                .orElse(Collections.emptySet());
    }
}

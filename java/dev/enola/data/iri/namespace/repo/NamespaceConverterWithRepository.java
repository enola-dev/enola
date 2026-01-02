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
package dev.enola.data.iri.namespace.repo;

import dev.enola.data.iri.IRI;
import dev.enola.data.iri.NamespaceConverter;

public class NamespaceConverterWithRepository implements NamespaceConverter {

    private final NamespaceRepository repo;

    public NamespaceConverterWithRepository(NamespaceRepository repo) {
        this.repo = repo;
    }

    @Override
    public String toCURIE(Object iri) {
        var iriString = iri.toString();
        for (var namespace : repo.list()) {
            if (iriString.startsWith(namespace.iri())) {
                var rest = iriString.substring(namespace.iri().length());
                return namespace.prefix() + ":" + rest;
            }
        }
        return iriString;
    }

    @Override
    public IRI toIRI(String curie) {
        var p = curie.indexOf(':');
        if (p == -1) return IRI.from(curie);

        var iriSchema = curie.substring(0, p);

        var optNamepaceBaseIRI = repo.getIRI(iriSchema);
        if (optNamepaceBaseIRI.isEmpty()) return IRI.from(curie);

        var schemaSpecificPart = curie.substring(p + 1);
        return IRI.from(optNamepaceBaseIRI.get(), schemaSpecificPart);
    }
}

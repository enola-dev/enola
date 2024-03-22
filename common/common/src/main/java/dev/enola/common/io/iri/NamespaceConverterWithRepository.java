/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.iri;

public class NamespaceConverterWithRepository implements NamespaceConverter {

    private final NamespaceRepository repo;

    public NamespaceConverterWithRepository(NamespaceRepository repo) {
        this.repo = repo;
    }

    @Override
    public String toCURIE(String iri) {
        for (var namespace : repo.list()) {
            if (iri.startsWith(namespace.iri())) {
                var rest = iri.substring(namespace.iri().length());
                return namespace.prefix() + ":" + rest;
            }
        }
        return iri;
    }

    @Override
    public String toIRI(String curie) {
        var p = curie.indexOf(':');
        if (p == -1) return curie;

        var iriSchema = curie.substring(0, p);

        var optNamepaceBaseIRI = repo.getIRI(iriSchema);
        if (optNamepaceBaseIRI.isEmpty()) return curie;

        var schemaSpecificPart = curie.substring(p + 1);
        return optNamepaceBaseIRI.get() + schemaSpecificPart;
    }
}

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
package dev.enola.rdf.proto;

import dev.enola.data.iri.namespace.repo.Namespace;
import dev.enola.data.iri.namespace.repo.NamespaceRepository;
import dev.enola.thing.proto.ThingOrBuilder;
import dev.enola.thing.proto.Value;

import org.eclipse.rdf4j.rio.RDFHandler;

import java.util.HashSet;

class Namespacer {

    // Instead of all of NamespaceRepository.CTX.list(),
    // only add namespace prefixes for IRI that are actually used,
    // by (recursively!) visiting all Things' properties, and objects - and nested:

    static void setNamespaces(ThingOrBuilder from, RDFHandler into) {
        var namespaceRepo = NamespaceRepository.ctx();
        var namespaces = new HashSet<Namespace>();
        visit(from, namespaceRepo, namespaces);
        for (var ns : namespaces) into.handleNamespace(ns.prefix(), ns.iri());
    }

    private static void visit(
            ThingOrBuilder from, NamespaceRepository namespaceRepo, HashSet<Namespace> namespaces) {

        var thingIRI = from.getIri();
        if (thingIRI != null) namespaceRepo.match(thingIRI).ifPresent(namespaces::add);

        from.getPropertiesMap()
                .forEach(
                        (key, value) -> {
                            namespaceRepo.match(key).ifPresent(namespaces::add);
                            visit(value, namespaceRepo, namespaces);
                        });
    }

    private static void visit(
            Value value, NamespaceRepository namespaceRepo, HashSet<Namespace> namespaces) {
        switch (value.getKindCase()) {
            case STRUCT -> visit(value.getStruct(), namespaceRepo, namespaces);
            case LIST -> {
                for (var innerValue : value.getList().getValuesList())
                    visit(innerValue, namespaceRepo, namespaces);
            }
        }
    }
}

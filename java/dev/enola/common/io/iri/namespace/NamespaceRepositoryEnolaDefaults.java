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
package dev.enola.common.io.iri.namespace;

public class NamespaceRepositoryEnolaDefaults {

    // TODO Replace this with something which reads e.g. //models/enola.dev/namespaces.ttl

    public static final NamespaceRepository INSTANCE =
            new NamespaceRepositoryBuilder()
                    .store("enola", "https://enola.dev/")
                    .store("xsd", "http://www.w3.org/2001/XMLSchema#")
                    .store("schema", "https://schema.org/")
                    .store("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
                    .store("rdfs", "http://www.w3.org/2000/01/rdf-schema#")
                    .store("foaf", "http://xmlns.com/foaf/0.1/")
                    .store("dc", "http://purl.org/dc/elements/1.1/")
                    .store("dcterms", "http://purl.org/dc/terms/")
                    .store("owl", "http://www.w3.org/2002/07/owl#")
                    .store("ex", "https://example.org/")
                    .store("epub", "http://www.idpf.org/2007/ops#") // TODO is it / or /# or # ?!
                    // https://idpf.github.io/epub-prefixes/packages/
                    .store("a11y", "http://www.idpf.org/epub/vocab/package/a11y/#")
                    .store("epubsc", "http://idpf.org/epub/vocab/sc/#")
                    .store("marc", "http://id.loc.gov/vocabulary/")
                    .store("media", "http://www.idpf.org/epub/vocab/overlays/#")
                    .store("onix", "http://www.editeur.org/ONIX/book/codelists/current.html#")
                    .store("rendition", "http://www.idpf.org/vocab/rendition/#")
                    .store("msv", "http://www.idpf.org/epub/vocab/structure/magazine/#")
                    .store(
                            "prism",
                            "http://www.prismstandard.org/specifications/3.0/PRISM_CV_Spec_3.0.htm#")
                    .build();
}

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

public class NamespaceRepositoryEnolaDefaults {

    // TODO Move to adopting ThingNamespaceRepository, based on //models/enola.dev/namespaces.ttl
    // Add Human Background Documentation Reference URLs to TTL, where IRI don't resolve to URL

    // TODO add https://www.w3.org/2011/rdfa-context/rdfa-1.1

    // TODO add https://github.com/zazuko/rdf-vocabularies/tree/master/ontologies (during build)
    // see https://prefix.zazuko.com/prefixes

    // TODO add https://prefix.cc/popular/all.sparql by reading that during build

    // TODO add https://lov.linkeddata.es/dataset/lov/

    public static final NamespaceRepository INSTANCE =
            new NamespaceRepositoryBuilder()
                    .store("enola", "https://enola.dev/")
                    .store("xsd", "http://www.w3.org/2001/XMLSchema#")
                    .store("xhtml", "http://www.w3.org/1999/xhtml/")
                    .store("schema", "https://schema.org/")
                    .store("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
                    .store("rdfs", "http://www.w3.org/2000/01/rdf-schema#")
                    .store("foaf", "http://xmlns.com/foaf/0.1/")
                    .store("dc", "http://purl.org/dc/elements/1.1/")
                    .store("dcterms", "http://purl.org/dc/terms/")
                    .store("owl", "http://www.w3.org/2002/07/owl#")
                    .store("ex", "https://example.org/")

                    // EPUB
                    // TODO Double check if correctly it is / or /# or # ?!
                    .store("epub", "http://www.idpf.org/2007/ops#")
                    .store("opf", "http://www.idpf.org/2007/opf#")
                    .store("calibre", "http://calibre.kovidgoyal.net/2009/metadata")
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

                    // PDF, see https://developer.adobe.com/xmp/docs/XMPNamespaces/
                    // https://developer.adobe.com/xmp/docs/XMPNamespaces/xmp/
                    .store("xmp", "http://ns.adobe.com/xap/1.0/")
                    // https://developer.adobe.com/xmp/docs/XMPNamespaces/xmpMM/
                    .store("xmpMM", "http://ns.adobe.com/xap/1.0/mm/")
                    // https://developer.adobe.com/xmp/docs/XMPNamespaces/xmpBJ/
                    .store("xmpBJ", "http://ns.adobe.com/xap/1.0/bj/")
                    // https://developer.adobe.com/xmp/docs/XMPNamespaces/xmpTPg/
                    .store("xmpTPg", "https://developer.adobe.com/xmp/docs/XMPNamespaces/xmpTPg/")
                    // https://developer.adobe.com/xmp/docs/XMPNamespaces/xmpDM/
                    .store("xmpDM", "http://ns.adobe.com/xmp/1.0/DynamicMedia/")
                    // https://developer.adobe.com/xmp/docs/XMPNamespaces/xmpRights/
                    .store("xmpRights", "http://ns.adobe.com/xap/1.0/rights/")
                    // https://developer.adobe.com/xmp/docs/XMPNamespaces/pdf/
                    .store("pdf", "http://ns.adobe.com/pdf/1.3/")
                    // https://developer.adobe.com/xmp/docs/XMPNamespaces/photoshop/
                    .store("photoshop", "http://ns.adobe.com/photoshop/1.0/")
                    // https://developer.adobe.com/xmp/docs/XMPNamespaces/crs/
                    .store("crs", "http://ns.adobe.com/camera-raw-settings/1.0/")
                    // https://developer.adobe.com/xmp/docs/XMPNamespaces/exif/
                    .store("exif", "http://ns.adobe.com/exif/1.0/")
                    // https://developer.adobe.com/xmp/docs/XMPNamespaces/tiff/
                    .store("tiff", "http://ns.adobe.com/tiff/1.0/")
                    // http://iptc.org/std/Iptc4xmpCore/1.0/xmlns/
                    .store("Iptc4xmpCore", "http://iptc.org/std/Iptc4xmpCore/1.0/xmlns/")
                    .build();
}

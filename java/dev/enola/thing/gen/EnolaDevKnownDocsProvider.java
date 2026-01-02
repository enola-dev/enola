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
package dev.enola.thing.gen;

import com.google.common.collect.ImmutableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class EnolaDevKnownDocsProvider implements KnownDocsProvider {

    // TODO Support looking up Thing IRIs with Templates??

    // TODO Later make this configurable... with Things with URI Templates? Simpler?

    // Nota Bene: https://schema.org and http://purl.org/dc/ "Dublin Core" (e.g.
    // http://purl.org/dc/elements/1.1/) are intentionally NOT included here, because they are
    // "nice" already as-is.

    private static final Logger LOG = LoggerFactory.getLogger(EnolaDevKnownDocsProvider.class);

    private final Iterable<String> wellKnownPrefixes =
            ImmutableList.of(
                    "https://enola.dev/",
                    "http://www.w3.org/2000/01/rdf-schema",
                    "http://www.w3.org/1999/02/22-rdf-syntax-ns",
                    "http://www.w3.org/2001/XMLSchema");

    @Override
    public String get(String iri) {
        for (var wellKnownPrefix : wellKnownPrefixes) {
            if (iri.startsWith(wellKnownPrefix)) {
                try {
                    return "https://docs.enola.dev/models/"
                            + Relativizer.dropSchemeAddExtension(new URI(iri), "")
                            + "/";
                } catch (URISyntaxException e) {
                    LOG.warn("URISyntaxException for {}", iri, e);
                    return iri;
                }
            }
        }
        return iri;
    }
}

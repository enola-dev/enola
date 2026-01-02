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
package dev.enola.data.iri;

import dev.enola.common.context.TLC;

/**
 * NamespaceConverter converts "compact" <a
 * href="https://en.m.wikipedia.org/wiki/CURIE">CURIE</a>-like IRIs (i.e. an IRI with a short
 * schema, from a list of prefixes only valid in a local context instead of globally) to &amp; from
 * "full" IRIs (which are globally unique).
 *
 * <p>Both methods of this interface may simply return back the argument, if no "match" was found.
 *
 * <p>This class does not actually use [square] brackets around the CURIE.
 */
public interface NamespaceConverter {

    String toCURIE(Object iri);

    IRI toIRI(String curie);

    NamespaceConverter CTX =
            new NamespaceConverter() {
                @Override
                public String toCURIE(Object iri) {
                    return TLC.get(NamespaceConverter.class).toCURIE(iri);
                }

                @Override
                public IRI toIRI(String curie) {
                    return TLC.get(NamespaceConverter.class).toIRI(curie);
                }
            };
}

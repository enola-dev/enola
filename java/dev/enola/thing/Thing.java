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
package dev.enola.thing;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Thing is the central data structure of Enola.
 *
 * <p>Each Thing has an {@link #iri()}, which uniquely identifies it. All Things have 0..n
 * predicates, each identified by an IRI itself, and having a value. Each such value has a (Java,
 * here) Type (see {@link PredicatesObjects}).
 *
 * <p>This is, of course, heavily inspired by TBL's vision of the <i>Semantic Web</i> of <i>Linked
 * Data</i>, such as also described by standards such has RDF and then used e.g. by SPARQL, or
 * JSON-LD, etc.
 */
public interface Thing extends PredicatesObjects {

    String iri();

    @Override
    Builder<? extends Thing> copy();

    @SuppressFBWarnings("NM_SAME_SIMPLE_NAME_AS_INTERFACE")
    interface Builder<B extends Thing> extends PredicatesObjects.Builder<B> { // skipcq: JAVA-E0169

        Builder<B> iri(String iri);

        Builder<B> set(String predicateIRI, Object value);

        Builder<B> set(String predicateIRI, Object value, String datatypeIRI);

        @Override
        B build();
    }
}

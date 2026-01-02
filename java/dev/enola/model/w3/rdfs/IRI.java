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
package dev.enola.model.w3.rdfs;

import dev.enola.thing.HasIRI;
import dev.enola.thing.HasPredicateIRI;

// TODO Replace with String constants on Property and Class, like in HasA/HasB/TestSomething
enum IRI implements HasIRI {
    RDFS("http://www.w3.org/2000/01/rdf-schema#");

    enum Predicate implements HasPredicateIRI {
        seeAlso(RDFS + "seeAlso"),
        comment(RDFS + "comment"),
        label(RDFS + "label");

        private final String iri;

        Predicate(String iri) {
            this.iri = iri;
        }

        @Override
        public String iri() {
            return iri;
        }

        @Override
        public String toString() {
            return iri();
        }
    }

    enum Class implements HasClassIRI {
        Class(RDFS + "Class"),
        Resource(RDFS + "Resource");

        private final String iri;

        Class(String iri) {
            this.iri = iri;
        }

        @Override
        public String iri() {
            return iri;
        }

        @Override
        public String toString() {
            return iri();
        }
    }

    private final String iri;

    IRI(String iri) {
        this.iri = iri;
    }

    @Override
    public String iri() {
        return iri;
    }

    @Override
    public String toString() {
        return iri();
    }
}

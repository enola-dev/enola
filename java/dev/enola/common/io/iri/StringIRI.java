/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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

import java.net.URI;
import java.net.URISyntaxException;

/* non-public! */ final class StringIRI extends IRI {

    private final String iri;

    StringIRI(String iri) {
        this.iri = iri;
    }

    @Override
    public String toString() {
        return iri;
    }

    @Override
    public int hashCode() {
        return iri.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof StringIRI otherStringIRI) {
            return iri.equals(otherStringIRI.iri);
        }
        return false;
    }

    @Override
    public int compareTo(IRI other) {
        if (other instanceof StringIRI otherStringIRI) return iri.compareTo(otherStringIRI.iri);
        else return iri.compareTo(other.toString());
    }

    @Override
    public URI toURI() throws URISyntaxException {
        return new URI(iri);
    }
}

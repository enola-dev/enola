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
package dev.enola.data.iri;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URI;

/* non-public! */
/*TODO value*/ class HolderIRI extends IRI {

    // We know what we're doing (hopefully)
    @SuppressWarnings("Immutable")
    private IRI iri;

    HolderIRI(IRI iri) {
        this.iri = requireNonNull(iri);
    }

    @Override
    public URI toURI() {
        if (iri instanceof URI_IRI uriIRI) {
            return uriIRI.toURI();
        } else {
            var uriIRI = new URI_IRI(URI.create(iri.toString()));
            iri = uriIRI;
            return uriIRI.toURI();
        }
    }

    @Override
    public void append(Appendable appendable) throws IOException {
        iri.append(appendable);
    }

    @Override
    public String toString() {
        return iri.toString();
    }

    @Override
    public int hashCode() {
        return iri.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other instanceof HolderIRI otherHolderIRI) return iri.equals(otherHolderIRI.iri);
        if (other instanceof IRI otherIRI) return this.toString().equals(otherIRI.toString());
        return false;
    }

    @Override
    public int compareTo(IRI other) {
        return iri.compareTo(other);
    }
}

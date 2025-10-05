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

/* non-public! */ final /*TODO value*/ class URI_IRI extends IRI {

    private final URI uri;

    URI_IRI(URI uri) {
        this.uri = requireNonNull(uri);
    }

    @Override
    public void append(Appendable appendable) throws IOException {
        appendable.append(toString());
    }

    @Override
    public String toString() {
        return uri.toString();
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other instanceof URI_IRI otherURI_IRI) return uri.equals(otherURI_IRI.uri);
        if (other instanceof IRI otherIRI) return this.toString().equals(otherIRI.toString());
        return false;
    }

    @Override
    public int compareTo(IRI other) {
        if (other instanceof URI_IRI otherURI_IRI) return uri.compareTo(otherURI_IRI.uri);
        else return uri.toString().compareTo(other.toString());
    }

    @Override
    public URI toURI() {
        return uri;
    }
}

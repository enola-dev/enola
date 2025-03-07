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

public class HolderIRI extends IRI {

    // We know what we're doing (hopefully)
    @SuppressWarnings("Immutable")
    private IRI iri;

    private HolderIRI(IRI iri) {
        this.iri = iri;
    }

    public static IRI from(String iriString) {
        return new HolderIRI(new StringIRI(iriString));
    }

    public static IRI from(java.net.URI uri) {
        return new HolderIRI(new URI_IRI(uri));
    }

    @Override
    public URI toURI() throws URISyntaxException {
        if (iri instanceof URI_IRI uriIRI) {
            return uriIRI.toURI();
        } else {
            var uriIRI = new URI_IRI(new URI(iri.toString()));
            iri = uriIRI;
            return uriIRI.toURI();
        }
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
    @SuppressWarnings("EqualsDoesntCheckParameterClass")
    public boolean equals(Object other) {
        return iri.equals(other);
    }

    @Override
    public int compareTo(IRI other) {
        return iri.compareTo(other);
    }
}

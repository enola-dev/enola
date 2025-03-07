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
package dev.enola.common.io.iri.namespace;

import com.google.common.base.Objects;

import dev.enola.common.io.iri.IRI;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;

/**
 * CURIE {@link dev.enola.common.io.iri.IRI}.
 *
 * <p>See also {@link QName}.
 */
/* non-public! */ final class CURIE_IRI extends IRI {

    // NO! private final String prefix;
    private final String namespaceIRI;
    private final String localName;

    @SuppressWarnings("Immutable") // We (hopefully) know what we're doing!
    private transient @Nullable String iri;

    CURIE_IRI(String namespaceIRI, String localName) {
        this.namespaceIRI = namespaceIRI;
        this.localName = localName;
    }

    @Override
    public void append(Appendable appendable) throws IOException {
        appendable.append(namespaceIRI).append(localName);
    }

    @Override
    public String toString() {
        if (iri == null) iri = namespaceIRI + localName;
        return iri;
    }

    @Override
    public int hashCode() {
        int result = namespaceIRI.hashCode();
        result = 31 * result + localName.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CURIE_IRI otherCurieIRI)
            return Objects.equal(namespaceIRI, otherCurieIRI.namespaceIRI)
                    && Objects.equal(localName, otherCurieIRI.localName);
        else return false;
    }

    @Override
    public int compareTo(IRI other) {
        if (other instanceof CURIE_IRI otherCurieIRI) {
            int namespaceComparison = this.namespaceIRI.compareTo(otherCurieIRI.namespaceIRI);
            if (namespaceComparison != 0) {
                return namespaceComparison;
            }
            return this.localName.compareTo(otherCurieIRI.localName);
        } else {
            return this.toString().compareTo(other.toString());
        }
    }

    @Override
    public URI toURI() throws URISyntaxException {
        return new URI(toString());
    }
}

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

import com.google.common.base.Objects;

import java.io.IOException;

import javax.xml.namespace.QName;

/**
 * CURIE {@link dev.enola.common.io.iri.IRI}.
 *
 * <p>See also {@link QName}.
 */
/* non-public! */ final /*TODO value*/ class CURIE_IRI extends StringableIRI {

    // NO! private final String prefix;
    private final String namespaceIRI;
    private final String localName;

    CURIE_IRI(String namespaceIRI, String localName) {
        this.namespaceIRI = requireNonNull(namespaceIRI, "namespaceIRI");
        this.localName = requireNonNull(localName, "localName");
    }

    @Override
    public void append(Appendable appendable) throws IOException {
        appendable.append(namespaceIRI).append(localName);
    }

    @Override
    protected String createStringIRI() {
        return namespaceIRI + localName;
    }

    @Override
    public int hashCode() {
        int result = namespaceIRI.hashCode();
        result = 31 * result + localName.hashCode();
        return result;
    }

    @Override
    protected boolean isEqualTo(Object other) {
        if (other instanceof CURIE_IRI otherCurieIRI)
            return Objects.equal(namespaceIRI, otherCurieIRI.namespaceIRI)
                    && Objects.equal(localName, otherCurieIRI.localName);
        return false;
    }

    @Override
    protected boolean isComparableTo(Object other) {
        return other instanceof CURIE_IRI;
    }

    @Override
    protected int compare(Object other) {
        var otherCurieIRI = (CURIE_IRI) other;
        int namespaceComparison = this.namespaceIRI.compareTo(otherCurieIRI.namespaceIRI);
        if (namespaceComparison != 0) {
            return namespaceComparison;
        }
        return this.localName.compareTo(otherCurieIRI.localName);
    }
}

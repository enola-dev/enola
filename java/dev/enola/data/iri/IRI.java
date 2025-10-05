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

import com.google.errorprone.annotations.Immutable;

import java.io.IOException;
import java.net.URI;

/**
 * IRI (Internationalized Resource Identifier) at its core is basically just any object which can be
 * identified by a typically globally (or "clearly within a specific context") unique String; see
 * also ID.
 *
 * <p>Contrary to an URL (URI), this is not limited to "something which can be fetched". It has no
 * explicit notion of a "protocol" (scheme) or "authority" (host) - nor path. Thus, there is also no
 * resolve() kind of method here (if that's needed, then use {@link URI}, or Enola's own TBD class).
 * There is therefore also no normalize() sort of method here - because that again really depends on
 * the "protocol" (scheme).
 *
 * <p>The "internationalized" aspect here specifically refers to the fact that what {@link
 * #toString()} returns is not "encoded" in any way; it's literally just some String, which may well
 * contain "rich" (Unicode) characters (although implementations may well limit and not permit e.g.
 * whitespace, etc.) Implementing classes may however well choose to offer additional encoding
 * related methods.
 */
// TODO @ID
@Immutable
public abstract /*TODO value*/ class IRI implements Comparable<IRI> {

    public static IRI from(String iri) {
        return new HolderIRI(new StringIRI(iri));
    }

    public static IRI from(String namespaceIRI, String localName) {
        return new HolderIRI(new NamespacedIRI(namespaceIRI, localName));
    }

    // TODO Re-think if IRI from(java.net.URI uri) is really needed?
    public static IRI from(java.net.URI uri) {
        return new HolderIRI(new URI_IRI(uri));
    }

    /* TODO
    public static IRI newUUID() {
        return new UUID_IRI(UUID.randomUUID());
    }
    */

    // TODO Consider adding an Object id() method here? See IDIRI.

    /*
        // TODO Globally rethink binary bytes representations...
        public void append(OutputStream os) throws IOException {
            // TODO #effiency How to best pre-size the BufferedWriter?
            var writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            append(writer);
            // TODO try() catch {} AutoCloseable!
            writer.flush();
            writer.close();
        }
    */

    public abstract void append(Appendable appendable) throws IOException;

    @Override
    public abstract String toString();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int compareTo(IRI other);

    public URI toURI() {
        return URI.create(toString());
    }

    public final String toCURIE() {
        return NamespaceConverter.CTX.toCURIE(this);
    }
}

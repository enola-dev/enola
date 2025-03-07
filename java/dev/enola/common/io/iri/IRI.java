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

import com.google.errorprone.annotations.Immutable;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * IRI (Internationalized Resource Identifier) at its core is basically just any object which can be
 * identified by a typically globally (or "clearly within a specific context") unique String; see
 * also {@link ID}.
 *
 * <p>Contrary to {@link URL} this is technically not per-se limited to "something which can be
 * fetched". It has no explicit notion of a "protocol" (scheme) or "authority" (host) or path. There
 * is nothing inherent to "normalize".
 *
 * <p>The "internationalized" aspect here specifically refers to the fact that what {@link
 * #toString()} returns is not "encoded" in any way; it's literally just some String, which may well
 * contain "rich" (Unicode) characters (although implementations may well limit and not permit e.g.
 * whitespace, etc.) Implementing classes may however well choose to offer additional encoding
 * related methods.
 */
// TODO @ID
@Immutable
public abstract class IRI implements Comparable<IRI> {

    @Override
    public abstract String toString();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int compareTo(IRI other);

    public abstract URI toURI() throws URISyntaxException;

    // TODO public abstract URL toURL();

    // TODO public abstract String toCURIE();

    // TODO public abstract Long toID();

    // ? public abstract java.xml.QName toQName();
}

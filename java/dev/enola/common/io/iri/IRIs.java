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
package dev.enola.common.io.iri;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Utility methods for Internationalized Resource Identifiers (IRIs).
 *
 * <p>See <a href="https://datatracker.ietf.org/doc/html/rfc3987">RFC 3987</a>.
 */
public final class IRIs {
    // see also class dev.enola.common.io.resource.URIs

    /**
     * Resolves an IRI reference against a base IRI and returns the resulting IRI as a String.
     *
     * <p>See <a href="https://datatracker.ietf.org/doc/html/rfc3986#section-4">Section §4 of RFC
     * 3986</a>.
     *
     * <p>This is currently implemented using {@link java.net.URI#resolve()}. This works great, but
     * it creates (two) intermediate URI objects. If this ever becomes a problem for performance,
     * this implementation could be optimized to work directly on Strings.
     */
    public static String resolve(String base, String reference) throws URISyntaxException {
        return toURI(base).resolve(reference).toString();
    }

    public static URI toURI(String iri) throws URISyntaxException {
        return new URI(iri);
    }

    private IRIs() {}
}

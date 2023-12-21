/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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
package dev.enola.core;

import java.net.URI;

/**
 * ERI are Enola Resource Identifiers.
 *
 * <p>See also https://docs.enola.dev/concepts/uri, and e.g. dev.enola.core.GetEntityRequest#eri.
 *
 * <p>The enola: URI scheme is accepted, but ignored; other schemes are (currently) rejected. The
 * "authority" of the URI (currently) needs to be empty, but it's intended to be supported
 * eventually, for federation. The "?query" and "#fragment" of the URI are not supported, and will
 * be rejected.
 *
 * <p>This is the successor of {@link IDs}.
 */
public final class ERI {

    private final String path;

    private ERI(String path) {
        this.path = path;
    }

    public static ERI create(String eri) throws IllegalArgumentException {
        var uri = URI.create(eri);
        var scheme = uri.getScheme();

        check(eri, uri.getScheme() == null || "enola".equals(scheme) || "urn".equals(scheme));
        check(eri, uri.getRawAuthority() == null);
        check(eri, uri.getRawFragment() == null);
        check(eri, uri.getHost() == null);
        check(eri, uri.getPort() == -1);
        check(eri, uri.getRawQuery() == null);
        check(eri, uri.getRawUserInfo() == null);

        String tail;
        if ("urn".equals(scheme)) tail = uri.getSchemeSpecificPart().substring(6);
        else tail = uri.getSchemeSpecificPart();
        return new ERI(tail);
    }

    private static void check(String eri, boolean ok) {
        if (!ok)
            throw new IllegalArgumentException("Invalid ERI, Enola Resource Identifier: " + eri);
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return path;
    }
}

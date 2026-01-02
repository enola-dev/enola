/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.thing.gen;

import static com.google.common.base.Strings.nullToEmpty;

import dev.enola.thing.template.Templates;

import java.net.URI;

public final class Relativizer {

    private static String path(URI uri) {
        var path = uri.getPath();
        if (path == null) {
            throw new IllegalArgumentException("TODO: Extract path from URI: " + uri);
        }
        return path;
    }

    public static String relativize(URI baseURI, URI targetURI) {
        if (!baseURI.getScheme().equals(targetURI.getScheme())) return targetURI.toString();
        String[] basePath = path(baseURI).split("/");
        String[] targetPath = path(targetURI).split("/");

        int commonPrefixLength = 0;
        while (commonPrefixLength < basePath.length
                && commonPrefixLength < targetPath.length
                && basePath[commonPrefixLength].equals(targetPath[commonPrefixLength])) {
            commonPrefixLength++;
        }

        StringBuilder relativePath = new StringBuilder();
        for (int i = commonPrefixLength; i < basePath.length - 1; i++) { // -1 because no .. on last
            relativePath.append("../");
        }
        for (int i = commonPrefixLength; i < targetPath.length; i++) {
            relativePath.append(targetPath[i]).append("/");
        }
        if (!relativePath.isEmpty()) {
            relativePath.deleteCharAt(relativePath.length() - 1); // Remove trailing slash
        }

        var query = targetURI.getQuery();
        if (query != null) {
            relativePath.append('?');
            relativePath.append(query);
        }

        if (!relativePath.isEmpty()) return relativePath.toString();
        else return "#";
    }

    public static URI dropSchemeAddExtension(String iri, String extension) {
        return dropSchemeAddExtension(URI.create(Templates.dropVariableMarkers(iri)), extension);
    }

    // skipcq: JAVA-R1000 TODO Simplify this overly complex function!
    public static URI dropSchemeAddExtension(final URI thingIRI, final String extension) {
        var scheme = thingIRI.getScheme();
        if (scheme == null) return thingIRI;

        // TODO #performance Use StringBuilder instead String ssp!
        StringBuilder ssp = new StringBuilder();

        // Scheme
        if (!scheme.startsWith("http")) ssp.append(scheme);

        // Authority
        var authority = nullToEmpty(thingIRI.getAuthority());
        if (!authority.isEmpty()) ssp.append(!ssp.isEmpty() ? "/" : "").append(authority);

        // Path
        var path = nullToEmpty(thingIRI.getPath());
        if (!path.isEmpty()) ssp.append(path);
        else if (!scheme.startsWith("http"))
            ssp.append('/').append(thingIRI.getSchemeSpecificPart());

        // Fragment
        var fragment = thingIRI.getFragment();
        if (fragment != null) {
            ssp.append('/').append(fragment);
        }

        var query = thingIRI.getQuery();
        if (query != null) query = "?" + query;
        else query = "";

        var dotExtension = !extension.isEmpty() ? "." + extension : "";

        if (ssp.charAt(0) == '/' && ssp.charAt(1) == '/') {
            if (ssp.length() > 2)
                return URI.create(nos(ssp.substring(2)) + "." + extension + query);
            else return URI.create(dotExtension + query); // TODO ???
        } else if (ssp.charAt(0) == '/') {
            if (ssp.length() > 1)
                return URI.create(nos(ssp.substring(1)) + "." + extension + query);
            else return URI.create(dotExtension + query); // TODO ???
        } else return URI.create(nos(ssp.toString()) + dotExtension + query);
    }

    /** No Slash! */
    private static String nos(String path) {
        if (!path.endsWith("/")) return path;
        else return path.substring(0, path.length() - 1);
    }

    private Relativizer() {}
}

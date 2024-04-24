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
package dev.enola.thing.gen;

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

        if (!relativePath.isEmpty()) return relativePath.toString();
        else return "#";
    }

    public static URI dropSchemeAddExtension(URI thingIRI, String extension) {
        if (thingIRI.getScheme().startsWith("http")) {
            var ssp = thingIRI.getSchemeSpecificPart();
            var fragment = thingIRI.getFragment();
            if (fragment != null) {
                ssp = ssp + "/" + fragment;
            }
            if (ssp.startsWith("//")) {
                if (ssp.length() > 2) return URI.create(nos(ssp.substring(2)) + "." + extension);
                else return URI.create("." + extension); // TODO ???
            } else if (ssp.startsWith("/")) {
                if (ssp.length() > 1) return URI.create(nos(ssp.substring(1)) + "." + extension);
                else return URI.create("." + extension); // TODO ???
            } else return URI.create(nos(ssp) + "." + extension);
        } else {
            // Intentionally WITHOUT adding extension, here;
            // this is typically a https://enola.dev/source property value like file:///.../some.ttl
            return thingIRI;
        }
    }

    /** No Slash! */
    private static String nos(String path) {
        if (!path.endsWith("/")) return path;
        else return path.substring(0, path.length() - 1);
    }

    private Relativizer() {}
}

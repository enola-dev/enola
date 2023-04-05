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
package dev.enola.common.io.resource;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

public final class URIs {

    private static final Splitter AMPERSAND_SPLITTER =
            Splitter.on('&').omitEmptyStrings().trimResults();
    private static final Splitter EQUALSIGN_SPLITTER =
            Splitter.on('=').omitEmptyStrings().trimResults();

    public static Charset getCharset(URI uri) {
        var charset = getQueryMap(uri).get("charset");
        if (charset == null) {
            throw new IllegalArgumentException(
                    "URI does not contain a &charset=... : " + uri.toString());
        }
        return Charset.forName(charset);
    }

    public static Map<String, String> getQueryMap(URI uri) {
        if (uri == null) return Collections.emptyMap();
        Map<String, String> map = new HashMap<>();
        Set<String> queryParameterNames = new HashSet<>();
        String query = uri.getQuery();
        if (Strings.isNullOrEmpty(query)) {
            var part = uri.getSchemeSpecificPart();
            var qp = part.indexOf('?');
            if (qp > -1) {
                var fp = part.indexOf('#');
                if (fp == -1) {
                    query = part.substring(qp + 1);
                } else {
                    query = part.substring(qp + 1, fp);
                }
            }
        }
        if (Strings.isNullOrEmpty(query)) {
            return Collections.emptyMap();
        }
        final String finalQuery = query;
        AMPERSAND_SPLITTER
                .split(query)
                .forEach(
                        pair -> {
                            String[] nameValue =
                                    Iterables.toArray(EQUALSIGN_SPLITTER.split(pair), String.class);
                            if (nameValue.length > 2) {
                                throw new IllegalArgumentException(
                                        uri.toString()
                                                + " ID URI ?query has name/value with more than"
                                                + " 1 '=' sign: "
                                                + pair);
                            }
                            if (nameValue.length > 0) {
                                if (queryParameterNames.contains(nameValue[0]))
                                    throw new IllegalArgumentException(
                                            uri.toString()
                                                    + " ID URI ?query has duplicate names: "
                                                    + finalQuery);
                                queryParameterNames.add(nameValue[0]);
                            }
                            if (nameValue.length == 2) {
                                map.put(nameValue[0], nameValue[1]);
                            } else { // nameValue.length == 1
                                map.put(nameValue[0], null);
                            }
                        });
        return map;
    }

    /**
     * Extracts the "file name" from an URI, or the empty string if there is none. The filename is
     * simply the last part of the path of the URI. It COULD be a directory! Works for file: http:
     * and other even for "weird" URIs, such as those from Classpath URLs.
     *
     * <p>See also {@link com.google.common.io.Files#getFileExtension(String)} and @{@link
     * com.google.common.io.Files#getNameWithoutExtension(String)}.
     */
    public static String getFilename(URI uri) {
        final var scheme = uri.getScheme();
        if (Strings.isNullOrEmpty(scheme)) {
            return "";
        }
        if ("file".equals(scheme)) {
            if (uri.getPath().endsWith("/")) {
                return "";
            } else {
                return new java.io.File(uri.getPath()).getName();
            }
        } else if ("jar".equals(scheme)) {
            return getFilename(URI.create(uri.getSchemeSpecificPart()));
        } else if ("http".equals(scheme) || "https".equals(scheme)) {
            var path = uri.getPath();
            var p = path.lastIndexOf('/');
            if (p > -1) {
                return path.substring(p + 1);
            } else {
                return "";
            }
        } else {
            // You can try adding it above and see if it works...
            throw new IllegalArgumentException("TODO Add support for new URI scheme: " + uri);
        }
    }

    private URIs() {}
}

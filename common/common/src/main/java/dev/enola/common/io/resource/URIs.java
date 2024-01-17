/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2024 The Enola <https://enola.dev> Authors
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
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.MediaTypes;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class URIs {

    // URI Query Parameter Names
    private static final String MEDIA_TYPE = "mediaType";
    private static final String CHARSET = "charset";

    private static final Splitter AMPERSAND_SPLITTER =
            Splitter.on('&').omitEmptyStrings().trimResults();

    public static final MediaType DEFAULT_MEDIA_TYPE = MediaType.OCTET_STREAM;

    public static MediaType getMediaType(URI uri) {
        MediaType mediaType;
        var charsetParameter = getQueryMap(uri).get(CHARSET);
        var mediaTypeParameter = getQueryMap(uri).get(MEDIA_TYPE.toLowerCase());
        if (mediaTypeParameter == null) {
            mediaType = DEFAULT_MEDIA_TYPE;
        } else {
            mediaType = MediaTypes.parse(mediaTypeParameter);
        }
        if (charsetParameter != null) {
            mediaType = mediaType.withCharset(Charset.forName(charsetParameter));
        }
        if (!mediaType.charset().isPresent()) {
            mediaType = mediaType.withCharset(Charset.defaultCharset());
        }
        return mediaType;
    }

    public static URI addMediaType(URI uri, MediaType mediaType) {
        return addQueryParameter(uri, MEDIA_TYPE, mediaType.toString().replace(" ", ""));
    }

    private static URI addQueryParameter(URI uri, String key, String value) {
        String connector;
        if (uri.getQuery() != null && uri.getQuery().contains(key)) {
            return uri;
        } else if (uri.getQuery() == null) {
            if (!uri.getSchemeSpecificPart().contains("?")) {
                connector = "?";
            } else {
                // Special case of "scheme:?"-like URIs with "empty" query
                connector = "";
            }
        } else {
            connector = "&";
        }
        return URI.create(uri + connector + key + "=" + encodeQueryParameterValue(value));
    }

    private static String encodeQueryParameterValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    // package-local not public (for now)
    static Map<String, String> getQueryMap(URI uri) {
        if (uri == null) return Collections.emptyMap();
        Map<String, String> map = new HashMap<>();
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
        AMPERSAND_SPLITTER.split(query).forEach(queryParameter -> put(uri, queryParameter, map));
        return map;
    }

    private static void put(URI uri, String queryParameter, Map<String, String> map) {
        var p = queryParameter.indexOf('=');
        if (p == -1) {
            map.put(queryParameter, null);
        } else {
            var key = queryParameter.substring(0, p);
            if (map.containsKey(key))
                throw new IllegalArgumentException(
                        uri.toString() + " ID URI ?query has duplicate key");
            var value = queryParameter.substring(p + 1);
            map.put(key.toLowerCase(), value);
        }
    }

    /**
     * Get the "path"-like component of any URI. Similar to {@link URI#getPath()}, but also works
     * e.g. for "non-standard" relative "file:hello.txt" URIs, and correctly chops off query
     * arguments and fragments.
     */
    public static String getPath(URI uri) {
        var ssp = uri.getSchemeSpecificPart();

        var chop = ssp.indexOf('?', 0);
        if (chop == -1) chop = ssp.indexOf('#', 0);
        if (chop == -1) chop = ssp.length();

        return ssp.substring(0, chop);
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
        // TODO This should probably use the (new) getPath()?
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

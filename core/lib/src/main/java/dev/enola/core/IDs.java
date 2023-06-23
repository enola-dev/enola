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

import static com.google.common.base.Strings.isNullOrEmpty;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;

import dev.enola.core.proto.ID;
import dev.enola.core.proto.IDOrBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * ID formatting and parsing functions. {@link ID} can be formatted to and parsed from the following
 * forms (inspired by e.g. https://en.m.wikipedia.org/wiki/List_of_URI_schemes and
 * https://en.m.wikipedia.org/wiki/Uniform_Resource_Name):
 *
 * <ul>
 *   <li>Our: demo.foo/abc/def
 *   <li>URI: enola:demo.foo/abc/def
 *   <li>URN: urn:enola.dev:demo.foo:abc:def
 * </ul>
 */
public final class IDs {

    private static final String URI_SCHEME = "enola";
    private static final String URI_SCHEME_COLON = URI_SCHEME + ':';
    private static final Splitter SLASH_SPLITTER =
            Splitter.on('/').omitEmptyStrings().trimResults();

    // private static final Joiner AMPERSAND_JOINER = Joiner.on('&').skipNulls();

    private IDs() {}

    public static ID parse(String s) {
        if (s.startsWith(URI_SCHEME_COLON)) {
            URI uri = URI.create(s);
            // return parseOur(uri.getSchemeSpecificPart());
            return parseOur(s.substring(URI_SCHEME_COLON.length()));
        } else {
            return parseOur(s);
        }
    }

    private static ID parseOur(String s) {
        var id = ID.newBuilder();
        var firstSlash = s.indexOf('/');
        if (firstSlash == -1) {
            parseNamespaceAndEntity(s, id);
        } else {
            parseNamespaceAndEntity(s.substring(0, firstSlash), id);
            for (var segment : SLASH_SPLITTER.split(s.substring(firstSlash))) {
                id.addPaths(segment);
            }
        }
        return id.build();
    }

    private static void parseNamespaceAndEntity(String s, ID.Builder id) {
        var lastDot = s.lastIndexOf('.');
        if (lastDot > -1) {
            id.setNs(s.substring(0, lastDot));
            id.setEntity(s.substring(lastDot + 1));
        } else {
            id.setEntity(s);
        }
    }

    public static URI toPathURI(IDOrBuilder id) {
        try {
            // Do NOT use #anchors, as that would make anchors on HTML UIs harder.
            return new URI(URI_SCHEME, toPath(id), null);
        } catch (URISyntaxException e) {
            // Should be "impossible", given that the code validated the characters..
            throw new IllegalStateException("This should never happen!! :-(", e);
        }
    }

    public static String toPath(IDOrBuilder id) {
        // TODO Validate ID components for valid characters etc, as per spec in its proto
        var sb = new StringBuilder(id.getNs());
        if (sb.length() > 0) {
            sb.append('.');
        }
        sb.append(id.getEntity());
        if (id.getPathsCount() > 0) {
            sb.append('/');
            for (int i = 0; i < id.getPathsCount(); i++) {
                sb.append(id.getPaths(i));
                if (i < id.getPathsCount() - 1) {
                    sb.append('/');
                }
            }
        }
        return sb.toString();
    }

    // TODO public static String toURN(ID id) {

    /* TODO Re-implement later - or ditch?

         * In text (string) form, it "looks" like an RFC 2396 Uniform Resource
         * Identifier (URI, not URN), but this is just we want some "standard"-like
         * format which humans are used to seeing. The real syntax is actually simpler
         * and quite a bit more restricted, see
         * https://en.wikipedia.org/wiki/Uniform_Resource_Identifier#Syntax:
         * The "authority" (//...) is not used (and IDs with them are rejected; see
         * IDsTest.java).
         * Things like a "cluster" or a "rack" or whatever "hierarchies" an entity may
         * have are, by convention, not part of the entity (path), but represented as
         * part of the query; for example:
         *   k8s:pod?network=prod&context=demo&namespace=test&name=hello
         * * The "path" is not actually a path, but just a name of a kind of entity
         *   within that scheme. It cannot contain slashes.
         * * The "query" (?...) is used to identify a specific entity (with one or
         *   several names or UUID or whatever; specific to each entity).
         * * The "fragment" (#...) is not used (and IDs with them are rejected; see
         *   IDsTest.java).
        public static ID parse(String s) {
            java.net.URI uri = java.net.URI.create(s);
            ID.Builder builder = ID.newBuilder();

            if (uri.getScheme() == null) {
                throw new IllegalArgumentException(s + " ID URI has no scheme: " + uri);
            }
            builder.setNs(uri.getScheme());

            if (uri.getAuthority() != null) {
                throw new IllegalArgumentException(s + " ID URI cannot have an //authority: " + uri);
            }

            if (uri.getFragment() != null) {
                throw new IllegalArgumentException(s + " ID URI cannot have an #fragment: " + uri);
            }

            if (uri.getSchemeSpecificPart() == null) {
                throw new IllegalArgumentException(s + " ID URI has no path: " + uri);
            }
            String path = uri.getSchemeSpecificPart();
            int idx = path.indexOf('?');
            if (idx == -1) builder.setEntity(uri.getSchemeSpecificPart());
            else {
                String entity = path.substring(0, idx);
                builder.setEntity(entity);

                Map<String, String> map = IDs.getQueryMap(uri);
            }

            // TODO builder.setSegments(i, segment)

            return builder.build();
        }

        // KEEP the original Query format, but use enola: as scheme,
        // not the package 📦 as originally, that does into a dotted package
        // and entity name; so e.g. enola:dev.enola.demo/foo?name=abc
        public static String toQueryURI(ID id) {
            if (Strings.isNullOrEmpty(id.getScheme()))
                throw new IllegalArgumentException("ID proto has no scheme: " + id);
            StringBuffer sb = new StringBuffer(id.getScheme());
            sb.append(':');
            if (Strings.isNullOrEmpty(id.getEntityKind()))
                throw new IllegalArgumentException("ID proto has no entity: " + id);
            sb.append(id.getEntityKind());

            if (id.getSegmentsCount() > 0) sb.append('?');
            sb.append(
                    AMPERSAND_JOINER.join(
                            parts.getQueryMap().entrySet().stream()
                                    .map(
                                            pair -> {
                                                StringBuilder pairSB = new StringBuilder(pair.getKey());
                                                if (pair.getValue() != null) {
                                                    pairSB.append('=');
                                                    pairSB.append(pair.getValue());
                                                }
                                                return pairSB.toString();
                                            })
                                    .iterator()));

            return sb.toString();
        }
    */

    public static Map<String, String> pathMap(ID kindID, ID entityID) {
        if (entityID.getPathsCount() != kindID.getPathsCount()) {
            throw new IllegalArgumentException(
                    "Missing (or extra) paths! Entity ID="
                            + entityID
                            + "; EntityKind ID="
                            + kindID);
        }
        var n = kindID.getPathsCount();
        var builder = ImmutableMap.<String, String>builderWithExpectedSize(n);
        for (int i = 0; i < n; i++) {
            builder.put(kindID.getPaths(i), entityID.getPaths(i));
        }
        return builder.build();
    }

    public static ID withoutPath(ID id) {
        if (id == null) return null;
        if (id.getPathsCount() == 0) return id;
        return ID.newBuilder(id).clearPaths().build();
    }

    public static boolean isEmpty(ID id) {
        return isNullOrEmpty(id.getNs())
                && isNullOrEmpty(id.getEntity())
                && id.getPathsCount() == 0;
    }
}

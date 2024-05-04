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
package dev.enola.core;

import static com.google.common.base.Strings.isNullOrEmpty;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;

import dev.enola.common.io.iri.template.URITemplateSplitter;
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
 *   <li>URN: urn:enola:demo.foo/abc/def
 * </ul>
 *
 * This is the predecessor of {@link ERI}. The intention is to eventually completely remove this.
 */
public final class IDs {

    private static final String URI_SCHEME = "enola";
    private static final String URI_SCHEME_COLON = URI_SCHEME + ':';
    private static final Splitter SLASH_SPLITTER =
            Splitter.on('/').omitEmptyStrings().trimResults();

    // private static final Joiner AMPERSAND_JOINER = Joiner.on('&').skipNulls();

    private static final ID.Builder EK_ID_TEMPLATE =
            ID.newBuilder().setNs("enola").setEntity("entity_kind");

    private IDs() {}

    public static ID parse(String s) {
        if (s.startsWith(URI_SCHEME_COLON)) {
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

    private static StringBuilder ekPath(IDOrBuilder id) {
        // TODO Validate ID components for valid characters etc, as per spec in its proto
        var sb = new StringBuilder(id.getNs());
        if (sb.length() > 0) {
            sb.append('.');
        }
        sb.append(id.getEntity());
        return sb;
    }

    public static String toPath(IDOrBuilder id) {
        var sb = ekPath(id);
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

    /**
     * Transform ID into a RFC 6570 URI Template which can be used with an {@link
     * URITemplateSplitter} or an {@link com.github.fge.uritemplate.URITemplate}.
     */
    public static String toURITemplate(IDOrBuilder id) {
        var sb = ekPath(id);
        if (id.getPathsCount() > 0) {
            sb.append('/');
            for (int i = 0; i < id.getPathsCount(); i++) {
                sb.append('{');
                sb.append(id.getPaths(i));
                sb.append('}');
                if (i < id.getPathsCount() - 1) {
                    sb.append('/');
                }
            }
        }

        return sb.toString();
    }

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

    public static ID.Builder entityKind(ID id) {
        return EK_ID_TEMPLATE.clone().addPaths(ekPath(id).toString());
    }
}

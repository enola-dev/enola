/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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

import static java.util.Collections.emptyMap;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.common.net.MediaType;

import dev.enola.common.context.Context;
import dev.enola.common.context.TLC;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class URIs {
    // see also class dev.enola.common.io.iri.IRIs

    // TODO Eventually completely replace all this with the intended new IRI class!

    // TODO Review if all this String instead of URI-based processing could be removed and replaced
    // perhaps by fully adopting https://github.com/enola-dev/enola/issues/797; and/or alternatively
    // with first encoding invalid special characters in URIs; see the related TBD in
    // FileGlobResolver.

    // URI Query Parameter Names
    private static final String MEDIA_TYPE = "mediaType";
    private static final String CHARSET = "charset"; // as in MediaType#CHARSET_ATTRIBUTE

    private static final Splitter AMPERSAND_SPLITTER =
            Splitter.on('&').omitEmptyStrings().trimResults();

    public record MediaTypeAndOrCharset(@Nullable String mediaType, @Nullable String charset) {}

    public static boolean hasQueryParameter(URI uri, String key) {
        var queryMap = getQueryMap(uri);
        return queryMap.containsKey(key);
    }

    public static MediaTypeAndOrCharset getMediaTypeAndCharset(URI uri) {
        var queryMap = getQueryMap(uri);
        var charsetParameter = queryMap.get(CHARSET);
        var mediaTypeParameter = queryMap.get(MEDIA_TYPE.toLowerCase());
        if (mediaTypeParameter == null) mediaTypeParameter = queryMap.get(MEDIA_TYPE);
        return new MediaTypeAndOrCharset(mediaTypeParameter, charsetParameter);
    }

    public static @Nullable String getCharset(URI uri) {
        var mediaTypeAndCharset = getMediaTypeAndCharset(uri);
        if (mediaTypeAndCharset.charset != null) return mediaTypeAndCharset.charset;
        if (mediaTypeAndCharset.mediaType != null) {
            var optMediaTypeCharset = MediaType.parse(mediaTypeAndCharset.mediaType);
            if (optMediaTypeCharset.charset().isPresent())
                return optMediaTypeCharset.charset().get().toString();
        }
        return null;
    }

    public static URI addMediaType(URI uri, MediaType mediaType) {
        return addQueryParameter(uri, MEDIA_TYPE, mediaType.toString().replace(" ", ""));
    }

    public static URI addCharset(URI uri, Charset charset) {
        return addQueryParameter(uri, CHARSET, charset.toString());
    }

    public static URI addQuery(URI uri, Map<String, String> parameters) {
        for (var parameter : parameters.entrySet()) {
            uri = addQueryParameter(uri, parameter.getKey(), parameter.getValue());
        }
        return uri;
    }

    /**
     * Returns an URI with everything except the query parameters of the uri (1st) parameter, but
     * the query parameters of originalUriWithQuery (2nd) parameter - IFF the uri (1st) parameter
     * has no query; otherwise just returns the uri (1st) parameter as-is.
     *
     * <p>For example, this method can be used to preserve query parameters when changing the URI
     * path.
     */
    public static URI addQuery(URI uri, URI originalUriWithQuery) {
        if (Strings.isNullOrEmpty(originalUriWithQuery.getQuery())) return uri;
        if (!Strings.isNullOrEmpty(uri.getQuery())) return uri;

        return java.net.URI.create(uri + "?" + originalUriWithQuery.getQuery());
    }

    public static String addQuery(String string, Map<String, String> parameters) {
        return addQuery(java.net.URI.create(string), parameters).toString();
    }

    private static URI addQueryParameter(URI uri, String key, String value) {
        String connector;
        if (uri.getQuery() != null && uri.getQuery().contains(key)) {
            return uri;
        } else if (uri.getQuery() == null && !uri.getSchemeSpecificPart().contains("?")) {
            connector = "?";
        } else if (uri.getQuery() == null && uri.getSchemeSpecificPart().equals("?")) {
            // Handle special (but technically invalid cases) of "scheme:?" URIs with "empty" query
            // This is currently used by EmptyResource.EMPTY_URI (but may be changed later)
            connector = "";
        } else {
            connector = "&";
        }
        return java.net.URI.create(uri + connector + key + "=" + encodeQueryParameterValue(value));
    }

    private static String encodeQueryParameterValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    // package-local not public (for now)
    static String getQueryString(String uri) {
        var qp = uri.indexOf('?');
        if (qp > -1) {
            // Handle Glob URIs, like e.g. "file:/tmp//?.txt"
            if (uri.indexOf('=', qp) == -1) return "";
            var fp = uri.indexOf('#');
            if (fp == -1) {
                return uri.substring(qp + 1);
            } else {
                return uri.substring(qp + 1, fp);
            }
        }
        return "";
    }

    public static Map<String, String> getQueryMap(String uri) {
        return getQueryMapGivenQueryString(getQueryString(uri));
    }

    private static Map<String, String> getQueryMapGivenQueryString(String query) {
        if (Strings.isNullOrEmpty(query)) {
            return emptyMap();
        }
        Map<String, String> map = new HashMap<>();
        AMPERSAND_SPLITTER.split(query).forEach(queryParameter -> put(queryParameter, map));
        return map;
    }

    public static Map<String, String> getQueryMap(URI uri) {
        if (uri == null) return emptyMap();

        String query = uri.getQuery();
        if (Strings.isNullOrEmpty(query)) {
            var part = uri.getSchemeSpecificPart();
            try {
                query = getQueryString(part);
            } catch (StringIndexOutOfBoundsException e) {
                throw new IllegalStateException(uri.toString(), e);
            }
        }
        return getQueryMapGivenQueryString(query);
    }

    private static void put(String queryParameter, Map<String, String> map) {
        var p = queryParameter.indexOf('=');
        if (p == -1) {
            map.put(queryParameter, null);
        } else {
            var key = queryParameter.substring(0, p);
            if (map.containsKey(key))
                throw new IllegalArgumentException(
                        "URI Query Parameter has duplicate key: " + queryParameter);
            var value = queryParameter.substring(p + 1);
            map.put(key, value);
        }
    }

    /**
     * Get a {@link Path} from an {@link URI}. This method is used internally by {@code
     * dev.enola.common.io.resource.Resource} framework implementations, and typically shouldn't be
     * called directly by users. Please see the {@code dev.enola.common.io.resource.FileResource}
     * for more related background.
     */
    public static Path getFilePath(URI uri) {
        uri = absolutify(uri);
        uri = dropQueryAndFragment(uri);
        try {
            return Path.of(uri);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid URI: " + uri, e);
        }
    }

    public static Path getFilePath(String uri) {
        var scheme = getScheme(uri);
        var authority = ""; // TODO Implement getAuthority(String uri)
        var path = getPath(uri);
        return getFilePath(scheme, "", path);
    }

    private static Path getFilePath(String scheme, String authority, String path) {
        // TODO Don't hard-code this to file: but use MoreFileSystems.URI_SCHEMAS, somehow...
        if ("file".equals(scheme)) {
            return FileSystems.getDefault().getPath(path);
        } else
            try {
                URI fsURI = new URI(scheme, authority, null, null, null);
                var fs = FileSystems.getFileSystem(fsURI);
                return fs.getPath(path);
            } catch (URISyntaxException e) {
                // This is rather unexpected...
                throw new IllegalStateException(
                        "Failed to create FileSystem Authority URI: " + scheme + ":" + path, e);
            }
    }

    public static String getScheme(String iri) {
        if (iri == null) return "";
        var p = iri.indexOf(':');
        if (p == -1) return "";
        return iri.substring(0, p);
    }

    public static boolean hasScheme(String iri) {
        return iri.indexOf(':') > 0;
    }

    private static boolean hasScheme(URI uri) {
        return !Strings.isNullOrEmpty(uri.getScheme());
    }

    static String getSchemeSpecificPart(String iri) {
        if (iri == null) return "";
        var p = iri.indexOf(':');
        if (p == -1) return "";
        return iri.substring(p + 1);
    }

    /**
     * Get the "path"-like component of any URI. Similar to {@link URI#getPath()}, but also works
     * e.g. for "non-standard" relative "file:hello.txt" URIs, and correctly chops off query
     * arguments and fragments.
     *
     * <p>TODO This does not yet decode correctly! :=((
     *
     * <p>TODO Is this really required?! Re-review which tests URI#getPath() fails, and why...
     */
    @Deprecated // Get rid of this, it's stupid, buggy, and bad.
    // TODO This doesn't correctly handle URIs with an authority!
    public static String getPath(URI uri) {
        return chopFragmentAndQuery(uri.getSchemeSpecificPart());
    }

    public static String getPath(String uri) {
        return chopFragmentAndQuery(getSchemeSpecificPart(uri));
    }

    private static String chopFragmentAndQuery(String ssp) {
        var chop = ssp.indexOf('?');

        // Handle Glob URIs, like e.g. "file:/tmp//?.txt"
        if (ssp.indexOf('=', chop) == -1) chop = -1;

        if (chop == -1) chop = ssp.indexOf('#');
        if (chop == -1) chop = ssp.length();

        return ssp.substring(0, chop);
    }

    private static String chopLastSlash(String path) {
        if (!path.endsWith("/")) return path;
        else return path.substring(0, path.length() - 1);
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
        return getLastPathSegmentOrHost(uri, false);
    }

    public static String getFilenameWithoutExtension(URI uri, String... extensions) {
        var fileName = getFilename(uri);
        for (String extension : extensions) {
            if (fileName.endsWith(extension))
                return fileName.substring(0, fileName.length() - extension.length());
        }
        return Files.getNameWithoutExtension(fileName);
    }

    public static boolean hasExtension(URI uri, String... extensions) {
        var path = uri.getPath();
        if (path == null) return false;
        for (String extension : extensions) if (path.endsWith(extension)) return true;
        return false;
    }

    public static String getFilenameOrLastPathSegmentOrHost(URI uri) {
        return getLastPathSegmentOrHost(uri, true);
    }

    private static String getLastPathSegmentOrHost(URI uri, boolean evenIfSlashed) {
        String path;
        var scheme = uri.getScheme();
        if ("file".equals(scheme)) {
            path = chopFragmentAndQuery(uri.getPath());
        } else if ("jar".equals(scheme)) {
            return chopFragmentAndQuery(
                    getFilename(java.net.URI.create(uri.getSchemeSpecificPart())));
        } else if ("http".equals(scheme) || "https".equals(scheme)) {
            path = chopFragmentAndQuery(uri.getPath());
        } else {
            path = getPath(uri);
        }

        if (!evenIfSlashed && (Strings.isNullOrEmpty(path) || path.endsWith("/"))) {
            return "";
        }
        if (evenIfSlashed && (Strings.isNullOrEmpty(path) || "/".equals(path))) {
            var host = uri.getHost();
            return host;
        }

        int p;
        if (!path.endsWith("/")) p = path.lastIndexOf('/');
        else p = path.substring(0, path.length() - 1).lastIndexOf('/');

        return p > -1 ? chopLastSlash(path.substring(p + 1)) : chopLastSlash(path);
    }

    /**
     * This converts e.g. "file:relative.txt" to "file:///tmp/.../relative.txt" (depending on the
     * current working directory, obviously). It loses any query parameters and fragments.
     */
    @Deprecated // TODO Get rid of the support for the fake "file:relative.txt" syntax
    public static URI rel2abs(URI uri) {
        if (uri == null) return uri;
        if (!"file".equals(uri.getScheme())) return uri;
        if (!uri.isOpaque()) return uri;

        String relativePath = uri.getSchemeSpecificPart();
        return Path.of(relativePath).toAbsolutePath().toUri();
    }

    public static URI absolutify(URI uri) {
        if (hasScheme(uri)) return uri;
        var base = TLC.optional(ContextKeys.BASE).orElseThrow(ex(uri));
        return base.resolve(uri);
    }

    public static String absolutify(String uri) {
        if (hasScheme(uri)) return uri;
        var base = TLC.optional(ContextKeys.BASE).orElseThrow(ex(uri));
        return resolve(base.toString(), uri);
    }

    private static String resolve(String base, String child) {
        if (!child.startsWith("/")) return base + child;
        return getScheme(base) + "://" + child;
    }

    private static Supplier<IllegalStateException> ex(Object uri) {
        return () ->
                new IllegalStateException("Missing ContextKeys.BASE on TLC to resolve: " + uri);
    }

    public static URI dropQueryAndFragment(URI uri) {
        if (uri.getRawQuery() == null && uri.getRawFragment() == null) return uri;
        try {
            return new URI(
                    uri.getScheme(),
                    uri.getUserInfo(),
                    uri.getHost(),
                    uri.getPort(),
                    uri.getPath(),
                    null,
                    null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("TODO FIXME" + uri, e);
        }
    }

    public static String dropQueryAndFragment(String uri) {
        return dropQueryAndFragment(URI.create(uri)).toString();
    }

    public static URI getBase(URI uri) throws IOException {
        try {
            var path = uri.getPath();
            if (path == null)
                // throw new IllegalArgumentException("Cannot get Path from: " + uri);
                return URI.create("");
            if (path.endsWith("/"))
                if (uri.getQuery() == null && uri.getFragment() == null) return uri;
                // NB: Both query and fragment are *intentionally* dropped in this case!
                else return new URI(uri.getScheme(), uri.getAuthority(), path, null);

            path = path.substring(0, path.lastIndexOf('/'));
            // Again, same as above, we're ignoring query & fragment
            return new URI(uri.getScheme(), uri.getAuthority(), path, null);
        } catch (URISyntaxException e) {
            throw new IOException("TODO Why is this new URI invalid: " + uri, e);
        }
    }

    public static URI addFragment(URI uri, String fragment) {
        if (Strings.isNullOrEmpty(fragment)) return uri;
        try {
            return new URI(
                    uri.getScheme(),
                    uri.getAuthority(),
                    uri.getPath(),
                    uri.getQuery(),
                    (uri.getFragment() != null ? uri.getFragment() : "") + fragment);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Failed to addFragment", e);
        }
    }

    // NB: There is intentionally no URI create(String uri) method here!
    // Do simply use {@link URI#create(String)} or {@link URI#URI(String)}.
    // If you find that it does not correctly set the URI's Query, just use
    // "scheme:/thing?ping=pong=pang#fragment" instead of "scheme:thing?ping=pong=pang#fragment".
    /*
        public static URI create(String uri) {
            String scheme = getScheme(uri);
            String authority = null;
            String path = getPath(uri);
            String query = getQueryString(uri);
            String fragment = getFragment(uri);
            try {
                // This fails if path doesn't start with '/' so this entire method is pointless!
                return new URI(scheme, authority, path, query, fragment);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Invalid Syntax: " + uri, e);
            }
        }

        private static String getFragment(String uri) {
            return uri.lastIndexOf('#') == -1 ? uri : uri.substring(uri.lastIndexOf('#') + 1);
        }
    */

    // TODO Review if getScheme(), getPath(), getQueryString(), getFragment() are *REALLY* needed?!

    @Deprecated // TODO This automagic " " => %20 is fishy... who needs this, why?!
    public static String encode(String uri) {
        return uri.replace(" ", "%20");
    }

    public static URI parse(String uri) throws URISyntaxException {
        return new URI(encode(uri));
    }

    private URIs() {}

    public enum ContextKeys implements Context.Key<URI> {

        /**
         * Base URI. Used to resolve <a
         * href="https://en.wikipedia.org/wiki/Uniform_Resource_Identifier#URI_references">relative
         * reference URIs</a> which don't have a scheme.
         */
        // TODO Support for this is new and emerging, and not yet fully comprehensive...
        BASE
    }
}

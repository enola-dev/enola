/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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

import static com.google.common.net.MediaType.parse;

import com.google.common.base.Strings;
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.MediaTypes;

import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Resource I/O implementation for RFC 2397 <a
 * href="https://en.m.wikipedia.org/wiki/Data_URI_scheme">data: URLs</a>.
 *
 * <p>@see {@link MultibaseResource} for another similar URL scheme.
 */
public class DataResource extends BaseResource implements ReadableButNotWritableResource {

    // This is a "clean room" implementation, which is hopefully correct. Alternatives:
    // - https://github.com/robtimus/data-url
    // - https://github.com/maxschuster/DataUrl
    // - https://github.com/ooxi/jdatauri

    private static final String SCHEME = "data";
    private static final Pattern DATA_URL_REGEX = Pattern.compile("^data:([^,]*),(.*)$");
    private static final Charset DATA_DEFAULT_CHARSET = StandardCharsets.US_ASCII;
    private static final MediaType DEFAULT_MEDIA_TYPE =
            parse("text/plain;charset=" + DATA_DEFAULT_CHARSET);
    private static final byte[] EMPTY_BYTES = new byte[0];

    public static class Provider implements ResourceProvider {
        @Override
        public Resource getResource(URI uri) {
            if (SCHEME.equals(uri.getScheme())) return new DataResource(uri);
            else return null;
        }
    }

    private final ByteSource byteSource;

    // TODO Rewrite using https://blog.jetbrains.com/idea/2024/02/constructor-makeover-in-java-22/
    // once we're on Java >21 (and avoid having to match the RegExp twice, which is inefficient)

    private static URI checkSchema(URI uri) {
        if (!SCHEME.equals(uri.getScheme())) throw new IllegalArgumentException(uri.toString());
        return uri;
    }

    private static MediaType extractMediaType(URI uri) {
        var matcher = DATA_URL_REGEX.matcher(uri.toString());
        if (!matcher.matches()) throw new IllegalArgumentException(uri.toString());

        var mediaTypePart = matcher.group(1);
        if (mediaTypePart == null || mediaTypePart.isEmpty()) return DEFAULT_MEDIA_TYPE;

        String mediaType;
        int lastSemicolonIndex = mediaTypePart.lastIndexOf(';');
        if (lastSemicolonIndex != -1
                && ";base64".equals(mediaTypePart.substring(lastSemicolonIndex))) {
            mediaType = mediaTypePart.substring(0, lastSemicolonIndex);
            if (mediaType.isEmpty()) mediaType = DEFAULT_MEDIA_TYPE.toString();
        } else mediaType = mediaTypePart;
        return parse(mediaType);
    }

    private static byte[] extractBytes(URI uri) {
        var matcher = DATA_URL_REGEX.matcher(uri.toString());
        if (!matcher.matches()) throw new IllegalArgumentException(uri.toString());

        String mediaTypePart = matcher.group(1);
        String encodedData = matcher.group(2);
        if (encodedData == null) return EMPTY_BYTES; // skipcq: JAVA-S1049

        if (mediaTypePart.contains("base64"))
            // We CANNOT use Guava's BaseEncoding.base64Url(), that's different
            return java.util.Base64.getDecoder().decode(encodedData);
        else
            return URLDecoder.decode(encodedData, DATA_DEFAULT_CHARSET)
                    .getBytes(DATA_DEFAULT_CHARSET);
    }

    // TODO public static Resource of(ByteSource byteSource, MediaType mediaType) {
    //   java.util.Base64.getEncoder().withoutPadding().encode(byteSource.read());

    public static Resource of(@Nullable String text, @Nullable MediaType mediaType) {
        var data = MediaTypes.toStringWithoutSpaces(mediaType) + "," + Strings.nullToEmpty(text);
        try {
            return new DataResource(new URI(SCHEME, data, null));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(data, e);
        }
    }

    public static Resource of(@Nullable String text) {
        return of(text, null);
    }

    // NB: A DataResource(URI uri, MediaType mediaType) constructor does not make sense here!

    public DataResource(URI uri) {
        super(checkSchema(uri), extractMediaType(uri));
        byteSource = ByteSource.wrap(extractBytes(uri));
    }

    @Override
    public ByteSource byteSource() {
        return byteSource;
    }
}

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

import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import dev.enola.common.io.iri.URIs;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.mediatype.MediaTypes;
import dev.enola.common.io.mediatype.YamlMediaType;

import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Utility for detecting a (better) {@link MediaType} for a Resource.
 *
 * <p>This class is intentionally package private, and should stay so; this is NOT for {@link
 * Resource} API users (who would just use {@link AbstractResource#mediaType()}); it's only used by
 * *Resource SPI implementations. (Which technically makes it impossible to {easily} write *Resource
 * implementations which do not extend BaseResource outside of this package; but with the current
 * mono-repo architecture, that's just fine.)
 */
class MediaTypeDetector {
    // NB: This class should *NEVER* implement MediaTypeProvider - that's a separate concern!

    // Default to "application/octet-stream", as per e.g.
    // https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
    private static final MediaType DEFAULT = com.google.common.net.MediaType.OCTET_STREAM;

    // TODO Merge IGNORE & TRY_FIXING
    // Ignores certain known to be wrong (bad, invalid) content types
    private static final Set<MediaType> IGNORE =
            ImmutableSet.of(
                    // java.net.URLConnection returns this when there is no content-type header
                    MediaType.parse("content/unknown"));
    private static final Set<MediaType> TRY_FIXING =
            ImmutableSet.of(
                    // raw.githubusercontent.com returns "text/plain" e.g. for *.yaml
                    MediaType.parse("text/plain"));

    private static boolean isSpecial(MediaType mediaType) {
        var mediaTypeWithoutParameters = mediaType.withoutParameters();
        return TRY_FIXING.contains(mediaTypeWithoutParameters)
                || IGNORE.contains(mediaTypeWithoutParameters)
                || DEFAULT.equals(mediaTypeWithoutParameters);
    }

    /**
     * This is called by Resource* implementation constructors, typically via {@link BaseResource},
     * if there is (only) an URI and a ByteSource - but no original/client requested MediaType.
     */
    MediaType detect(URI uri, ByteSource byteSource) {
        var mediaTypeCharset = URIs.getMediaTypeAndCharset(uri);
        var detected = detect(mediaTypeCharset.mediaType(), mediaTypeCharset.charset(), uri);
        detected = detectCharsetAndMediaType(uri, byteSource, detected);
        return detected;
    }

    /**
     * This is called by Resource* implementation constructors, typically via {@link BaseResource},
     * if there is (only) an URI and an original/client requested MediaType already. In this case,
     * there is no point in also considering a ByteSource.
     */
    MediaType overwrite(URI uri, final MediaType originalMediaType) {
        var mediaType = originalMediaType;

        // TODO Move this into UrlResource if "content/unknown" is hard-coded in URLConnection?
        if (IGNORE.contains(mediaType.withoutParameters())) {
            mediaType = MediaType.OCTET_STREAM;
        }

        var uriCharset = URIs.getMediaTypeAndCharset(uri);
        var uriMediaType = uriCharset.mediaType();
        if (uriMediaType != null) mediaType = MediaType.parse(uriMediaType);

        var cs = uriCharset.charset();
        if (cs != null) mediaType = mediaType.withCharset(Charset.forName(cs));
        else {
            if (!mediaType.charset().isPresent() && originalMediaType.charset().isPresent()) {
                mediaType = mediaType.withCharset(originalMediaType.charset().get());
            } else {
                mediaType = detectCharsetAndMediaType(uri, ByteSource.empty(), mediaType);
            }
        }
        mediaType = fixMissingCharset(mediaType);
        return mediaType;
    }

    private MediaType detectCharsetAndMediaType(
            URI uri, ByteSource byteSource, MediaType detected) {
        if (!detected.charset().isPresent()) {
            // TODO Make YAML just 1 of many Charset detectors...
            YamlMediaType rcd = new YamlMediaType();
            if (URIs.getFilename(uri).endsWith(".yaml")
                    || MediaTypes.normalizedNoParamsEquals(
                            detected, rcd.knownTypesWithAlternatives().keySet())) {
                var detectedCharset = rcd.detectCharset(uri, byteSource);
                if (detectedCharset.isPresent()) {
                    detected = detected.withCharset(detectedCharset.get());
                }
            }
        }
        if (!isSpecial(detected)) return detected;
        else return MediaTypeProviders.SINGLETON.get().detect(uri.toString(), byteSource, detected);
    }

    private MediaType fixMissingCharset(MediaType mediaType) {
        if (mediaType.charset().isPresent()) return mediaType;
        // TODO Replace this with a more "pluggable" instead of this initial hard-coded design
        if (mediaType.is(MediaType.ANY_TEXT_TYPE)) {
            // TODO Remove this; it's wrong! Generic text cannot just be assumed to be UTF-8!
            mediaType = mediaType.withCharset(StandardCharsets.UTF_8);
            // TODO This should move into a TBD JsonMediaType implements ResourceCharsetDetector
        } else if (mediaType.is(MediaType.JSON_UTF_8.withoutParameters())) {
            // TODO See ResourceCharsetDetector above; implement JSON BOM detection à la §3 from
            // https://www.ietf.org/rfc/rfc4627.txt in a new class JsonResourceCharsetDetector
            mediaType = mediaType.withCharset(StandardCharsets.UTF_8);
        } else if (mediaType.subtype().endsWith("+json")) {
            mediaType = mediaType.withCharset(StandardCharsets.UTF_8);
        }
        // TODO NOT if (mediaType.subtype().endsWith("+yaml")) { but via YamlMediaType
        return mediaType;
    }

    // This is currently still used by both UrlResource & OkHttpResource (and MediaTypeDetectorTest)
    // but this is conceptually the same as the overwrite(URI uri, MediaType originalMediaType)
    // TODO Switch OkHttpResource to use that instead
    // TODO Make private (or inline and remove)
    MediaType detect(@Nullable String contentType, @Nullable String contentEncoding, URI uri) {
        MediaType mediaType = null;
        if (contentType != null) {
            mediaType = MediaTypes.parse(contentType);
            if (TRY_FIXING.contains(mediaType.withoutParameters())
                    || IGNORE.contains(mediaType.withoutParameters())) {
                mediaType = null;
            }
        }

        if (mediaType == null) {
            if (contentType == null) mediaType = DEFAULT;
            else {
                mediaType = MediaTypes.parse(contentType);
                if (IGNORE.contains(mediaType.withoutParameters())) {
                    mediaType = DEFAULT;
                }
            }
        }

        if (contentEncoding != null) {
            mediaType = mediaType.withCharset(Charset.forName(contentEncoding));
        } else {
            mediaType = fixMissingCharset(mediaType);
        }

        return mediaType;
    }
}

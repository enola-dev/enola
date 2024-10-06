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
package dev.enola.common.io.mediatype;

import com.google.common.net.MediaType;

import java.util.Optional;
import java.util.Set;

/** Extension methods for (Guava's) {@link MediaType}. */
// See also dev.enola.format.tika.TikaMediaTypes for its.
public final class MediaTypes {
    private MediaTypes() {}

    /**
     * Improved version of {@link MediaType#parse(String)} which also invokes {@link
     * #normalize(MediaType)}.
     */
    public static MediaType parse(String input) {
        // TODO Optimize MediaType to avoid new object allocation with a private
        // MediaType#KNOWN_TYPES-like approach
        return normalize(MediaType.parse(input));
    }

    @Deprecated // Remove and replace (inline) with implementation, for clarity
    public static MediaType normalize(MediaType mediaType) {
        return MediaTypeProviders.SINGLETON.get().normalize(mediaType);
    }

    public static boolean normalizedNoParamsEquals(MediaType actual, MediaType... expecteds) {
        for (var expected : expecteds) {
            if (normalize(actual).withoutParameters().equals(expected.withoutParameters()))
                return true;
        }
        return false;
    }

    public static boolean normalizedNoParamsEquals(MediaType actual, Set<MediaType> expecteds) {
        for (var expected : expecteds) {
            if (normalize(actual).withoutParameters().equals(expected.withoutParameters()))
                return true;
        }
        return false;
    }

    public static Optional<String> parameter(MediaType mediaType, String name) {
        var parameters = mediaType.parameters().get(name);
        if (parameters.isEmpty()) {
            return Optional.empty();
        } else if (parameters.size() == 1) {
            // NB: ofNullable() instead of of() is crucial here!
            return Optional.ofNullable(parameters.get(0));
        } else {
            throw new IllegalArgumentException(
                    "MediaType has multiple '" + name + "' parameters: " + mediaType);
        }
    }

    /**
     * Converts a MediaType to an (Enola.dev-defined) IRI. For example, "text/plain" is converted to
     * "<a
     * href="https://enola.dev/mediaType/text/plain">https://enola.dev/mediaType/text/plain</a>",
     * and "application/dita+xml;format=concept" to "<a
     * href="https://enola.dev/mediaType/application/dita+xml;format=concept">https://enola.dev/mediaType/application/dita/xml?format=concept</a>".
     */
    public static String toIRI(MediaType mediaType) {
        var sb = new StringBuilder("https://enola.dev/mediaType/");
        sb.append(mediaType.type());
        sb.append('/');
        sb.append(mediaType.subtype().replace('+', '/'));
        var parameters = mediaType.parameters();
        if (!parameters.isEmpty()) {
            sb.append('?');
            parameters.forEach(
                    (key, value) -> sb.append(key).append('=').append(value).append('&'));
            return sb.substring(0, sb.length() - 1);
        } else return sb.toString();
    }
}

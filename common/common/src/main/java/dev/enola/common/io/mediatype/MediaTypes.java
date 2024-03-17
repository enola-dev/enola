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

/** Extension methods for {@link MediaType}. */
public final class MediaTypes {

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
        return MediaTypeProviders.SINGLETON.normalize(mediaType);
    }

    public static boolean normalizedNoParamsEquals(MediaType actual, MediaType... expecteds) {
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
}

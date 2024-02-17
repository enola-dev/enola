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

import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Stream;

public final class MediaTypes {

    // TODO Rename this class MediaTypes to MediaTypeNormalizer? And remove #parse() method here?

    private static final MediaTypes INSTANCE = new MediaTypes();

    private final Map<MediaType, MediaType> alternatives;

    private MediaTypes(MediaTypeProvider... providers) {
        this.alternatives = createAlternatives(providers);
    }

    private MediaTypes(Stream<ServiceLoader.Provider<MediaTypeProvider>> providers) {
        this(providers.map(p -> p.get()).toArray(MediaTypeProvider[]::new));
    }

    private MediaTypes() {
        this(ServiceLoader.load(MediaTypeProvider.class).stream());
    }

    /**
     * Improved version of {@link MediaType#parse(String)} which also invokes {@link
     * #normalize(MediaType)}.
     */
    public static MediaType parse(String input) {
        // TODO Optimize MediaType to avoid new object allocation with a private
        // MediaType#KNOWN_TYPES-like approach
        return normalize(MediaType.parse(input));
    }

    public static MediaType normalize(MediaType mediaType) {
        return INSTANCE.alternatives
                .getOrDefault(mediaType.withoutParameters(), mediaType)
                .withParameters(mediaType.parameters());
    }

    public static boolean normalizedNoParamsEquals(MediaType actual, MediaType... expecteds) {
        for (var expected : expecteds) {
            if (normalize(actual).withoutParameters().equals(expected.withoutParameters()))
                return true;
        }
        return false;
    }

    private Map<MediaType, MediaType> createAlternatives(MediaTypeProvider[] providers) {
        var map = ImmutableMap.<MediaType, MediaType>builder();
        Arrays.stream(providers)
                .forEach(
                        provider ->
                                provider.knownTypesWithAlternatives()
                                        .forEach(
                                                (mediaType, mediaTypes) ->
                                                        mediaTypes.forEach(
                                                                alternativeMediaType ->
                                                                        map.put(
                                                                                alternativeMediaType
                                                                                        .withoutParameters(),
                                                                                mediaType
                                                                                        .withoutParameters()))));
        return map.build();
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

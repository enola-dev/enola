/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Aggregates other {@link MediaTypeProvider}s, either from an explicit list provided to
 * constructor, or found via service discovery. Typically used via {@link #SINGLETON}.
 */
public class MediaTypeProviders implements MediaTypeProvider {

    public static final MediaTypeProviders SINGLETON =
            new MediaTypeProviders(ServiceLoader.load(MediaTypeProvider.class).stream());

    private final Map<MediaType, MediaType> alternatives;
    private final Map<MediaType, Set<MediaType>> knownTypesWithAlternatives;
    private final Map<String, MediaType> extensionsToTypes;

    private MediaTypeProviders(Stream<ServiceLoader.Provider<MediaTypeProvider>> providers) {
        this(providers.map(p -> p.get()).toArray(MediaTypeProvider[]::new));
    }

    public MediaTypeProviders(MediaTypeProvider... providers) {
        this.alternatives = createAlternatives(providers);
        this.knownTypesWithAlternatives = collectAlternatives(providers);
        this.extensionsToTypes = collectExtensions(providers);
    }

    @Override
    public Map<MediaType, Set<MediaType>> knownTypesWithAlternatives() {
        return knownTypesWithAlternatives;
    }

    @Override
    public Map<String, MediaType> extensionsToTypes() {
        return extensionsToTypes;
    }

    /**
     * Attempts to "normalize" using {@link MediaTypeProvider#knownTypesWithAlternatives()}.
     *
     * @param mediaType the one to try to replace
     * @return a "canoncial" one, if found; else the argument
     */
    public MediaType normalize(MediaType mediaType) {
        return alternatives
                .getOrDefault(mediaType.withoutParameters(), mediaType)
                .withParameters(mediaType.parameters());
    }

    private Map<String, MediaType> collectExtensions(MediaTypeProvider[] providers) {
        int n = 0;
        for (var provider : providers) {
            n += provider.extensionsToTypes().size();
        }
        var extensions = new HashSet<String>(n);
        var map = ImmutableMap.<String, MediaType>builderWithExpectedSize(n);
        for (var provider : providers) {
            for (var entry : provider.extensionsToTypes().entrySet()) {
                var extension = entry.getKey();
                if (extensions.contains(extension))
                    throw new IllegalArgumentException("Duplicate Extension: " + extension);
                map.put(entry);
                extensions.add(extension);
            }
        }
        return map.build();
    }

    private Map<MediaType, Set<MediaType>> collectAlternatives(MediaTypeProvider[] providers) {
        int n = 0;
        for (var provider : providers) {
            n += provider.extensionsToTypes().size();
        }
        var map = ImmutableMap.<MediaType, Set<MediaType>>builderWithExpectedSize(n);
        for (var provider : providers) {
            // This would not work (overwrite) if different MediaTypeProviders were to return the
            // same primary canonical Media Type, but this shouldn't be a problem in practice.
            // TODO Improve this #later #lowPriority.
            map.putAll(provider.knownTypesWithAlternatives());
        }
        return map.build();
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
}

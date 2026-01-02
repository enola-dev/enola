/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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

import com.google.common.collect.*;
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import dev.enola.common.context.Singleton;

import java.util.*;

/**
 * Aggregates other {@link MediaTypeProvider}s, from an explicit list provided to constructor. (This
 * intentionally does NOT use {@link ServiceLoader}; reasons include requiring a specific priority
 * order.) Typically used via {@link #SINGLETON}.
 */
public class MediaTypeProviders implements MediaTypeProvider {

    public static final Singleton<MediaTypeProviders> SINGLETON = new Singleton<>() {};

    public static Singleton<MediaTypeProviders> set(MediaTypeProvider... providers) {
        return SINGLETON.set(new MediaTypeProviders(providers));
    }

    private final Map<MediaType, MediaType> alternatives;
    private final Map<MediaType, Set<MediaType>> knownTypesWithAlternatives;
    private final Multimap<String, MediaType> extensionsToTypes;
    private final Iterable<MediaTypeProvider> providers;

    public MediaTypeProviders(MediaTypeProvider... providers) {
        this.alternatives = createAlternatives(providers);
        this.knownTypesWithAlternatives = collectAlternatives(providers);
        this.extensionsToTypes = collectExtensions(providers);
        this.providers = ImmutableList.copyOf(providers);
    }

    @Override
    public Map<MediaType, Set<MediaType>> knownTypesWithAlternatives() {
        return knownTypesWithAlternatives;
    }

    @Override
    public Multimap<String, MediaType> extensionsToTypes() {
        return extensionsToTypes;
    }

    @Override
    public MediaType detect(String uri, ByteSource byteSource, MediaType original) {
        for (MediaTypeProvider provider : providers) {
            MediaType detected = provider.detect(uri, byteSource, original);
            if (!detected.equals(original)) return detected;
        }
        return original;
    }

    /**
     * Attempts to "normalize" using {@link MediaTypeProvider#knownTypesWithAlternatives()}.
     *
     * @param mediaType the one to try to replace
     * @return a "canonical" one, if found; else the argument
     */
    public MediaType normalize(MediaType mediaType) {
        return alternatives
                .getOrDefault(mediaType.withoutParameters(), mediaType)
                .withParameters(mediaType.parameters());
    }

    private Multimap<String, MediaType> collectExtensions(MediaTypeProvider[] providers) {
        int n = 0;
        for (var provider : providers) {
            n += provider.extensionsToTypes().size();
        }
        Multimap<String, MediaType> map = MultimapBuilder.treeKeys().arrayListValues(1).build();
        for (var provider : providers) {
            var providerMultimap = provider.extensionsToTypes();
            map.putAll(providerMultimap);
        }
        return ImmutableMultimap.copyOf(map);
    }

    private Map<MediaType, Set<MediaType>> collectAlternatives(MediaTypeProvider[] providers) {
        int n = 0;
        for (var provider : providers) {
            n += provider.extensionsToTypes().size();
        }
        var map = ImmutableMap.<MediaType, Set<MediaType>>builderWithExpectedSize(n);
        for (var provider : providers) {
            // If different MediaTypeProviders were to return the same primary canonical Media Type,
            // the ImmutableMap.Builder#build() would throw an IllegalArgumentException, as
            // duplicate keys would have been added.
            var providerMultimap = provider.knownTypesWithAlternatives();
            map.putAll(providerMultimap);
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

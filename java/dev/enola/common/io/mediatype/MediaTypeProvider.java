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

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.net.MediaType;

import dev.enola.common.io.resource.AbstractResource;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface MediaTypeProvider extends ResourceMediaTypeDetector {

    // TODO Make MediaTypeProvider more consistent (use Multimap for both; instead of [historical]
    // difference)

    // TODO An implementation based on enola.dev/mediaType Type YAML/binary!

    /** Maps "canoncial" (primary) Media Types to a set of its "also known as alternatives". */
    default Map<MediaType, Set<MediaType>> knownTypesWithAlternatives() {
        return Collections.emptyMap();
    }

    /** Maps URI path extensions (without dot) to its "canonical" Media Types. */
    Multimap<String, MediaType> extensionsToTypes();

    /**
     * {@link ResourceMediaTypeDetector#detect(AbstractResource)} implementation using {@link
     * #extensionsToTypes()} and {@link #knownTypesWithAlternatives()}.
     */
    // TODO Abandon this, again? Users which need #knownTypesWithAlternatives() should just use
    // MediaTypes' normalize, typically via normalizedNoParamsEquals()? For users which need
    // #extensionsToTypes()... well, that one is tricky - who's "right", the Resource mediaType,
    // or the extension of the Resource's URI?!
    @Override
    default Optional<MediaType> detect(AbstractResource resource) {
        var e2mt = extensionsToTypes();
        var mediaTypes = e2mt.values();
        var resourceMediaType = resource.mediaType();

        if (mediaTypes.contains(resourceMediaType)) return Optional.of(resourceMediaType);

        // TODO It's kinda wrong that this uses MediaTypeProviders.SINGLETON; it would be clearer if
        // it only ever used itself. But requires moving normalize() from MediaTypeProviders to...
        // where? Another ABC?! Urgh.
        var normalized = MediaTypeProviders.SINGLETON.normalize(resourceMediaType);
        if (!normalized.equals(resourceMediaType)) return Optional.of(normalized);

        // NB: This looks inefficient, and you could be tempted do this "the other way around"
        // (instead of checking EACH map entry with uri.endsWith(), the URI extension should be
        // looked up in theMap). However, this is not possible because we want to support
        // "extensions" with several dots, such as *.schema.yaml etc. which makes it difficult to
        // "extract the extension" from a path.
        var uri = resource.uri().toString();
        for (var extensionEntry : e2mt.asMap().entrySet()) {
            var extension = extensionEntry.getKey();
            // System.out.println(uri + " ? " + extension);
            if (uri.endsWith(extension)) {
                var mediaTypesForExtensions = extensionEntry.getValue();
                if (Iterables.size(mediaTypesForExtensions) > 1)
                    throw new IllegalStateException(
                            extension + " has more than 1 MediaType: " + mediaTypesForExtensions);
                return Optional.of(mediaTypesForExtensions.iterator().next());
            }
        }

        // TODO if (resource instanceof ReadableResource) ... snif it, like in MediaTypeDetector

        return Optional.empty();
    }
}

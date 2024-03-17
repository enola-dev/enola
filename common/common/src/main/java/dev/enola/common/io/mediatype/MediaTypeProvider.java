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

import dev.enola.common.io.resource.AbstractResource;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface MediaTypeProvider extends ResourceMediaTypeDetector {

    // TODO An implementation based on enola.dev/mediaType Type YAML/binary!

    /** Maps "canoncial" (primary) Media Types to a set of its "also known as alternatives". */
    Map<MediaType, Set<MediaType>> knownTypesWithAlternatives();

    /** Maps URI path extensions to a "canoncial" Media Types */
    Map<String, MediaType> extensionsToTypes();

    /**
     * {@link ResourceMediaTypeDetector#detect(AbstractResource)} implementation using {@link
     * #extensionsToTypes()} and {@link #knownTypesWithAlternatives()}.
     */
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

        // TODO This is (very) inefficient, and should be done the "other way around"... Instead of
        // checking EACH map entry with uri.endsWith(), the URI extension should be looked up in the
        // Map!
        var uri = resource.uri().toString();
        for (var extensionEntry : e2mt.entrySet()) {
            if (uri.endsWith(extensionEntry.getKey()))
                return Optional.of(extensionEntry.getValue());
        }

        // TODO if (resource instanceof ReadableResource) ... snif it, like in MediaTypeDetector

        return Optional.empty();
    }
}

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
package dev.enola.common.io.mediatype;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import dev.enola.common.io.iri.URIs;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public interface MediaTypeProvider extends ResourceMediaTypeDetector {

    // TODO Remove extends ResourceMediaTypeDetector - these are separate concepts.

    // TODO Make MediaTypeProvider more consistent (use Multimap for both; instead of [historical]
    // difference)

    // TODO An implementation based on enola.dev/mediaType Type YAML/binary!

    /** Maps "canonical" (primary) Media Types to a set of its "also known as alternatives". */
    default Map<MediaType, Set<MediaType>> knownTypesWithAlternatives() {
        return Collections.emptyMap();
    }

    /**
     * Maps the URI path "extension" (WITH the dot; or not, filenames such as "README" are also
     * permitted!) to its "canonical" Media Types. This should only be used for "informational"
     * listing kind of output. To actually determine a MediaType, please use {@link #detect(String,
     * ByteSource, MediaType)} instead.
     */
    Multimap<String, MediaType> extensionsToTypes();

    /**
     * {@link ResourceMediaTypeDetector#detect(String, ByteSource, MediaType)} default
     * implementation using {@link #extensionsToTypes()}. This matches the "longest" filename
     * extension (if required).
     */
    // or the extension of the Resource's URI?!
    @Override
    default MediaType detect(String uri, ByteSource byteSource, MediaType original) {
        var e2mt = extensionsToTypes();
        var mediaTypes = e2mt.values();

        if (mediaTypes.contains(original)) return original;

        // TODO It's kinda wrong that this uses MediaTypeProviders.SINGLETON; it would be clearer if
        // it only ever used itself. But that requires moving normalize() from MediaTypeProviders
        // to... where? Another ABC?! Urgh.
        original = MediaTypeProviders.SINGLETON.get().normalize(original);

        // NB: This looks inefficient, and you could be tempted to do this "the other way around"
        // (instead of checking EACH map entry with uri.endsWith(), the URI extension should be
        // looked up in theMap). However, this is not possible because we want to support
        // "extensions" with several dots, such as *.schema.yaml etc. which makes it difficult to
        // "extract the extension" from a path.
        var uriWithoutParametersAndFragment = URIs.dropQueryAndFragment(uri);
        for (var extensionEntry : e2mt.asMap().entrySet()) {
            var extension = extensionEntry.getKey();
            // TODO Remove this again! That would allow supporting files with a fixed name, no ext.
            if (!extension.startsWith(".")) throw new IllegalStateException(extension);
            if (uriWithoutParametersAndFragment.endsWith(extension)) {
                var mediaTypesForExtensions = extensionEntry.getValue();
                if (Iterables.size(mediaTypesForExtensions) > 1)
                    throw new IllegalStateException(
                            extension + " has more than 1 MediaType: " + mediaTypesForExtensions);
                return mediaTypesForExtensions.iterator().next();
            }
        }

        return original;
    }
}

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

    Map<MediaType, Set<MediaType>> knownTypesWithAlternatives();

    Map<String, MediaType> extensionsToTypes();

    @Override
    // TODO Integrate this with MediaTypeDetector
    default Optional<MediaType> detect(AbstractResource resource) {
        var e2mt = extensionsToTypes();
        var mediaTypes = e2mt.values();
        var resourceMediaType = resource.mediaType();
        // TODO This should compare MediaTypes ditching (only) charset parameters
        // TODO Use MediaTypes#normalizedNoParamsEquals with knownTypesWithAlternatives()
        if (mediaTypes.contains(resourceMediaType)) return Optional.of(resourceMediaType);

        var uri = resource.uri().toString();
        for (var extensionEntry : e2mt.entrySet()) {
            if (uri.endsWith(extensionEntry.getKey()))
                return Optional.of(extensionEntry.getValue());
        }

        // TODO if (resource instanceof ReadableResource) ... snif it, like in MediaTypeDetector

        return Optional.empty();
    }
}

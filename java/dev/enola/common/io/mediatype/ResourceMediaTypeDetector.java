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

import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import dev.enola.common.io.resource.AbstractResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.Resource;

import java.util.Optional;

/**
 * API for detectors of a (better) MediaType for a Resource.
 *
 * <p>This interface is typically not used directly by {@link Resource} API users (who would just
 * use {@link AbstractResource#mediaType()}). Instead, it is normally implemented by (some) <code>
 * *MediaType</code> API implementations.
 */
public interface ResourceMediaTypeDetector {

    @Deprecated
    // TODO Remove this old signature!
    default Optional<MediaType> detect(AbstractResource resource) {
        var byteSource =
                resource instanceof ReadableResource readableResource
                        ? readableResource.byteSource()
                        : ByteSource.empty();
        return Optional.of(detect(resource.uri().toString(), byteSource, resource.mediaType()));
    }

    /**
     * Detect the {@link MediaType}. This may look e.g. at the filename extension of the URI. It
     * could also "sniff" the content to detect "magic numbers" of certain binary file formats in
     * headers from byteSource; such implementations internally may use {@link
     * ResourceCharsetDetector} (which may "sniff" e.g. BOM headers).
     */
    MediaType detect(String uri, ByteSource byteSource, MediaType original);
}

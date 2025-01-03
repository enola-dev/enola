/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2025 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.resource;

import static dev.enola.common.io.resource.SPI.missingCharsetExceptionSupplier;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

import java.time.Instant;
import java.util.Optional;

public interface ReadableResource extends AbstractResource {

    ByteSource byteSource();

    // TODO Consider replacing or integrating this with Converter?!
    default CharSource charSource() {
        // Workaround for java.lang.NoSuchMethodError: 'java.util.Optional
        // com.google.common.base.Optional.toJavaUtil()' due to classpath hell :(
        var charset = java.util.Optional.ofNullable(mediaType().charset().orNull());
        return byteSource()
                .asCharSource(charset.orElseThrow(missingCharsetExceptionSupplier(this)));
    }

    // NO contentLength() because ByteSource already has a size() + sizeIfKnown()

    /**
     * Last Modified date time (if known). Implemented e.g. via a File's last modified (not created
     * or accessed) time, or a remote resource's <tt>Last-Modified</tt> HTTP Header. Typically used
     * for cache invalidation to determine if the resource version is the same as a previously read
     * one. Some implementations may well not provide this!
     */
    default Optional<Instant> lastModifiedIfKnown() {
        return Optional.empty();
    }
}

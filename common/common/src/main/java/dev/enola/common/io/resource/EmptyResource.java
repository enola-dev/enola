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
package dev.enola.common.io.resource;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.MediaTypes;

import java.net.URI;

/**
 * Resources which when read is always immediately EOF. Note that this is read-only, and
 * intentionally does not implement WritableResource; use e.g. {@link ResourceProviders#getResource}
 * with "empty:-" to get a wrapped implementation that implements writable but throws an error.
 *
 * @see NullResource for an alternatives that returns 0s instead of EOF.
 */
public class EmptyResource implements ReadableResource {
    // TODO Perhaps rename this to VoidResource with void:/ URI?

    static final String SCHEME = "empty";

    private final MediaType mediaType;

    private final URI uri;

    public EmptyResource(MediaType mediaType) {
        this.mediaType = mediaType;
        this.uri = uri(this.mediaType);
    }

    public EmptyResource(String mediaType) {
        this(MediaTypes.parse(mediaType));
    }

    public static URI uri(MediaType mediaType) {
        return URI.create(SCHEME + ":" + mediaType.withoutParameters());
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public MediaType mediaType() {
        return mediaType;
    }

    @Override
    public ByteSource byteSource() {
        return ByteSource.empty();
    }

    @Override
    public CharSource charSource() {
        return CharSource.empty();
    }

    @Override
    public String toString() {
        return "EmptyResource{uri=" + uri() + ", mediaType=" + mediaType + "}";
    }
}

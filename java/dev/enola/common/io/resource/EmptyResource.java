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
package dev.enola.common.io.resource;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.net.MediaType;

import dev.enola.common.io.iri.URIs;

import java.net.URI;

/**
 * Read-only resources which when read are always immediately EOF (like "data:,"). This is a bit
 * like /dev/null on *NIX OS for reading, but not for writing (because /dev/null ignores writes,
 * whereas this fails).
 *
 * @see NullResource for an alternative that returns infinite 0s instead of EOF.
 */
public class EmptyResource extends BaseResource implements ReadableButNotWritableResource {
    // TODO Perhaps rename this to VoidResource with void:/ URI?

    public static class Provider implements ResourceProvider {

        @Override
        public Resource getResource(URI uri) {
            if (SCHEME.equals(uri.getScheme())) return new EmptyResource(uri);
            else return null;
        }
    }

    static final String SCHEME = "empty";

    // TODO Use similar pattern to EMPTY_TEXT_URI (just without charset)
    public static final URI EMPTY_URI = URI.create(SCHEME + ":?");
    public static final EmptyResource INSTANCE =
            new EmptyResource(EMPTY_URI, MediaType.OCTET_STREAM);

    // Intentionally contains a "filename", so that e.g. URIs.getFilename() "works"
    public static final URI EMPTY_TEXT_URI = URI.create(SCHEME + ":/void?charset=utf-8");

    public EmptyResource(URI uri) {
        super(uri);
    }

    public EmptyResource(MediaType mediaType) {
        super(URIs.addMediaType(EMPTY_URI, mediaType), mediaType);
    }

    public EmptyResource(URI uri, MediaType mediaType) {
        super(uri, mediaType);
    }

    @Override
    public ByteSource byteSource() {
        return ByteSource.empty();
    }

    @Override
    public CharSource charSource() {
        return CharSource.empty();
    }
}

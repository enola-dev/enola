/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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

import java.net.URI;

class EmptyResource implements ReadableResource {
    // TODO Perhaps rename this to VoidResource with void:/ URI?

    static final EmptyResource INSTANCE = new EmptyResource();

    static final String SCHEME = "empty";

    private static final URI EMPTY_URI = URI.create("empty:-");

    private static final MediaType MEDIA_TYPE = MediaType.OCTET_STREAM;

    private EmptyResource() {}

    @Override
    public URI uri() {
        return EMPTY_URI;
    }

    @Override
    public MediaType mediaType() {
        return MEDIA_TYPE;
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
        return "EmptyResource{uri=" + uri() + '}';
    }
}

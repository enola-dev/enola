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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class StringResource implements ReadableResource {

    static final String SCHEME = "string";

    private final String string;
    private final MediaType mediaType;
    private final URI uri;

    public StringResource(String text) {
        this(text, MediaType.PLAIN_TEXT_UTF_8);
    }

    public StringResource(String text, MediaType mediaType) {
        this.string = Objects.requireNonNull(text, "text");
        if ("".equals(text)) {
            throw new IllegalArgumentException(
                    "Empty string: not supported (because that's an invalid URI)");
        }
        this.mediaType = Objects.requireNonNull(mediaType, "mediaType");
        if (!mediaType.charset().isPresent()) {
            throw new IllegalArgumentException(
                    "MediaType is missing required charset: " + mediaType);
        }

        try {
            this.uri = new URI(SCHEME, string, null);
        } catch (URISyntaxException e) {
            // This should never happen, if the escaping above is correct...
            throw new IllegalArgumentException("String is invalid in URI: " + text, e);
        }
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
        return charSource().asByteSource(mediaType().charset().get());
    }

    @Override
    public CharSource charSource() {
        return CharSource.wrap(string);
    }

    @Override
    public String toString() {
        return "StringResource{uri=" + uri() + ", mediaType=" + mediaType() + "}";
    }
}

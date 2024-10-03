/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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

import static java.util.Objects.requireNonNull;

import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.MediaTypeDetector;

import java.net.URI;

public abstract class BaseResource implements AbstractResource {

    // TODO For implementations such as OkHttpResource, MediaType should be "lazily" initialized...

    private static final MediaTypeDetector mtd = new MediaTypeDetector();

    // Always keep the user-specified URI (because e.g. ?query parameters & #fragment may get lost)
    protected final URI uri;
    protected final MediaType mediaType;

    protected BaseResource(URI uri, ByteSource byteSource) {
        this(uri, mtd.detect(uri, byteSource));
    }

    protected BaseResource(URI uri, MediaType mediaType) {
        this.uri = requireNonNull(uri, "uri");
        this.mediaType = mtd.overwrite(uri, requireNonNull(mediaType, "mediaType"));
    }

    protected BaseResource(URI uri) {
        this.uri = requireNonNull(uri, "uri");
        this.mediaType = mtd.detect(uri, ByteSource.empty());
    }

    @Override
    public final URI uri() {
        return uri; // NOT path.toUri();
    }

    @Override
    public final MediaType mediaType() {
        return mediaType;
    }

    @Override
    public String toString() {
        return getClass().getName() + "{uri=" + uri() + " & mediaType=" + mediaType() + '}';
    }
}

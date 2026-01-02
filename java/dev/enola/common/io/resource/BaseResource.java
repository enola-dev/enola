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
package dev.enola.common.io.resource;

import static java.util.Objects.requireNonNull;

import com.google.common.base.Suppliers;
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import java.net.URI;
import java.util.function.Supplier;

public abstract class BaseResource implements AbstractResource {

    private static final MediaTypeDetector mtd = new MediaTypeDetector();

    // Always keep the user-specified URI (because e.g. ?query parameters & #fragment may get lost)
    protected final URI uri;
    protected final Supplier<MediaType> mediaType;

    protected BaseResource(URI uri) {
        this(uri, ByteSource.empty());
    }

    protected BaseResource(URI uri, ByteSource byteSource) {
        this.uri = requireNonNull(uri, "uri");
        this.mediaType = Suppliers.memoize(() -> mtd.detect(uri, byteSource));
    }

    protected BaseResource(URI uri, MediaType mediaType) {
        this.uri = requireNonNull(uri, "uri");
        this.mediaType =
                Suppliers.memoize(
                        () -> mtd.adjustCharset(uri, requireNonNull(mediaType, "mediaType")));
    }

    // No need for: protected BaseResource(URI uri, MediaType mediaType, ByteSource byteSource) {

    protected BaseResource(URI uri, Supplier<MediaType> mediaTypeSupplier) {
        this.uri = requireNonNull(uri, "uri");
        this.mediaType =
                Suppliers.memoize(
                        () ->
                                mtd.adjustCharset(
                                        uri, requireNonNull(mediaTypeSupplier.get(), "mediaType")));
    }

    @Override
    public final URI uri() {
        return uri; // NOT path.toUri();
    }

    @Override
    public final MediaType mediaType() {
        return mediaType.get();
    }

    @Override
    public String toString() {
        return getClass().getName() + "{uri=" + uri() + " & mediaType=" + mediaType() + '}';
    }
}

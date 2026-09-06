/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2026 The Enola <https://enola.dev> Authors
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

import dev.enola.common.io.resource.DataResource;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/** {@link MediaType} detector. */
public final class MediaTypeDetector {

    public static Optional<MediaType> detectOptional(String uri) {
        if (uri.startsWith("data:")) {
            return Optional.of(new DataResource(URI.create(uri)).mediaType());
        }

        Path path = Paths.get(uri);
        return detectOptional(path);
    }

    public static Optional<MediaType> detectOptional(Path path) {
        try {
            String type = Files.probeContentType(path);
            if (type == null) {
                type = MoreFileTypeDetector.detect(path);
            }
            if (type == null) {
                return Optional.empty();
            }
            try {
                return Optional.of(MediaType.parse(type));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public static Optional<MediaType> detectOptional(URI uri) {
        return detectOptional(uri.toString());
    }

    /**
     * Detect the {@link MediaType}, based on the filename extension of the URI.
     *
     * <p>Based on {@link Files#probeContentType(Path)}, but:
     *
     * <ul>
     *   <li>accepts String (URI), instead of Path
     *   <li>returns MediaType, instead of String
     *   <li>throws only IllegalArgumentException and never IOException
     *   <li>never returns null
     * </ul>
     *
     * @throws IllegalArgumentException if the MediaType cannot be reliably determined. This is
     *     typically preferable over returning something like "application/octet-stream" (or
     *     "application/binary", or some other overly generic MediaType).
     */
    public static MediaType detect(String uri) throws IllegalArgumentException {
        return detectOptional(uri)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "Cannot determine MediaType for URI: " + uri));
    }

    public static MediaType detect(Path path) throws IllegalArgumentException {
        return detectOptional(path)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "Cannot determine MediaType for Path: " + path));
    }

    public static MediaType detect(URI uri) {
        return detect(uri.toString());
    }

    /* *
     * Detect the {@link MediaType}. This may look e.g. at the filename extension of the URI. It MAY
     * (or not...) also "sniff" the content to detect "magic numbers" of certain binary file formats
     * in headers from byteSource.
     */
    // TODO MediaType detect(String uri, ByteSource byteSource);

    /* *
     * Detect the {@link MediaType}. This may look e.g. at the filename extension of the URI. It MAY
     * (or not...) also "sniff" the content to detect "magic numbers" of certain binary file formats
     * in headers from byteSource.
     *
     * <p>The original MediaType is provided as a hint, but may be incorrect.
     */
    // TODO MediaType detect(String uri, ByteSource byteSource, MediaType original);
}

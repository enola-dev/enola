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

import com.google.common.collect.ImmutableSet;
import com.google.common.net.MediaType;

import java.net.URI;

/**
 * MultipartResources are "logical" resources which do have an URI, but are composed of multiple
 * "parts" which are independent (sub)resources, each with their own {@link MediaType} and content.
 *
 * <p>Examples could be Emails with attachments (RFC 2045 &amp; 2046), or files on operating systems
 * with file systems where a single file can contain multiple alternative data streams (e.g. NTFS,
 * and old classic Mac OS's HFS with data &amp; resources forks), or things like the frontmatter
 * resources.
 *
 * <p>The URI of the sub-resources should correspond to the "parent" MultipartResource's URI
 * appended by the part name as a #fragment.
 *
 * <p>This interface intentionally does not extend {@link Resource}, because not all implementations
 * will have a "root" resource (although some may).
 */
public interface MultipartResource {

    MediaType MEDIA_TYPE = MediaType.create("multipart", "related");

    /**
     * MediaType defaults to {@link #MEDIA_TYPE}. Implementations may or may not return media types
     * with charset parameters, as that's also specified on parts, and the encoding may or may not
     * be the same for each of them.
     */
    default MediaType mediaType() {
        return MEDIA_TYPE;
    }

    URI uri();

    ImmutableSet<String> parts();

    Resource part(String name);
}

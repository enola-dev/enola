/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.object;

import dev.enola.common.io.resource.ReadableResource;

import java.io.IOException;
import java.util.Optional;

public interface ObjectReader {

    /** Read an object of Class T from the resource, if present. */
    <T> Optional<T> optional(ReadableResource resource, Class<T> type) throws IOException;

    /** Read e.g. a JSON (or YAML) [ ... ] array. */
    <T> Iterable<T> readArray(ReadableResource resource, Class<T> type) throws IOException;

    /**
     * Read e.g. a <a href="https://yaml.org/spec/1.2.2/#chapter-9-document-stream-productions">YAML
     * stream</a>. (For formats such as plain JSON which do not directly support a streams syntax,
     * this will always return a single element.)
     */
    <T> Iterable<T> readStream(ReadableResource resource, Class<T> type) throws IOException;

    // TODO This should check and fail if there is "more than one" because its a stream, and fail
    default <T> T read(ReadableResource resource, Class<T> type) throws IOException {
        return optional(resource, type)
                .orElseThrow(
                        () ->
                                new IOException(
                                        getClass().getSimpleName()
                                                + " cannot read "
                                                + resource.uri()
                                                + " as "
                                                + type.getTypeName()));
    }

    // PS: There is intentionally no ResolvedType / TypeReference / TypeToken sorta support!
}

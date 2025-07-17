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

    <T> Iterable<T> readAll(ReadableResource resource, Class<T> type) throws IOException;

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

    <T> Optional<T> optional(ReadableResource resource, Class<T> type) throws IOException;

    // PS: There is intentionally no ResolvedType / TypeReference / TypeToken sorta support!
}

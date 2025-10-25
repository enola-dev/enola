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
package dev.enola.common.io.object.csv;

import dev.enola.common.io.object.ObjectReader;
import dev.enola.common.io.resource.ReadableResource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CsvReader implements ObjectReader {

    @Override
    public <T> Optional<T> optional(ReadableResource resource, Class<T> type) throws IOException {
        throw new UnsupportedOperationException("Use readStream or readArray for CSV reading");
    }

    @Override
    public <T> Iterable<T> readArray(ReadableResource resource, Class<T> type) throws IOException {
        return readStream(resource, type);
    }

    @Override
    public <T> Iterable<T> readStream(ReadableResource resource, Class<T> type) throws IOException {
        if (!type.isAssignableFrom(Map.class))
            throw new IllegalArgumentException("CsvReader currently only supports Map types");

        return List.<T>of();
    }
}

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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import dev.enola.common.io.resource.ReadableResource;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ObjectReaderChain implements ObjectReader {

    private final Iterable<ObjectReader> readers;

    public ObjectReaderChain(Iterable<ObjectReader> readers) {
        this.readers = ImmutableList.copyOf(readers);
    }

    @Override
    public <T> Optional<T> optional(ReadableResource resource, Class<T> type) throws IOException {
        for (var reader : readers) {
            Optional<T> optional = reader.optional(resource, type);
            if (optional.isPresent()) {
                return optional;
            }
        }
        return Optional.empty();
    }

    @Override
    public <T> Iterable<T> readAll(ReadableResource resource, Class<T> type) throws IOException {
        for (var reader : readers) {
            Iterable<T> iterable = reader.readAll(resource, type);
            if (!Iterables.isEmpty(iterable)) {
                return iterable;
            }
        }
        return List.of();
    }
}

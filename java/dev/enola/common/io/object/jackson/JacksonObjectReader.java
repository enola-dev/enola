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
package dev.enola.common.io.object.jackson;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.MediaType;

import dev.enola.common.io.object.ObjectReader;
import dev.enola.common.io.resource.ReadableResource;

import java.io.IOException;
import java.util.Optional;

abstract class JacksonObjectReader implements ObjectReader {

    private final ObjectMapper mapper;

    protected JacksonObjectReader(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    abstract boolean canHandle(MediaType mediaType);

    @Override
    public <T> Optional<T> optional(ReadableResource resource, Class<T> type) throws IOException {
        if (!canHandle(resource.mediaType())) return Optional.empty();
        try (var reader = resource.charSource().openBufferedStream()) {
            return Optional.of(mapper.readValue(reader, type));
        } catch (DatabindException e) {
            throw new IOException(e);
        }
    }
}

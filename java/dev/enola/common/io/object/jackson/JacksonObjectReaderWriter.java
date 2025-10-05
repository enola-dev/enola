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

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.net.MediaType;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import dev.enola.common.io.object.Identifiable;
import dev.enola.common.io.object.ObjectReaderWriter;
import dev.enola.common.io.object.ProviderFromID;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.jackson.ObjectMappers;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

abstract class JacksonObjectReaderWriter implements ObjectReaderWriter {

    // TODO Upgrade from Jackson 2.x to 3.0

    private final ObjectMapper mapper;
    private final @Nullable ProviderFromID provider;

    protected JacksonObjectReaderWriter(ObjectMapper mapper) {
        this.mapper = mapper;
        this.provider = null;
        init();
    }

    protected JacksonObjectReaderWriter(ObjectMapper mapper, ProviderFromID provider) {
        this.mapper = mapper;
        this.provider = provider;
        init();
    }

    private void init() {
        var module = new SimpleModule();
        module.addSerializer(Identifiable.class, new IdentifiableIdSerializer());
        if (provider != null) module.setDeserializers(new IdentifiableDeserializers(provider));
        mapper.registerModule(module);

        ObjectMappers.configure(mapper);
    }

    abstract boolean canHandle(MediaType mediaType);

    abstract String empty();

    @Override
    public <T> Optional<T> optional(ReadableResource resource, Class<T> type) throws IOException {
        if (!canHandle(resource.mediaType())) return Optional.empty();
        return read(
                resource,
                reader -> Optional.of(mapper.readValue(reader, type)),
                () -> Optional.of(mapper.readValue(empty(), type)));
    }

    @Override
    public <T> Iterable<T> readArray(ReadableResource resource, Class<T> type) throws IOException {
        if (!canHandle(resource.mediaType())) return List.of();
        return read(
                resource,
                reader -> {
                    var javaType =
                            mapper.getTypeFactory().constructCollectionType(List.class, type);
                    return mapper.readValue(reader, javaType);
                },
                () -> List.of());
    }

    @Override
    public <T> Iterable<T> readStream(ReadableResource resource, Class<T> type) throws IOException {
        if (!canHandle(resource.mediaType())) return List.of();
        return read(
                resource,
                reader -> {
                    var parser = mapper.getFactory().createParser(reader);
                    try (MappingIterator<T> mappingIterator = mapper.readValues(parser, type)) {
                        return mappingIterator.readAll();
                    }
                },
                () -> List.of());
    }

    private <R> R read(
            ReadableResource resource,
            IOThrowingFunction<java.io.Reader, R> function,
            IOThrowingSupplier<R> emptyValueSupplier)
            throws IOException {
        try (var reader = resource.charSource().openBufferedStream()) {
            if (isEmpty(reader)) return emptyValueSupplier.get();
            return function.apply(reader);
        } catch (IOException e) {
            e.addSuppressed(new IOException(resource.uri().toString()));
            throw e;
        }
    }

    private boolean isEmpty(java.io.Reader reader) throws IOException {
        if (!reader.ready()) return true;
        if (reader.markSupported()) {
            reader.mark(1);
            if (reader.read() == -1) {
                return true;
            } else {
                reader.reset();
            }
        }
        return false;
    }

    @FunctionalInterface
    private interface IOThrowingFunction<T, R> {
        R apply(T t) throws IOException;
    }

    @FunctionalInterface
    private interface IOThrowingSupplier<T> {
        T get() throws IOException;
    }

    @Override
    @CanIgnoreReturnValue
    public boolean write(Object instance, WritableResource resource) throws IOException {
        if (!canHandle(resource.mediaType())) return false;
        try (var writer = resource.charSink().openBufferedStream()) {
            mapper.writeValue(writer, instance);
            return true;
        } catch (IOException e) {
            e.addSuppressed(new IOException(resource.uri().toString()));
            throw e;
        }
    }
}

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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.net.MediaType;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import dev.enola.common.io.object.ObjectReaderWriter;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

abstract class JacksonObjectReaderWriter implements ObjectReaderWriter {

    // TODO Upgrade from Jackson 2.x to 3.0

    private final ObjectMapper mapper;

    protected JacksonObjectReaderWriter(ObjectMapper mapper) {
        this.mapper = mapper;

        // Do NOT use mapper.findAndRegisterModules();
        // because that would mean that mapping would depend on (un-controllable) classpath

        // https://github.com/FasterXML/jackson-modules-java8
        mapper.registerModule(new JavaTimeModule());
        // TODO Optional<T> support with mapper.registerModule(new Jdk8Module());

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Always skip empty sequences ([]) and maps
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        // Always set missing sequences to empty collections
        var nullAsEmpty = JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY);
        mapper.configOverride(List.class).setSetterInfo(nullAsEmpty);
        mapper.configOverride(Set.class).setSetterInfo(nullAsEmpty);
        mapper.configOverride(Map.class).setSetterInfo(nullAsEmpty);
    }

    abstract boolean canHandle(MediaType mediaType);

    abstract String empty();

    @Override
    public <T> Optional<T> optional(ReadableResource resource, Class<T> type) throws IOException {
        if (!canHandle(resource.mediaType())) return Optional.empty();
        try (var reader = resource.charSource().openBufferedStream()) {
            if (!reader.ready()) return Optional.of(mapper.readValue(empty(), type));
            if (reader.markSupported()) {
                reader.mark(1);
                if (reader.read() == -1) return Optional.of(mapper.readValue(empty(), type));
                else reader.reset();
            }
            return Optional.of(mapper.readValue(reader, type));
        } catch (DatabindException e) {
            throw new IOException(e);
        }
    }

    @Override
    public <T> Iterable<T> readArray(ReadableResource resource, Class<T> type) throws IOException {
        if (!canHandle(resource.mediaType())) return List.of();
        try (var reader = resource.charSource().openBufferedStream()) {
            var javaType = mapper.getTypeFactory().constructCollectionType(List.class, type);
            return mapper.readValue(reader, javaType);
        } catch (DatabindException e) {
            throw new IOException(e);
        }
    }

    @CanIgnoreReturnValue
    public boolean write(Object instance, WritableResource resource) throws IOException {
        if (!canHandle(resource.mediaType())) return false;
        try (var writer = resource.charSink().openBufferedStream()) {
            mapper.writeValue(writer, instance);
            return true;
        } catch (DatabindException e) {
            throw new IOException(e);
        }
    }
}

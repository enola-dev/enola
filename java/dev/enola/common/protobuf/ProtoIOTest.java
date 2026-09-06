/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.protobuf;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.net.MediaType;
import com.google.protobuf.Any;
import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import com.google.protobuf.TypeRegistry;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.EmptyResource;
import dev.enola.common.io.resource.MemoryResource;
import dev.enola.common.io.resource.ReadableResource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;

class ProtoIOTest {

    @RegisterExtension SingletonRule r = $(MediaTypeProviders.set(new ProtobufMediaTypes()));

    public static final Timestamp TIMESTAMP =
            Timestamp.newBuilder().setSeconds(123).setNanos(456).build();

    private final String pathToResourceOnClasspath = "ok.textproto";
    private final Message.Builder builder = Timestamp.newBuilder();

    @Test
    void testProto() throws IOException {
        ProtoIO.check(pathToResourceOnClasspath, builder);
    }

    private Timestamp readTimestamp(ReadableResource resource) throws IOException {
        return new ProtoIO().read(resource, Timestamp.newBuilder(), Timestamp.class);
    }

    @Test
    void readGoodTextproto() throws IOException {
        assertThat(readTimestamp(new ClasspathResource("ok.textproto"))).isEqualTo(TIMESTAMP);
    }

    @Test
    void readEmptyBinary() throws IOException {
        var r = new EmptyResource(ProtobufMediaTypes.PROTOBUF_BINARY);
        assertThat(readTimestamp(r)).isEqualTo(Timestamp.getDefaultInstance());
    }

    @Test
    void readEmptyTextproto() throws IOException {
        var r = new EmptyResource(ProtobufMediaTypes.PROTOBUF_TEXTPROTO_UTF_8);
        assertThat(readTimestamp(r)).isEqualTo(Timestamp.getDefaultInstance());
    }

    @Test
    void readEmptyJSON() throws IOException {
        var r = new EmptyResource(ProtobufMediaTypes.PROTOBUF_JSON_UTF_8);
        assertThat(readTimestamp(r)).isEqualTo(Timestamp.getDefaultInstance());
    }

    @Test
    void readEmptyYAML() throws IOException {
        var r = new EmptyResource(ProtobufMediaTypes.PROTOBUF_YAML_UTF_8);
        assertThat(readTimestamp(r)).isEqualTo(Timestamp.getDefaultInstance());
    }

    @Test
    void writeReadTextProto() throws IOException {
        MemoryResource resource = new MemoryResource(ProtobufMediaTypes.PROTOBUF_TEXTPROTO_UTF_8);
        new ProtoIO().write(TIMESTAMP, resource);
        assertThat(resource.charSource().read()).isEqualTo("seconds: 123\nnanos: 456\n");

        assertThat(new ProtoIO().read(resource, Timestamp.newBuilder(), Timestamp.class))
                .isEqualTo(TIMESTAMP);
    }

    @Test
    void writeReadJSON() throws IOException {
        MemoryResource resource = new MemoryResource(ProtobufMediaTypes.PROTOBUF_JSON_UTF_8);
        new ProtoIO().write(TIMESTAMP, resource);
        assertThat(resource.charSource().read()).isEqualTo("\"1970-01-01T00:02:03.000000456Z\"");

        assertThat(new ProtoIO().read(resource, Timestamp.newBuilder(), Timestamp.class))
                .isEqualTo(TIMESTAMP);
    }

    // TODO Add support for ProtobufMediaTypes.PROTOBUF_YAML_UTF_8

    @Test
    void writeReadBinary() throws IOException {
        MemoryResource resource = new MemoryResource(ProtobufMediaTypes.PROTOBUF_BINARY);
        new ProtoIO().write(TIMESTAMP, resource);
        assertThat(resource.byteSource().size()).isEqualTo(5);

        assertThat(new ProtoIO().read(resource, Timestamp.newBuilder(), Timestamp.class))
                .isEqualTo(TIMESTAMP);
    }

    @Test
    void writeUnknown() throws IOException {
        MemoryResource resource = new MemoryResource(MediaType.ANY_TYPE);
        assertThrows(IllegalStateException.class, () -> new ProtoIO().write(TIMESTAMP, resource));
    }

    @Test
    void readBadTextproto() throws IOException {
        assertThat(
                        assertThrows(
                                ProtoIO.TextParseException.class,
                                () ->
                                        new ProtoIO()
                                                .read(
                                                        new ClasspathResource("nok.textproto"),
                                                        Timestamp.newBuilder())))
                .hasMessageThat()
                .contains("google.protobuf.Timestamp.bad");

        // new ProtoIO().merge(classpath("nok.textproto"), Timestamp.newBuilder());
    }

    @Test
    void writeReadAnyTextproto() throws IOException {
        var typeRegistry = TypeRegistry.newBuilder().add(Timestamp.getDescriptor()).build();
        var io = new ProtoIO(typeRegistry);
        var any = Any.pack(TIMESTAMP);
        var textproto =
                """
                [type.googleapis.com/google.protobuf.Timestamp] {
                  seconds: 123
                  nanos: 456
                }
                """;

        var resource = new MemoryResource(ProtobufMediaTypes.PROTOBUF_TEXTPROTO_UTF_8);
        io.write(any, resource);
        assertThat(resource.charSource().read()).isEqualTo(textproto);

        assertThat(io.read(resource, Any.newBuilder(), Any.class)).isEqualTo(any);
    }

    @Test
    void writeReadAnyJSON() throws IOException {
        var typeRegistry = TypeRegistry.newBuilder().add(Timestamp.getDescriptor()).build();
        var io = new ProtoIO(typeRegistry);
        var any = Any.pack(TIMESTAMP);
        var json =
                "{\"@type\":\"type.googleapis.com/google.protobuf.Timestamp\","
                        + "\"value\":\"1970-01-01T00:02:03.000000456Z\"}";

        var resource = new MemoryResource(ProtobufMediaTypes.PROTOBUF_JSON_UTF_8);
        io.write(any, resource);
        assertThat(resource.charSource().read()).isEqualTo(json);

        assertThat(io.read(resource, Any.newBuilder(), Any.class)).isEqualTo(any);
    }

    @Test
    void writeAnyYAML() throws IOException {
        var typeRegistry = TypeRegistry.newBuilder().add(Timestamp.getDescriptor()).build();
        var io = new ProtoIO(typeRegistry);
        var any = Any.pack(TIMESTAMP);
        var yaml =
                "{'@type': type.googleapis.com/google.protobuf.Timestamp, value:"
                        + " '1970-01-01T00:02:03.000000456Z'}\n";

        var resource = new MemoryResource(ProtobufMediaTypes.PROTOBUF_YAML_UTF_8);
        io.write(any, resource);
        assertThat(resource.charSource().read()).isEqualTo(yaml);

        assertThat(io.read(resource, Any.newBuilder(), Any.class)).isEqualTo(any);
    }
}

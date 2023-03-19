/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.google.protobuf.TextFormat;
import com.google.protobuf.TypeRegistry;
import com.google.protobuf.util.JsonFormat;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;

import java.io.*;
import java.net.URI;

public class ProtoIO {

    // TODO scan for proto-file and proto-message headers to support DynamicMessage
    // TODO support proto-import?

    private final ExtensionRegistry extensionRegistry = ExtensionRegistry.getEmptyRegistry();
    private final TypeRegistry typeRegistry = TypeRegistry.newBuilder().build();

    public void write(Message message, WritableResource resource) throws IOException {
        if (ProtobufMediaTypes.PROTOBUF_BINARY.equals(resource.mediaType())) {
            try (OutputStream os = resource.byteSink().openBufferedStream()) {
                message.writeTo(os);
            }
        } else {
            try (Writer writer = resource.charSink().openBufferedStream()) {
                if (resource.mediaType()
                        .is(ProtobufMediaTypes.PROTOBUF_TEXTPROTO_UTF_8.withoutParameters())) {
                    TextFormat.printer()
                            .escapingNonAscii(false)
                            .usingTypeRegistry(typeRegistry)
                            .print(message, writer);
                } else if (resource.mediaType()
                        .is(ProtobufMediaTypes.PROTOBUF_JSON_UTF_8.withoutParameters())) {
                    JsonFormat.printer()
                            .usingTypeRegistry(typeRegistry)
                            .omittingInsignificantWhitespace()
                            .appendTo(message, writer);
                } else {
                    throw new IllegalArgumentException(
                            "TODO Implement for missing mediaType: " + resource);
                }
            }
        }
    }

    public <B extends Builder> B read(ReadableResource resource, B builder) throws IOException {
        if (ProtobufMediaTypes.PROTOBUF_BINARY.equals(resource.mediaType())) {
            try (InputStream is = resource.byteSource().openBufferedStream()) {
                builder.mergeFrom(is, extensionRegistry);
            }
        } else {
            try (Reader reader = resource.charSource().openBufferedStream()) {
                if (resource.mediaType()
                        .is(ProtobufMediaTypes.PROTOBUF_TEXTPROTO_UTF_8.withoutParameters())) {
                    TextFormat.getParser().merge(reader, extensionRegistry, builder);
                } else if (resource.mediaType()
                        .is(ProtobufMediaTypes.PROTOBUF_JSON_UTF_8.withoutParameters())) {
                    JsonFormat.parser().usingTypeRegistry(typeRegistry).merge(reader, builder);
                } else {
                    throw new IllegalArgumentException(
                            "TODO Implement for missing mediaType: " + resource);
                }
            } catch (TextFormat.ParseException e) {
                throw new TextParseException(resource.uri(), e);
            }
        }
        return builder;
    }

    public <M extends Message> M read(
            ReadableResource resource, Builder builder, Class<M> messageClass) throws IOException {
        return (M) read(resource, builder).build();
    }

    // Shortcut intended for unit tests
    public static void check(String pathToResourceOnClasspath, Message.Builder builder)
            throws IOException {
        ReadableResource resource = new ClasspathResource(pathToResourceOnClasspath);
        // TODO new ProtoIO().merge(resource, DynamicMessage.newBuilder(descriptor));
        new ProtoIO().read(resource, builder);
    }

    public static class TextParseException extends TextFormat.ParseException {

        private final URI uri;

        public TextParseException(URI uri, TextFormat.ParseException e) {
            super(e.getLine(), e.getColumn(), uri.toString() + ":" + e.getMessage());
            this.uri = uri;
        }
    }
}

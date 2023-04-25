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

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.net.MediaType.JSON_UTF_8;

import static dev.enola.common.io.mediatype.MediaTypes.normalizedNoParamsEquals;
import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;
import static dev.enola.common.protobuf.ProtobufMediaTypes.*;

import com.google.common.net.MediaType;
import com.google.protobuf.*;
import com.google.protobuf.Message.Builder;
import com.google.protobuf.util.JsonFormat;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.yamljson.YamlJson;

import java.io.*;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

public class ProtoIO {

    // TODO scan for proto-file and proto-message headers to support DynamicMessage
    // TODO support proto-import?

    private final ExtensionRegistry extensionRegistry = ExtensionRegistry.getEmptyRegistry();
    private final TypeRegistry typeRegistry = TypeRegistry.newBuilder().build();

    // Shortcut intended for unit tests
    public static void check(String pathToResourceOnClasspath, Message.Builder builder)
            throws IOException {
        ReadableResource resource = new ClasspathResource(pathToResourceOnClasspath);
        // TODO new ProtoIO().merge(resource, DynamicMessage.newBuilder(descriptor));
        new ProtoIO().read(resource, builder);
    }

    public void write(Message message, WritableResource resource) throws IOException {
        if (ProtobufMediaTypes.PROTOBUF_BINARY.equals(resource.mediaType())) {
            try (OutputStream os = resource.byteSink().openBufferedStream()) {
                message.writeTo(os);
            }
        } else {
            try (Writer writer = resource.charSink(UTF_8).openBufferedStream()) {
                if (resource.mediaType().is(PROTOBUF_TEXTPROTO_UTF_8.withoutParameters())) {
                    TextFormat.printer()
                            .escapingNonAscii(false)
                            .usingTypeRegistry(typeRegistry)
                            .print(message, writer);
                } else if (resource.mediaType().is(PROTOBUF_JSON_UTF_8.withoutParameters())) {
                    JsonFormat.printer()
                            .usingTypeRegistry(typeRegistry)
                            .omittingInsignificantWhitespace()
                            .appendTo(message, writer);
                } else if (resource.mediaType().is(PROTOBUF_YAML_UTF_8.withoutParameters())) {
                    var sb = new StringBuffer();
                    JsonFormat.printer()
                            .usingTypeRegistry(typeRegistry)
                            .omittingInsignificantWhitespace()
                            .appendTo(message, sb);
                    var json = sb.toString();
                    var yaml = YamlJson.jsonToYaml(json);
                    writer.write(yaml);
                } else {
                    throw new IllegalArgumentException(
                            "TODO Implement for missing mediaType: " + resource);
                }
            }
        }
    }

    // TODO Refactor callers to not require returning B builder argument
    public <B extends Builder> B read(ReadableResource resource, B builder) throws IOException {
        MediaType mediaType = resource.mediaType();
        if (normalizedNoParamsEquals(mediaType, PROTOBUF_BINARY)) {
            try (InputStream is = resource.byteSource().openBufferedStream()) {
                builder.mergeFrom(is, extensionRegistry);
            }
        } else {
            try (Reader reader = resource.charSource(UTF_8).openBufferedStream()) {
                if (normalizedNoParamsEquals(mediaType, PROTOBUF_TEXTPROTO_UTF_8)) {
                    TextFormat.getParser().merge(reader, extensionRegistry, builder);
                } else if (normalizedNoParamsEquals(mediaType, PROTOBUF_JSON_UTF_8, JSON_UTF_8)) {
                    JsonFormat.parser().usingTypeRegistry(typeRegistry).merge(reader, builder);
                } else if (normalizedNoParamsEquals(mediaType, PROTOBUF_YAML_UTF_8, YAML_UTF_8)) {
                    var yaml = resource.charSource(UTF_8).read();
                    var json = YamlJson.yamlToJson(yaml);
                    JsonFormat.parser().usingTypeRegistry(typeRegistry).merge(json, builder);
                } else {
                    throw new IllegalArgumentException(
                            mediaType + " unknown mediaType for URI: " + resource);
                }
            } catch (TextFormat.ParseException e) {
                throw new TextParseException(resource.uri(), e);
            } catch (InvalidProtocolBufferException e) {
                throw new InvalidProtocolBufferException(resource + " :: " + e.getMessage(), e);
            }
        }
        return builder;
    }

    public <M extends Message> M read(
            ReadableResource resource, Builder builder, Class<M> messageClass) throws IOException {
        return (M) read(resource, builder).build();
    }

    // TODO Later this could be replaced by a more general Resource format conversion framework
    public void convert(ReadableResource in, Builder builder, WritableResource out)
            throws IOException {
        read(in, builder);
        var built = builder.build();
        write(built, out);
    }

    public static class TextParseException extends TextFormat.ParseException {

        private final URI uri;

        public TextParseException(URI uri, TextFormat.ParseException e) {
            super(e.getLine(), e.getColumn(), uri.toString() + ":" + e.getMessage());
            this.uri = uri;
        }
    }
}

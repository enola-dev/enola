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

import static com.google.common.net.MediaType.JSON_UTF_8;

import static dev.enola.common.io.mediatype.MediaTypes.normalizedNoParamsEquals;
import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTOBUF_BINARY;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTOBUF_JSON_UTF_8;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTOBUF_TEXTPROTO_UTF_8;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTOBUF_YAML_UTF_8;

import static java.util.Objects.requireNonNull;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.net.MediaType;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.TextFormat;
import com.google.protobuf.TypeRegistry;
import com.google.protobuf.util.JsonFormat;

import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.yamljson.YamlJson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

/**
 * Converts Protocol Buffer {@link Message}s from/to {@link TextFormat}, {@link JsonFormat} and
 * YAML.
 */
public class ProtoIO {

    // TODO scan for proto-file and proto-message headers to support DynamicMessage
    // TODO support proto-import?

    private final ExtensionRegistry extensionRegistry;
    private final TypeRegistry typeRegistry;

    private final TextFormat.Parser textFormatParser;

    private ProtoIO(ExtensionRegistry extensionRegistry, TypeRegistry typeRegistry) {
        this.extensionRegistry = extensionRegistry;
        this.typeRegistry = typeRegistry;

        this.textFormatParser =
                TextFormat.Parser.newBuilder().setTypeRegistry(typeRegistry).build();
    }

    public ProtoIO(TypeRegistry typeRegistry) {
        this(ExtensionRegistry.getEmptyRegistry(), requireNonNull(typeRegistry, "typeRegistry"));
    }

    public ProtoIO() {
        this(ExtensionRegistry.getEmptyRegistry(), TypeRegistry.newBuilder().build());
    }

    @Deprecated // TODO This is ugly... remove!
    @VisibleForTesting // Shortcut intended for unit tests. This uses an empty TypeRegistry.
    public static void check(String pathToResourceOnClasspath, Message.Builder builder)
            throws IOException {
        ReadableResource resource = new ClasspathResource(pathToResourceOnClasspath);
        // TODO new ProtoIO().merge(resource, DynamicMessage.newBuilder(descriptor));
        new ProtoIO().read(resource, builder);
    }

    private static boolean isEmpty(Reader reader) throws IOException {
        if (!reader.markSupported()) {
            throw new IllegalArgumentException("Reader !markSupported()");
        }
        reader.mark(3);
        var first = reader.read();
        reader.reset();
        return first == -1;
    }

    public void write(Message message, WritableResource resource) throws IOException {
        MediaType mediaType = resource.mediaType();
        if (ProtobufMediaTypes.PROTOBUF_BINARY.equals(mediaType.withoutParameters())) {
            try (OutputStream os = resource.byteSink().openBufferedStream()) {
                message.writeTo(os);
            }
        } else {
            try (Writer writer = resource.charSink().openBufferedStream()) {
                // TODO Use the new ResourceConverter infrastructure here...
                if (normalizedNoParamsEquals(mediaType, PROTOBUF_TEXTPROTO_UTF_8)) {
                    TextFormat.printer()
                            .escapingNonAscii(false)
                            .usingTypeRegistry(typeRegistry)
                            .print(message, writer);

                } else if (normalizedNoParamsEquals(mediaType, PROTOBUF_JSON_UTF_8, JSON_UTF_8)
                        || mediaType.subtype().endsWith("+json")) {
                    JsonFormat.printer()
                            .usingTypeRegistry(typeRegistry)
                            .omittingInsignificantWhitespace()
                            .appendTo(message, writer);

                } else if (normalizedNoParamsEquals(mediaType, PROTOBUF_YAML_UTF_8, YAML_UTF_8)
                        || mediaType.subtype().endsWith("+yaml")) {
                    var sb = new StringBuilder();
                    JsonFormat.printer()
                            .usingTypeRegistry(typeRegistry)
                            .omittingInsignificantWhitespace()
                            .appendTo(message, sb);
                    var json = sb.toString();
                    var yaml = YamlJson.jsonToYaml(json);
                    writer.write(yaml);

                } else {
                    throw new IllegalArgumentException(
                            "TODO Implement for missing mediaType: "
                                    + mediaType
                                    + " of URI: "
                                    + resource.uri());
                }
            }
        }
    }

    // TODO Refactor callers to not require returning B builder argument
    public <B extends Message.Builder> B read(ReadableResource resource, B builder)
            throws IOException {
        MediaType mediaType = resource.mediaType();
        if (normalizedNoParamsEquals(mediaType, PROTOBUF_BINARY)) {
            try (InputStream is = resource.byteSource().openBufferedStream()) {
                builder.mergeFrom(is, extensionRegistry);
            }
        } else {
            try (Reader reader = resource.charSource().openBufferedStream()) {
                // TODO Use the new ResourceConverter infrastructure here...
                if (normalizedNoParamsEquals(mediaType, PROTOBUF_TEXTPROTO_UTF_8)) {
                    textFormatParser.merge(reader, extensionRegistry, builder);
                } else if (normalizedNoParamsEquals(mediaType, PROTOBUF_JSON_UTF_8, JSON_UTF_8)) {
                    if (!isEmpty(reader)) {
                        JsonFormat.parser().usingTypeRegistry(typeRegistry).merge(reader, builder);
                    }
                } else if (normalizedNoParamsEquals(mediaType, YAML_UTF_8)
                        || mediaType.subtype().endsWith("+yaml")) {
                    var yaml = resource.charSource().read();
                    var json = YamlJson.yamlToJson(yaml);
                    if (!json.isEmpty()) {
                        JsonFormat.parser().usingTypeRegistry(typeRegistry).merge(json, builder);
                    }
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

    @SuppressWarnings("unchecked")
    public <M extends Message> M read(
            ReadableResource resource, Message.Builder builder, Class<M> messageClass)
            throws IOException {
        return (M) read(resource, builder).build();
    }

    /** Deprecated; switch to using MessageResourceConverter. */
    @Deprecated // TODO Remove ProtoIO#convert by moving it into MessageResourceConverter
    public void convert(ReadableResource in, Message.Builder builder, WritableResource out)
            throws IOException {
        read(in, builder);
        var built = builder.build();
        write(built, out);
    }

    public static class TextParseException extends TextFormat.ParseException {

        private final URI uri;

        public TextParseException(URI uri, TextFormat.ParseException e) {
            super(e.getLine(), e.getColumn(), uri + ":" + e.getMessage());
            this.uri = uri;
        }

        public URI getUri() {
            return uri;
        }
    }
}

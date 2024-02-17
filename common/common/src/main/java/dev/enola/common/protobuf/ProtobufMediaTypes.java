/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2024 The Enola <https://enola.dev> Authors
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

import static java.util.Collections.emptySet;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.MediaTypeProvider;
import dev.enola.common.io.mediatype.MediaTypes;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ProtobufMediaTypes implements MediaTypeProvider {
    // TODO move this class into the common.proto module!

    // TODO Introduce parameters like messageType to indicate .textproto root message type? (And
    // "sniffing" them.)

    // The *.proto ("schemas") files, as well as "Text Proto" (and their JSON and YAML equivalent)
    // are text/* and not application/* whereas the binary serialization wire format is an
    // "application/*";
    // based on https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types.

    /**
     * MediaType parameter to indicate the resource's root message fully qualified name. Inspired by
     * https://protobuf.dev/reference/protobuf/textformat-spec/#header.
     */
    public static final String PARAMETER_PROTO_MESSAGE = "proto-message";

    public static MediaType setProtoMessageFQN(MediaType mediaType, String protoFQN) {
        return mediaType.withParameter(PARAMETER_PROTO_MESSAGE, protoFQN);
    }

    public static Optional<String> getProtoMessageFQN(MediaType mediaType) {
        return MediaTypes.parameter(mediaType, PARAMETER_PROTO_MESSAGE);
    }

    public static final MediaType PROTO_UTF_8 =
            MediaType.create("text", "proto").withCharset(Charsets.UTF_8);

    public static final MediaType PROTOBUF_TEXTPROTO_UTF_8 =
            MediaType.create("text", "protobuf").withCharset(Charsets.UTF_8);

    public static final MediaType PROTOBUF_JSON_UTF_8 =
            MediaType.create("text", "protobuf+json").withCharset(Charsets.UTF_8);

    public static final MediaType PROTOBUF_YAML_UTF_8 =
            MediaType.create("text", "protobuf+yaml").withCharset(Charsets.UTF_8);

    // https://datatracker.ietf.org/doc/html/draft-rfernando-protocol-buffers-00
    public static final MediaType PROTOBUF_BINARY = MediaType.create("application", "protobuf");

    @Override
    public Map<MediaType, Set<MediaType>> knownTypesWithAlternatives() {
        return ImmutableMap.of(
                PROTO_UTF_8,
                emptySet(),
                PROTOBUF_TEXTPROTO_UTF_8,
                emptySet(),
                PROTOBUF_JSON_UTF_8,
                emptySet(),
                PROTOBUF_YAML_UTF_8,
                emptySet(),
                PROTOBUF_BINARY,
                // https://stackoverflow.com/questions/30505408/what-is-the-correct-protobuf-content-type
                ImmutableSet.of(
                        MediaType.create("application", "x-protobuf"),
                        MediaType.create("application", "vnd.google.protobuf")));
    }

    @Override
    public Map<String, MediaType> extensionsToTypes() {
        // https://protobuf.dev/programming-guides/techniques/#suffixes
        return ImmutableMap.of(
                "proto",
                ProtobufMediaTypes.PROTO_UTF_8,
                // TODO This parameter isn't actually used for anything... yet.
                "proto.binpb",
                ProtobufMediaTypes.PROTOBUF_BINARY.withParameter(
                        PARAMETER_PROTO_MESSAGE, "google.protobuf.FileDescriptorSet"),
                "binpb",
                ProtobufMediaTypes.PROTOBUF_BINARY,
                "pb",
                ProtobufMediaTypes.PROTOBUF_BINARY,
                "textproto",
                ProtobufMediaTypes.PROTOBUF_TEXTPROTO_UTF_8,
                "txtpb",
                ProtobufMediaTypes.PROTOBUF_TEXTPROTO_UTF_8);
    }
}

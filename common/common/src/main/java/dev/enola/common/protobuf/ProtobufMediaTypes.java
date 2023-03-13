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

import static java.util.Collections.emptySet;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.net.MediaType;

import dev.enola.common.io.mediatype.MediaTypeProvider;

import java.util.Map;
import java.util.Set;

public class ProtobufMediaTypes implements MediaTypeProvider {
    // TODO move this class into the (TBD) common.proto module

    // TODO Introduce parameters like messageType to indicate .textproto root message type? (And
    // "sniffing" them.)

    // The *.proto ("schemas") files, as well as "Text Proto" (and their JSON and YAML equivalent)
    // are text/* and not application/* whereas the binary serialization wire format is an
    // "application/*";
    // based on https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types.

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
                Sets.newHashSet(
                        MediaType.create("application", "x-protobuf"),
                        MediaType.create("application", "vnd.google.protobuf")));
    }
}

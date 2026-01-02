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
package dev.enola.cli;

import com.google.common.net.MediaType;

import dev.enola.common.protobuf.ProtobufMediaTypes;
import dev.enola.rdf.io.RdfMediaTypes;
import dev.enola.thing.gen.graphcommons.GraphCommonsMediaType;
import dev.enola.thing.gen.graphviz.GraphvizMediaType;

public enum Format {
    Turtle,

    JSONLD,

    Graphviz,

    GraphCommons,

    TextProto,

    ProtoYAML,

    ProtoJSON,

    BinaryPB;

    MediaType toMediaType() {
        return switch (this) {
            case Turtle -> RdfMediaTypes.TURTLE;
            case JSONLD -> RdfMediaTypes.JSON_LD;
            case Graphviz -> GraphvizMediaType.GV;
            case GraphCommons -> GraphCommonsMediaType.GCJSON;

            case TextProto -> ProtobufMediaTypes.PROTOBUF_TEXTPROTO_UTF_8;
            case ProtoYAML -> ProtobufMediaTypes.PROTOBUF_YAML_UTF_8;
            case ProtoJSON -> ProtobufMediaTypes.PROTOBUF_JSON_UTF_8;
            case BinaryPB -> ProtobufMediaTypes.PROTOBUF_BINARY;
        };
    }
}

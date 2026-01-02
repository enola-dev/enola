/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.protobuf.schema;

import com.google.common.collect.ImmutableMap;
import com.google.genai.types.Schema;
import com.google.genai.types.Type.Known;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

import org.jspecify.annotations.Nullable;

/**
 * Conversion from <a href="https://protobuf.dev">Protocol Buffers</a> (model, not instances) to <a
 * href="https://googleapis.github.io/js-genai/release_docs/interfaces/types.Schema.html">Google
 * GenAI Schema</a> (which is based on <a
 * href="https://spec.openapis.org/oas/v3.0.3#schema-object">OpenAPI 3.0 schema</a>, which in turn
 * is very much like <a href="https://json-schema.org">JSON Schema</a>).
 *
 * <p>The Schema matches how a Proto message (instance, not model) would be serialized in <a
 * href="https://protobuf.dev/programming-guides/json/">ProtoJSON format</a>.
 *
 * <p>Other than just being 💃 fun, this is (very) useful to be able to 🧑🏽‍🍼 feed 🥄 Language
 * Models, such as Google 🔮 Gemini, 🥢 Protos instead of only 📜 text, for 🧰 tools (whether
 * through MCP 🔱 or not), or to configure their <a
 * href="https://github.com/googleapis/java-genai#generate-content-with-json-response-schema">expected
 * structured output</a>.
 *
 * @author <a href="http://www.vorburger.ch">Michael Vorburger.ch</a>
 */
public class MessageDescriptorToSchemaConverter {

    // TODO Research and compare with any existing prior art, if any?
    // TODO Announce this to ProtoBuf group

    public Schema convert(Message message) {
        return convert(message.getDescriptorForType());
    }

    public Schema convert(Descriptor descriptor) {
        var schema = Schema.builder();
        schema.type(Known.OBJECT);
        schema.title(descriptor.getFullName());

        var properties = ImmutableMap.<String, Schema>builder();
        for (var field : descriptor.getFields()) {
            var fieldSchema = Schema.builder();
            var name = field.getJsonName();
            var type = field.getType();
            fieldSchema.type(type(type));
            var format = format(field.getType());
            if (format != null) fieldSchema.format(format);
            properties.put(name, fieldSchema.build());
        }
        schema.properties(properties.build());

        // TODO schema.propertyOrdering()

        return schema.build();
    }

    private Known type(FieldDescriptor.Type type) {
        // TODO Handle Timestamp specially
        // https://protobuf.dev/programming-guides/proto3/#scalar
        // https://spec.openapis.org/oas/v3.0.3#data-types
        return switch (type) {
            case STRING, BYTES -> Known.STRING;
            case BOOL -> Known.BOOLEAN;
            case DOUBLE, FLOAT -> Known.NUMBER;

            case INT32,
                    INT64,
                    UINT32,
                    UINT64,
                    SINT32,
                    SINT64,
                    FIXED32,
                    FIXED64,
                    SFIXED32,
                    SFIXED64 ->
                    Known.INTEGER;

            case MESSAGE -> Known.OBJECT;
            // TODO Handle ENUM
            // TODO Handle GROUP
            case ENUM, GROUP -> throw new UnsupportedOperationException(type.name());
        };
    }

    private @Nullable String format(FieldDescriptor.Type type) {
        // TODO Handle Timestamp specially and return "date-time"
        return switch (type) {
            case DOUBLE -> "double";
            case FLOAT -> "float";
            case INT64, UINT64, FIXED64, SFIXED64, SINT64 -> "int64";
            case INT32, UINT32, FIXED32, SFIXED32, SINT32 -> "int32";
            // BYTES is "byte" (base64), NOT "binary" ('any sequence of octets')
            case BYTES -> "byte";
            default -> null;
        };
    }
}

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
package dev.enola.core.view;

import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;

import dev.enola.core.IDs;
import dev.enola.core.proto.ID;
import dev.enola.core.proto.Thing;
import dev.enola.core.proto.Thing.Builder;

@SuppressWarnings("restriction")
public class Things {

    // TODO Replace uses of this "old" with new dev.enola.thing.MessageToThingConverter API
    // TODO Delete this

    public static Thing.Builder from(Message message) {
        return from(message, true);
    }

    private static Thing.Builder from(Message message, boolean isTopLevel) {
        return switch (message) {
            case ID id -> toThing(id);
            case Timestamp ts -> toThing(Timestamps.toString(ts));
            default -> Thing.newBuilder().setStruct(fromGeneric(message, isTopLevel));
        };
    }

    private static Thing.Struct.Builder fromGeneric(Message message, boolean isTopLevel) {
        var struct = Thing.Struct.newBuilder();
        if (isTopLevel) {
            var protoFQN = message.getDescriptorForType().getFullName();
            // NB: Intentionally "$proto" instead of "$type" because TBD #soon,
            // or "$schema", because in the future with non-Proto direct-JSON
            // with https://json-schema.org the $schema will be as-is from JSON,
            // and the $type and $proto are Enola's.
            struct.putFields("$proto", toProto(protoFQN).build());
        }
        for (var field : message.getAllFields().entrySet()) {
            var descriptor = field.getKey();
            var name = descriptor.getName();
            struct.putFields(name, listToThing(field.getValue(), descriptor, message));
        }
        return struct;
    }

    private static Thing listToThing(Object object, FieldDescriptor field, Message message) {
        if (field.isRepeated()) {
            var thing = Thing.newBuilder();
            var n = message.getRepeatedFieldCount(field);
            var valueList = Thing.List.newBuilder();
            for (int i = 0; i < n; i++) {
                valueList.addEntries(toThing(message.getRepeatedField(field, i), field, message));
            }
            thing.setList(valueList);
            return thing.build();
        } else {
            return toThing(object, field, message).build();
        }
    }

    private static Thing.Builder toThing(Object object, FieldDescriptor field, Message message) {
        var type = field.getType();
        if (FieldDescriptor.Type.MESSAGE.equals(type)) {
            return from((Message) object, false);
        } else if ("google.protobuf.FieldDescriptorProto.type_name".equals(field.getFullName())) {
            // TODO This eventually shouldn't be hard-coded anymore, but declarative
            return toProto(object.toString().substring(1));
        } else {
            return toThing(object.toString());
        }
    }

    private static Builder toProto(String protoFQN) {
        return toThing(protoFQN, "enola:proto/" + protoFQN);
    }

    private static Thing.Builder toThing(ID id) {
        // TODO This eventually shouldn't be hard-coded anymore, but declarative
        var path = IDs.toPath(id);
        return toThing(path, "enola:" + path);
    }

    @VisibleForTesting
    static Thing.Builder toThing(String string) {
        var text = Thing.LinkedText.newBuilder();
        text.setString(string);
        if (string.startsWith("https://") || string.startsWith("http://")) {
            text.setUri(string);
        }
        return Thing.newBuilder().setText(text);
    }

    @VisibleForTesting
    static Thing.Builder toThing(String string, String uri) {
        var text = Thing.LinkedText.newBuilder();
        text.setString(string).setUri(uri);
        return Thing.newBuilder().setText(text);
    }
}

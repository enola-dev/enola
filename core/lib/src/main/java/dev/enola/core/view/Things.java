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

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;

import dev.enola.core.IDs;
import dev.enola.core.proto.ID;
import dev.enola.core.proto.Thing;

@SuppressWarnings("restriction")
public class Things {

    public static Thing.Builder from(Message message) {
        return switch (message) {
            case ID id -> toThing(IDs.toPath(id));
            case Timestamp ts -> toThing(Timestamps.toString(ts));
            default -> Thing.newBuilder().setStruct(fromGeneric(message));
        };
    }

    private static Thing.Struct.Builder fromGeneric(Message message) {
        var struct = Thing.Struct.newBuilder();
        for (var field : message.getAllFields().entrySet()) {
            var descriptor = field.getKey();
            var name = descriptor.getName();
            struct.putFields(name, listToThing(field.getValue(), descriptor, message));
        }
        struct.setTypeUri("enola:proto/" + message.getDescriptorForType().getFullName());
        return struct;
    }

    private static Thing listToThing(Object object, FieldDescriptor field, Message message) {
        if (field.isRepeated()) {
            var thing = Thing.newBuilder();
            var n = message.getRepeatedFieldCount(field);
            var valueList = Thing.List.newBuilder();
            for (int i = 0; i < n; i++) {
                // message.getDescriptorForType().findFieldByNumber(i)
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
            return from((Message) object);
        } else {
            return toThing(object.toString());
        }
    }

    private static Thing.Builder toThing(String string) {
        return Thing.newBuilder().setString(string);
    }
}

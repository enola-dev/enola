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

import dev.enola.core.proto.List;
import dev.enola.core.proto.ThingView;
import dev.enola.core.proto.Value;

@SuppressWarnings("restriction")
public class ThingViews {

    public static ThingView.Builder from(Message message) {
        var thingView = ThingView.newBuilder();
        for (var field : message.getAllFields().entrySet()) {
            var descriptor = field.getKey();
            var name = descriptor.getName();
            thingView.putFields(name, listToValue(field.getValue(), descriptor, message));
        }
        thingView.setTypeUri("enola:proto/" + message.getDescriptorForType().getFullName());
        return thingView;
    }

    private static Value listToValue(Object object, FieldDescriptor field, Message message) {
        if (field.isRepeated()) {
            var value = Value.newBuilder();
            var n = message.getRepeatedFieldCount(field);
            var valueList = List.newBuilder();
            for (int i = 0; i < n; i++) {
                // message.getDescriptorForType().findFieldByNumber(i)
                valueList.addEntries(toValue(message.getRepeatedField(field, i), field, message));
            }
            value.setList(valueList);
            return value.build();
        } else {
            return toValue(object, field, message);
        }
    }

    private static Value toValue(Object object, FieldDescriptor field, Message message) {
        var type = field.getType();
        var value = Value.newBuilder();
        if (FieldDescriptor.Type.MESSAGE.equals(type)) {
            value.setStruct(from((Message) object));
        } else {
            value.setString(object.toString());
        }
        return value.build();
    }
}

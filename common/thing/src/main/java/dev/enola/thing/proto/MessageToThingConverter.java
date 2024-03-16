/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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
package dev.enola.thing.proto;

// TODO package dev.enola.thing.message;

import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.convert.Converter;
import dev.enola.thing.Thing;
import dev.enola.thing.Value;

public class MessageToThingConverter implements Converter<MessageWithIRI, Thing.Builder> {
    // TODO Converter<MessageOrBuilder, to avoid build(), when not required?

    // TODO Fully replace "old" dev.enola.core.view.Things with this new API

    // TODO com.google.protobuf.Struct support!

    @Override
    public Thing.Builder convert(MessageWithIRI messageWithIRI) throws ConversionException {
        var thing = from(messageWithIRI.message(), true);
        thing.setIri(messageWithIRI.iri());
        return thing;
    }

    private Thing.Builder from(Message message, boolean isTopLevel) {
        var thing = Thing.newBuilder();
        if (isTopLevel) {
            ProtoTypes.addProtoField(thing, message);
            // TODO Set the "$etype" (as .. what, enola.dev/type?)
        }
        for (var field : message.getAllFields().entrySet()) {
            var descriptor = field.getKey();
            var key = ProtoTypes.getFieldERI(descriptor);
            thing.putFields(key, listToThing(field.getValue(), descriptor, message).build());
        }
        return thing;
    }

    private Value.Builder listToThing(Object object, FieldDescriptor field, Message message) {
        if (field.isRepeated()) {
            var n = message.getRepeatedFieldCount(field);
            var values = new Value.Builder[n];
            for (int i = 0; i < n; i++) {
                values[i] = toThing(message.getRepeatedField(field, i), field, message);
            }
            return toList(values);
        } else {
            return toThing(object, field, message);
        }
    }

    Value.Builder toList(Value.Builder... values) {
        var valueList = dev.enola.thing.Value.List.newBuilder();
        for (var value : values) {
            valueList.addValues(value);
        }
        return Value.newBuilder().setList(valueList);
    }

    private Value.Builder toThing(Object object, FieldDescriptor field, Message message) {
        var type = field.getType();
        if (FieldDescriptor.Type.MESSAGE.equals(type)) {
            return Value.newBuilder().setStruct(from((Message) object, false));
        } else if ("google.protobuf.FieldDescriptorProto.type_name".equals(field.getFullName())) {
            // TODO This eventually shouldn't be hard-coded anymore, but declarative
            return ProtoTypes.toThingValueLink(object.toString().substring(1));
        } else {
            return toValue(object.toString());
        }
    }

    @VisibleForTesting
    Value.Builder toValue(String string) {
        if (string.startsWith("https://") || string.startsWith("http://")) {
            return toLink(string, "");
        } else {
            return Value.newBuilder().setString(string);
        }
        // TODO Support Literal, with datatype, IFF Type says so!
    }

    static Value.Builder toLink(String iri, String label) {
        var link = Value.Link.newBuilder().setIri(iri).setLabel(label);
        return Value.newBuilder().setLink(link);
    }
}

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
package dev.enola.thing.message;

import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;

import dev.enola.common.convert.Converter;
import dev.enola.thing.KIRI;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.proto.Value;

import java.util.Base64;

public class MessageToThingConverter implements Converter<MessageWithIRI, Thing.Builder> {

    // TODO Fully replace "old" dev.enola.core.view.Things with this new API

    // TODO com.google.protobuf.Struct support!

    @Override
    public Thing.Builder convert(MessageWithIRI messageWithIRI) {
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
        var valueList = dev.enola.thing.proto.Value.List.newBuilder();
        for (var value : values) {
            valueList.addValues(value);
        }
        return Value.newBuilder().setList(valueList);
    }

    private Value.Builder toThing(Object object, FieldDescriptor field, Message message) {
        return switch (object) {
                // TODO case ID id -> ? ;
            case Timestamp ts -> toLiteral(Timestamps.toString(ts), KIRI.XSD.TS);
            default -> toThingByFieldType(object, field, message);
        };
    }

    private Value.Builder toThingByFieldType(
            Object object, FieldDescriptor field, Message message) {
        return switch (field.getType()) {
            case FieldDescriptor.Type.ENUM -> toEnumLink((EnumValueDescriptor) object);
            case FieldDescriptor.Type.BOOL -> toLiteral(object.toString(), KIRI.XSD.BOOL);
            case FieldDescriptor.Type.BYTES -> toLiteral(b64((ByteString) object), KIRI.XSD.BIN64);
            case FieldDescriptor.Type.DOUBLE -> toLiteral(object.toString(), KIRI.XSD.DOUBLE);
            case FieldDescriptor.Type.FLOAT -> toLiteral(object.toString(), KIRI.XSD.FLOAT);
            case FieldDescriptor.Type.INT64 -> toLiteral(object.toString(), KIRI.XSD.INT64);
            case FieldDescriptor.Type.FIXED64 -> toLiteral(object.toString(), KIRI.XSD.INT64);
            case FieldDescriptor.Type.SFIXED64 -> toLiteral(object.toString(), KIRI.XSD.INT64);
            case FieldDescriptor.Type.SINT64 -> toLiteral(object.toString(), KIRI.XSD.INT64);
            case FieldDescriptor.Type.UINT64 ->
                    toLiteral(Long.toUnsignedString((Long) object), KIRI.XSD.UINT64);
            case FieldDescriptor.Type.INT32 -> toLiteral(object.toString(), KIRI.XSD.INT32);
            case FieldDescriptor.Type.FIXED32 -> toLiteral(object.toString(), KIRI.XSD.INT32);
            case FieldDescriptor.Type.SFIXED32 -> toLiteral(object.toString(), KIRI.XSD.INT32);
            case FieldDescriptor.Type.SINT32 -> toLiteral(object.toString(), KIRI.XSD.INT32);
            case FieldDescriptor.Type.UINT32 ->
                    toLiteral(Integer.toUnsignedString((Integer) object), KIRI.XSD.UINT32);
            case FieldDescriptor.Type.MESSAGE ->
                    Value.newBuilder().setStruct(from((Message) object, false));
            case FieldDescriptor.Type.GROUP ->
                    Value.newBuilder().setStruct(from((Message) object, false));
            default -> toThingByFieldName(object, field, message);
        };
    }

    private Value.Builder toEnumLink(EnumValueDescriptor enumValue) {
        var iri = ProtoTypes.getEnumERI(enumValue);
        var label = enumValue.getName();
        return toLink(iri, label);
    }

    private String b64(ByteString byteString) {
        return Base64.getEncoder().encodeToString(byteString.toByteArray());
    }

    private Value.Builder toThingByFieldName(
            Object object, FieldDescriptor field, Message message) {
        return switch (field.getFullName()) {
            case "google.protobuf.FieldDescriptorProto.type_name" ->
                    // TODO This eventually shouldn't be hard-coded anymore, but declarative
                    ProtoTypes.toThingValueLink(object.toString().substring(1));
            default -> toValue(object.toString());
        };
    }

    Value.Builder toLiteral(String value, String datatype) {
        var literal = Value.Literal.newBuilder().setValue(value).setDatatype(datatype);
        return Value.newBuilder().setLiteral(literal);
    }

    @VisibleForTesting
    Value.Builder toValue(String string) {
        if (string.startsWith("https://") || string.startsWith("http://")) {
            return toLink(string, "");
        } else {
            return Value.newBuilder().setString(string);
        }
    }

    static Value.Builder toLink(String iri, String label) {
        var link = Value.Link.newBuilder().setIri(iri).setLabel(label);
        return Value.newBuilder().setLink(link);
    }
}

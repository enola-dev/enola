/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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
import com.google.common.base.Strings;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.MessageOrBuilder;

import dev.enola.thing.proto.Thing;
import dev.enola.thing.proto.Value;

/**
 * ProtoTypes defines the Enola Types related to <a href="https://protobuf.dev">Protocol
 * Buffers</a>.
 */
public class ProtoTypes {

    @VisibleForTesting
    static final String MESSAGE_DESCRIPTOR_PREDICATE_IRI = "http://enola.dev/proto/message";

    // TODO Change enola:/enola.dev/proto/ to https://enola.dev/proto/

    public static final String MESSAGE_DESCRIPTOR_ERI_PREFIX = "enola:/enola.dev/proto/message/";

    public static final String FIELD_DESCRIPTOR_ERI_PREFIX = "enola:/enola.dev/proto/field/";

    public static final String FIELD_ENUM_VALUE_ERI_PREFIX = "enola:/enola.dev/proto/enum-value/";

    static Thing.Builder addProtoField(Thing.Builder thing, MessageOrBuilder message) {
        // NB: We're setting the field that describes what Proto (Descriptor)
        // Thing (originally) has (had). This is *NOT* the same as e.g.
        // the "$etype" or (https://json-schema.org) "$schema", or other schemas by Enola's
        // "multi-schema support".
        var messageDescriptor = message.getDescriptorForType();
        thing.putProperties(
                MESSAGE_DESCRIPTOR_PREDICATE_IRI, toThingValueLink(messageDescriptor).build());
        return thing;
    }

    private static Value.Builder toThingValueLink(Descriptor messageDescriptor) {
        return toThingValueLink(messageDescriptor.getFullName());
    }

    public static String getMessageERI(Descriptor messageDescriptor) {
        return getMessageERI(messageDescriptor.getFullName());
    }

    public static Value.Builder toThingValueLink(String messageFQN) {
        return MessageToThingConverter.toLink(getMessageERI(messageFQN));
    }

    private static String getMessageERI(String messageFQN) {
        return MESSAGE_DESCRIPTOR_ERI_PREFIX + messageFQN;
    }

    public static String getFieldERI(FieldDescriptor fieldDescriptor) {
        return FIELD_DESCRIPTOR_ERI_PREFIX
                + requireNonNullOrEmpty(fieldDescriptor.getContainingType().getFullName())
                + "/"
                + fieldDescriptor.getNumber();
    }

    public static String getEnumValueERI(EnumValueDescriptor enumValue) {
        return FIELD_ENUM_VALUE_ERI_PREFIX
                + requireNonNullOrEmpty(enumValue.getType().getFullName())
                + "/"
                + enumValue.getIndex();
    }

    static String requireNonNullOrEmpty(String test) {
        if (!Strings.isNullOrEmpty(test)) return test;
        else throw new IllegalArgumentException();
    }
}

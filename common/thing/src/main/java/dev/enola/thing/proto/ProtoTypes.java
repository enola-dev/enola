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
import com.google.common.base.Strings;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.MessageOrBuilder;

import dev.enola.thing.Thing;
import dev.enola.thing.Value;
import dev.enola.thing.Value.Builder;

/**
 * ProtoTypes defines the Enola @{link Type}/s related to <a href="https://protobuf.dev">Protocol
 * Buffers</a>.
 */
public class ProtoTypes {

    @VisibleForTesting
    static final String MESSAGE_DESCRIPTOR_PREDICATE_IRI = "http://enola.dev/proto/message";

    private static final String MESSAGE_DESCRIPTOR_ERI = "enola:/enola.dev/proto/message/";

    private static final String FIELD_DESCRIPTOR_ERI = "enola:/enola.dev/proto/field/";

    static Thing.Builder addProtoField(Thing.Builder thing, MessageOrBuilder message) {
        // NB: We're setting the field that describes what Proto (Descriptor)
        // Thing (originally) has (had). This is *NOT* the same as e.g.
        // the "$etype" or (https://json-schema.org) "$schema", or other schemas by Enola's
        // "multi-schema support".
        var messageDescriptor = message.getDescriptorForType();
        thing.putFields(
                MESSAGE_DESCRIPTOR_PREDICATE_IRI, toThingValueLink(messageDescriptor).build());
        return thing;
    }

    static Value.Builder toThingValueLink(Descriptor messageDescriptor) {
        return toThingValueLink(messageDescriptor.getFullName());
    }

    public static Builder toThingValueLink(String messageFQN) {
        var iri = getMessageERI(messageFQN);
        return MessageToThingConverter.toLink(iri, messageFQN);
    }

    private static String getMessageERI(String messageFQN) {
        return MESSAGE_DESCRIPTOR_ERI + messageFQN;
    }

    static String getFieldERI(FieldDescriptor fieldDescriptor) {
        return FIELD_DESCRIPTOR_ERI
                + requireNonNullOrEmpty(fieldDescriptor.getContainingType().getFullName())
                + "/"
                + fieldDescriptor.getNumber();
    }

    static String requireNonNullOrEmpty(String test) {
        if (!Strings.isNullOrEmpty(test)) return test;
        else throw new IllegalArgumentException();
    }
}

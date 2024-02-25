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

import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.MessageOrBuilder;

import dev.enola.thing.Thing;
import dev.enola.thing.Value;

/**
 * ProtoTypes defines the Enola @{link Type}/s related to <a href="https://protobuf.dev">Protocol
 * Buffers</a>.
 */
public class ProtoTypes {

    @VisibleForTesting
    static final String MESSAGE_DESCRIPTOR_PREDICATE_IRI = "http://enola.dev/proto";

    private static final String MESSAGE_DESCRIPTOR_ERI = "enola:/enola.dev/proto/schema/";

    static Thing.Builder addProtoField(Thing.Builder thing, MessageOrBuilder message) {
        // NB: We're setting the field that describes what Proto (Descriptor)
        // Thing (originally) has (had). This is *NOT* the same as e.g.
        // the "$etype" or (https://json-schema.org) "$schema", or other schemas by Enola's
        // "multi-schema support".
        var protoFQN = message.getDescriptorForType().getFullName();
        thing.putFields(MESSAGE_DESCRIPTOR_PREDICATE_IRI, toThingValueLink(protoFQN).build());
        return thing;
    }

    static Value.Builder toThingValueLink(String protoFQN) {
        return MessageToThingConverter.toLink(MESSAGE_DESCRIPTOR_ERI + protoFQN, protoFQN);
    }
}

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
package dev.enola.common.protobuf;

import com.google.protobuf.Any;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

/** Utility functions related to {@link Any}. */
public class Anys {

    private final DescriptorProvider descriptorProvider;

    public Anys(DescriptorProvider descriptorProvider) {
        this.descriptorProvider = descriptorProvider;
    }

    public DynamicMessage toMessage(Any any) throws InvalidProtocolBufferException {
        var typeURL = any.getTypeUrl();
        Descriptor descriptor = descriptorProvider.getDescriptorForTypeUrl(typeURL);
        return DynamicMessage.parseFrom(descriptor, any.getValue());
    }

    @SuppressWarnings("unchecked")
    public static <T extends GeneratedMessageV3> T dynamicToStaticMessage(
            Message message, GeneratedMessageV3.Builder<?> builder) {
        if (message instanceof GeneratedMessageV3) {
            return (T) message;
        }

        var bytes = message.toByteArray();
        try {
            return (T) builder.mergeFrom(bytes).buildPartial();
        } catch (IllegalArgumentException | SecurityException | InvalidProtocolBufferException e) {
            throw new IllegalArgumentException("No good: " /*+ klass*/, e);
        }
    }
}

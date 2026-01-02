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
package dev.enola.common.protobuf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.DynamicMessage;

import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.io.resource.convert.CatchingResourceConverter;

import java.io.IOException;

/** Converts Resources containing Protocol Buffer messages using the {@link ProtoIO} utility. */
public class MessageResourceConverter implements CatchingResourceConverter {

    private final DescriptorProvider descriptorProvider;
    private final ProtoIO protoIO;

    public MessageResourceConverter(ProtoIO protoIO, DescriptorProvider descriptorProvider) {
        this.descriptorProvider = descriptorProvider;
        this.protoIO = protoIO;
    }

    @Override
    public boolean convertIntoThrows(ReadableResource from, WritableResource into)
            throws IOException {
        var fromProtoFQN = ProtobufMediaTypes.getProtoMessageFQN(from.mediaType());
        if (fromProtoFQN.isPresent()) {
            Descriptors.Descriptor descriptor =
                    (Descriptor) descriptorProvider.findByName(fromProtoFQN.get());
            // TODO Use new Messages (EnolaMessages) utility here #performance
            var builder = DynamicMessage.newBuilder(descriptor);
            protoIO.convert(from, builder, into);
            return true;
        } else {
            return false;
        }
    }
}

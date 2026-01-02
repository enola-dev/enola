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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.Any;
import com.google.protobuf.BoolValue;
import com.google.protobuf.BytesValue;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.Duration;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Empty;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.FieldMask;
import com.google.protobuf.FloatValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.StringValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Timestamp;
import com.google.protobuf.Type;
import com.google.protobuf.UInt32Value;
import com.google.protobuf.UInt64Value;

/**
 * Message is a Protocol Buffers utility to create new message builders from type URLs. It is
 * optimized to use the more efficient generated code for registered types, and fall back to slower
 * {@link DynamicMessage} for unknown types.
 */
public class Messages {
    // TODO Create an interface for newBuilder() to make this ready for DI with EnolaMessages ext

    // TODO Integrate with dev.enola.common.protobuf.DescriptorProvider?

    // TODO Re-consider using MessageLite instead of Message here, in case class Wany is removed

    private final DescriptorProvider descriptorProvider;
    private final ExtensionRegistryLite extensionRegistry;
    private final ImmutableMap<String, Message> defaultInstances;

    public Messages(
            DescriptorProvider descriptorProvider, ExtensionRegistryLite extensionRegistry) {
        this(descriptorProvider, extensionRegistry, ImmutableMap.of());
    }

    protected Messages(
            DescriptorProvider descriptorProvider,
            ExtensionRegistryLite extensionRegistry,
            ImmutableMap<String, Message> defaultInstances) {
        this.descriptorProvider = descriptorProvider;
        this.extensionRegistry = extensionRegistry;
        this.defaultInstances = check(wellKnownInstances().putAll(defaultInstances).build());
    }

    private ImmutableMap<String, Message> check(ImmutableMap<String, Message> defaultInstances) {
        for (var defaultInstanceEntry : defaultInstances.entrySet()) {
            var expected = defaultInstanceEntry.getKey();
            var actual = defaultInstanceEntry.getValue().getDescriptorForType().getFullName();
            if (!expected.equals(actual)) {
                throw new IllegalArgumentException(expected + " != " + actual);
            }
        }
        return defaultInstances;
    }

    public Message.Builder newBuilder(String typeURL) {
        var typeDefaultInstance = defaultInstances.get(fullName(typeURL));
        if (typeDefaultInstance != null) {
            return typeDefaultInstance.newBuilderForType();
        } else {
            Descriptor descriptor = descriptorProvider.getDescriptorForTypeUrl(typeURL);
            return DynamicMessage.newBuilder(descriptor);
        }
    }

    public Message toMessage(Any any) {
        var typeURL = any.getTypeUrl();
        if (Strings.isNullOrEmpty(typeURL))
            throw new IllegalArgumentException("Any missing typeURL: " + any);
        var builder = newBuilder(typeURL);
        try {
            builder.mergeFrom(any.getValue(), extensionRegistry);
            return builder.build();
        } catch (InvalidProtocolBufferException e) {
            throw new IllegalArgumentException("Invalid Any: " + any.getTypeUrl(), e);
        }
    }

    private String fullName(String typeURL) {
        // https://github.com/protocolbuffers/protobuf/blob/e0942c8f298e9edad5a2d6f94cdd2dab87efcae2/src/google/protobuf/any.proto#L129
        if (!typeURL.contains(typeURL)) {
            return typeURL;
        } else if (!typeURL.endsWith("/")) {
            return typeURL.substring(typeURL.lastIndexOf('/') + 1);
        } else {
            throw new IllegalArgumentException("Invalid typeURL, ends with slash: " + typeURL);
        }
    }

    private static ImmutableMap.Builder<String, Message> wellKnownInstances() {
        return ImmutableMap.<String, Message>builderWithExpectedSize(20)
                .put(Any.getDescriptor().getFullName(), Any.getDefaultInstance())
                .put(
                        FileDescriptorSet.getDescriptor().getFullName(),
                        FileDescriptorSet.getDefaultInstance())
                .put(
                        FileDescriptorProto.getDescriptor().getFullName(),
                        FileDescriptorProto.getDefaultInstance())
                .put(
                        DescriptorProto.getDescriptor().getFullName(),
                        DescriptorProto.getDefaultInstance())
                .put(Duration.getDescriptor().getFullName(), Duration.getDefaultInstance())
                .put(Empty.getDescriptor().getFullName(), Empty.getDefaultInstance())
                .put(FieldMask.getDescriptor().getFullName(), FieldMask.getDefaultInstance())

                // https://github.com/protocolbuffers/protobuf/blob/main/src/google/protobuf/struct.proto
                .put(Struct.getDescriptor().getFullName(), Struct.getDefaultInstance())
                .put(Timestamp.getDescriptor().getFullName(), Timestamp.getDefaultInstance())
                .put(Type.getDescriptor().getFullName(), Type.getDefaultInstance())

                // https://github.com/protocolbuffers/protobuf/blob/main/src/google/protobuf/wrappers.proto
                .put(BoolValue.getDescriptor().getFullName(), BoolValue.getDefaultInstance())
                .put(BytesValue.getDescriptor().getFullName(), BytesValue.getDefaultInstance())
                .put(DoubleValue.getDescriptor().getFullName(), DoubleValue.getDefaultInstance())
                .put(FloatValue.getDescriptor().getFullName(), FloatValue.getDefaultInstance())
                .put(Int32Value.getDescriptor().getFullName(), Int32Value.getDefaultInstance())
                .put(Int64Value.getDescriptor().getFullName(), Int64Value.getDefaultInstance())
                .put(UInt64Value.getDescriptor().getFullName(), UInt64Value.getDefaultInstance())
                .put(UInt32Value.getDescriptor().getFullName(), UInt32Value.getDefaultInstance())
                .put(StringValue.getDescriptor().getFullName(), StringValue.getDefaultInstance());
    }
}

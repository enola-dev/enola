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
package dev.enola.core.tbd;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Descriptors.Descriptor;

import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.io.resource.convert.ResourceConverterChain;
import dev.enola.common.protobuf.DescriptorProvider;
import dev.enola.common.protobuf.MessageResourceConverter;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.common.protobuf.ProtobufMediaTypes;
import dev.enola.common.protobuf.YamlJsonResourceConverter;
import dev.enola.core.meta.proto.EntityKinds;
import dev.enola.core.proto.Entity;

import java.io.IOException;

/**
 * <a href="https://en.wikipedia.org/wiki/Rosetta_Stone">Rosetta Stone</a> for converting between
 * different model serialization formats.
 */
public class Rosetta {

    // TODO This class is in dev.enola.core.tbd only for now, in order to have classpath access to
    // Entities.getDescriptor() and EntityKinds.getDescriptor() in the lookupDescriptor() below but
    // eventually it should move e.g. to dev.enola.common.protobuf instead, and use a
    // DescriptorProvider as generic proto Descriptor registry.

    private final ProtoIO protoIO = new ProtoIO();

    private static final DescriptorProvider DESCRIPTOR_PROVIDER =
            new DescriptorProvider() {

                @Override
                public Descriptor findByName(String messageTypeURL) {
                    switch (messageTypeURL) {
                        case "dev.enola.core.Entity":
                            return Entity.getDescriptor();
                        case "dev.enola.core.meta.EntityKinds":
                            return EntityKinds.getDescriptor();
                    }
                    throw new IllegalArgumentException(
                            "TODO Cannot find Descriptor for: " + messageTypeURL);
                }

                @Override
                public Descriptor getDescriptorForTypeUrl(String protoMessageFullyQualifiedName) {
                    throw new UnsupportedOperationException("Unimplemented method 'findByName'");
                }
            };

    private final MessageResourceConverter messageResourceConverter =
            new MessageResourceConverter(protoIO, DESCRIPTOR_PROVIDER);

    private final ResourceConverterChain resourceConverterChain =
            new ResourceConverterChain(
                    ImmutableList.of(messageResourceConverter, new YamlJsonResourceConverter()));

    public void convert(ReadableResource in, WritableResource out) throws IOException {
        if (!resourceConverterChain.convertInto(in, out)) {
            throw new IllegalArgumentException(
                    "Without protoFQN --schema CLI arg, or ?"
                            + ProtobufMediaTypes.PARAMETER_PROTO_MESSAGE
                            + "= contentType parameter, cannot convert "
                            + in
                            + " to "
                            + out);
        }
    }
}

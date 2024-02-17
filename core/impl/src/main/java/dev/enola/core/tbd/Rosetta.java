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

import static com.google.common.net.MediaType.JSON_UTF_8;

import static dev.enola.common.io.mediatype.MediaTypes.normalizedNoParamsEquals;
import static dev.enola.common.io.mediatype.YamlMediaType.YAML_UTF_8;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTOBUF_JSON_UTF_8;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTOBUF_YAML_UTF_8;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;

import dev.enola.common.io.resource.ReadableResource;
import dev.enola.common.io.resource.WritableResource;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.common.protobuf.ProtobufMediaTypes;
import dev.enola.common.yamljson.YamlJson;
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

    /**
     * Convert.
     *
     * @param protoFQN optional, may be null; only required if in is a *.textproto
     */
    public void convert(ReadableResource in, WritableResource out) throws IOException {

        // TODO Use the new ResourceConverter infrastructure here!

        var protoFQN = ProtobufMediaTypes.getProtoMessageFQN(in.mediaType());
        var inmt = in.mediaType().withoutParameters();
        var outmt = out.mediaType().withoutParameters();
        if (protoFQN.isPresent()) {
            Descriptors.Descriptor descriptor = lookupDescriptor(protoFQN.get());
            // TODO Use new Messages (EnolaMessages) utility here #performance
            var builder = DynamicMessage.newBuilder(descriptor);
            protoIO.convert(in, builder, out);

        } else if (normalizedNoParamsEquals(inmt, PROTOBUF_JSON_UTF_8, JSON_UTF_8)
                && normalizedNoParamsEquals(outmt, PROTOBUF_YAML_UTF_8, YAML_UTF_8)) {

            out.charSink().write(YamlJson.jsonToYaml(in.charSource().read()));

        } else if (normalizedNoParamsEquals(inmt, PROTOBUF_YAML_UTF_8, YAML_UTF_8)
                && normalizedNoParamsEquals(outmt, PROTOBUF_JSON_UTF_8, JSON_UTF_8)) {

            out.charSink().write(YamlJson.yamlToJson(in.charSource().read()));

        } else {
            throw new IllegalArgumentException(
                    "Without protoFQN --schema CLI arg, or ?"
                            + ProtobufMediaTypes.PARAMETER_PROTO_MESSAGE
                            + "= contentType parameter, cannot convert "
                            + inmt
                            + " to "
                            + outmt);
        }
    }

    private Descriptors.Descriptor lookupDescriptor(String protoFQN) {
        // TODO look it up via a DescriptorProvider, instead of hard-coding
        switch (protoFQN) {
            case "dev.enola.core.Entity":
                return Entity.getDescriptor();
            case "dev.enola.core.meta.EntityKinds":
                return EntityKinds.getDescriptor();
        }
        throw new IllegalArgumentException("TODO Cannot find Descriptor for: " + protoFQN);
    }
}

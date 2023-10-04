/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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
package dev.enola.core.meta;

import static com.google.common.truth.Truth.assertThat;
import static com.google.protobuf.Any.pack;

import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTOBUF_JSON_UTF_8;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Any;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Timestamp;

import dev.enola.common.io.resource.NullResource;
import dev.enola.common.protobuf.ProtoIO;
import dev.enola.core.meta.proto.EntityKind;
import dev.enola.core.proto.ID;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TypeRegistryWrapperTest {
    @Test
    public void empty() {
        assertThat(TypeRegistryWrapper.newBuilder().build().names()).isEmpty();
    }

    @Test
    public void one() {
        var wrapper =
                TypeRegistryWrapper.newBuilder().add(List.of(Timestamp.getDescriptor())).build();
        assertThat(wrapper.names()).containsExactly("google.protobuf.Timestamp");
    }

    @Test
    public void aLot() throws IOException {
        var wrapper1 =
                TypeRegistryWrapper.newBuilder()
                        .add(
                                ImmutableList.of(
                                        Any.getDescriptor(),
                                        Timestamp.getDescriptor(),
                                        ID.getDescriptor(),
                                        EntityKind.getDescriptor(),
                                        DescriptorProtos.DescriptorProto.getDescriptor()))
                        .build();
        check(wrapper1);

        var wrapper2 = TypeRegistryWrapper.newBuilder();
        for (var name : wrapper1.names()) {
            var any = pack(wrapper1.find(name).toProto(), "type.googleapis.com/");
            var descriptorProto = any.unpack(DescriptorProtos.DescriptorProto.class);
            wrapper2.add(descriptorProto);
        }
        check(wrapper2.build());
    }

    private void check(TypeRegistryWrapper wrapper) throws IOException {
        var io = new ProtoIO(wrapper.get());
        io.write(EntityKind.getDefaultInstance(), new NullResource(PROTOBUF_JSON_UTF_8));

        var any = pack(EntityKind.getDefaultInstance(), "type.googleapis.com/");
        io.write(any, new NullResource(PROTOBUF_JSON_UTF_8));
    }
}

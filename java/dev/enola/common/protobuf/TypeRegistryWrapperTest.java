/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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

import static com.google.common.truth.Truth.assertThat;
import static com.google.protobuf.Any.pack;

import static dev.enola.common.context.testlib.SingletonRule.$;
import static dev.enola.common.protobuf.ProtobufMediaTypes.PROTOBUF_JSON_UTF_8;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Any;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Timestamp;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.NullResource;
import dev.enola.protobuf.test.TestEnum;
import dev.enola.protobuf.test.TestSimple;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TypeRegistryWrapperTest {

    public @Rule SingletonRule r = $(MediaTypeProviders.set(new ProtobufMediaTypes()));

    @Test
    public void empty() {
        var wrapper = TypeRegistryWrapper.newBuilder().build();
        assertThat(wrapper.fileDescriptorSet().getFileList()).isEmpty();
        assertThat(wrapper.names()).isEmpty();
    }

    @Test
    public void one() {
        var wrapper =
                TypeRegistryWrapper.newBuilder().add(List.of(Timestamp.getDescriptor())).build();
        assertThat(wrapper.fileDescriptorSet().getFileCount()).isEqualTo(1);
        assertThat(wrapper.names()).containsExactly("google.protobuf.Timestamp");
    }

    @Test
    public void aLot() throws IOException, Descriptors.DescriptorValidationException {
        var wrapper1 =
                TypeRegistryWrapper.newBuilder()
                        .add(
                                ImmutableList.of(
                                        Any.getDescriptor(),
                                        TestSimple.getDescriptor(),
                                        Timestamp.getDescriptor(),
                                        DescriptorProtos.DescriptorProto.getDescriptor()))
                        .build();
        check(wrapper1);

        var fileDescriptorProto = wrapper1.fileDescriptorSet();
        var wrapper2 = TypeRegistryWrapper.from(fileDescriptorProto);
        check(wrapper2);
    }

    private void check(TypeRegistryWrapper wrapper) throws IOException {
        checkProtoIO(wrapper);
        checkFindByName(wrapper);
    }

    private void checkFindByName(TypeRegistryWrapper wrapper) {
        assertThat(wrapper.findByName(TestSimple.getDescriptor().getFullName())).isNotNull();

        var anEnumName = TestEnum.getDescriptor().getFullName();
        assertThat(wrapper.findByName(anEnumName)).isNotNull();

        var aNestedEnumName = TestSimple.TestNestedEnum.getDescriptor().getFullName();
        assertThat(wrapper.findByName(aNestedEnumName)).isNotNull();
    }

    private void checkProtoIO(TypeRegistryWrapper wrapper) throws IOException {
        var io = new ProtoIO(wrapper.get());
        io.write(TestSimple.getDefaultInstance(), new NullResource(PROTOBUF_JSON_UTF_8));

        var any1 = pack(TestSimple.getDefaultInstance());
        io.write(any1, new NullResource(PROTOBUF_JSON_UTF_8));

        var any2 = pack(TestSimple.getDefaultInstance(), "type.googleapis.com/");
        io.write(any2, new NullResource(PROTOBUF_JSON_UTF_8));
    }
}

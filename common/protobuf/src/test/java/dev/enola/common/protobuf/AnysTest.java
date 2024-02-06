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

import com.google.common.truth.extensions.proto.ProtoTruth;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;
import com.google.protobuf.TypeRegistry;

import org.junit.Test;

import java.time.Instant;

public class AnysTest {

    private TypeRegistry typeRegistry =
            TypeRegistry.newBuilder().add(Timestamp.getDescriptor()).build();

    private DescriptorProvider descriptorProvider =
            new TypeRegistryDescriptorProvider(typeRegistry);

    private Anys anys = new Anys(descriptorProvider);

    @Test
    public void testTimestamp() throws InvalidProtocolBufferException {
        Timestamp ts = Timestamps2.fromInstant(Instant.now());

        var any = Any.pack(ts);
        var dynamicMessage = anys.toMessage(any);
        ProtoTruth.assertThat(ts).isEqualTo(dynamicMessage);

        Timestamp ts2 = Anys.dynamicToStaticMessage(dynamicMessage, Timestamp.newBuilder());
        ProtoTruth.assertThat(ts2).isEqualTo(ts);
    }
}

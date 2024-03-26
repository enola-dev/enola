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
package dev.enola.core.message;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.ProtocolMessageEnum;

import dev.enola.common.convert.ConversionException;
import dev.enola.common.io.iri.NamespaceConverterIdentity;
import dev.enola.common.io.metadata.MetadataProvider;
import dev.enola.common.protobuf.DescriptorProvider;
import dev.enola.common.protobuf.TypeRegistryWrapper;
import dev.enola.core.meta.proto.FileSystemRepository;
import dev.enola.core.thing.ThingConnectorsProvider;
import dev.enola.thing.ThingMetadataProvider;
import dev.enola.thing.ThingProvider;
import dev.enola.thing.message.ProtoTypes;
import dev.enola.thing.message.ThingProviderAdapter;

import org.junit.Test;

import java.io.IOException;

public class ProtoEnumValueToThingConnectorTest {

    @Test
    // @Ignore // TODO Implement this test and make ProtoEnumValueToThingConnector work..
    public void label() throws IOException, ConversionException {
        // TODO What's a better EnumValue to test that's present in core proto?
        ProtocolMessageEnum enumValue = FileSystemRepository.Format.FORMAT_TEXTPROTO;

        EnumValueDescriptor enumValueDescriptor = enumValue.getValueDescriptor();
        String iri = ProtoTypes.getEnumValueERI(enumValueDescriptor);

        var descriptor = enumValueDescriptor.getType().getContainingType();
        DescriptorProvider dp = TypeRegistryWrapper.newBuilder().add(descriptor).build();
        var thingConnector = new ProtoEnumValueToThingConnector(dp);

        ThingProvider tp =
                new ThingProviderAdapter(
                        new ThingConnectorsProvider(ImmutableList.of(thingConnector)), null);

        MetadataProvider mp = new ThingMetadataProvider(tp, new NamespaceConverterIdentity());
        var label = mp.getLabel(iri);

        assertThat(label).isEqualTo(enumValue.getValueDescriptor().getName());
    }
}

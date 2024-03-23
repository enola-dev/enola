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

        ThingProvider tp = new ThingConnectorsProvider(ImmutableList.of(thingConnector));

        MetadataProvider mp = new ThingMetadataProvider(tp, new NamespaceConverterIdentity());
        var label = mp.getLabel(iri);

        assertThat(label).isEqualTo(enumValue.getValueDescriptor().getName());
    }
}

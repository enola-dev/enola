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
package dev.enola.core.type;

import com.google.protobuf.Any;
import com.google.protobuf.DescriptorProtos.DescriptorProto;

import dev.enola.common.protobuf.DescriptorProvider;
import dev.enola.core.EnolaException;
import dev.enola.core.EnolaService;
import dev.enola.core.meta.proto.Type;
import dev.enola.core.proto.GetThingRequest;
import dev.enola.core.proto.GetThingResponse;
import dev.enola.core.proto.ListEntitiesRequest;
import dev.enola.core.proto.ListEntitiesResponse;
import dev.enola.thing.KIRI;
import dev.enola.thing.ThingExt;
import dev.enola.thing.message.MessageToThingConverter;
import dev.enola.thing.message.MessageWithIRI;
import dev.enola.thing.message.ProtoTypes;
import dev.enola.thing.proto.Things;

public class ProtoService implements EnolaService {
    // TODO This shouldn't be an EnolaService, just a ThingConnector!!

    private final MessageToThingConverter m2t = new MessageToThingConverter();

    public static Type.Builder TYPE() {
        return Type.newBuilder()
                .setEmoji("🕵🏾‍♀️")
                .setName("type.enola.dev/proto")
                .setUri(ProtoTypes.MESSAGE_DESCRIPTOR_ERI_PREFIX + "{FQN}")
                // This is "google.protobuf.DescriptorProto"
                .setProto(DescriptorProto.getDescriptor().getFullName());
        // NOT .addConnectors(Connector.newBuilder().setJavaClass(ProtoConnector.class.getName()));
    }

    private final DescriptorProvider descriptorProvider;

    public ProtoService(DescriptorProvider descriptorProvider) {
        this.descriptorProvider = descriptorProvider;
    }

    // public static class ProtoConnector implements ThingConnector {
    //     @Override
    //     public void augment(Builder thing, Type type) throws EnolaException {
    //         // TODO Auto-generated method stub
    //         throw new UnsupportedOperationException("Unimplemented method 'augment'");
    //     }
    // }

    @Override
    public GetThingResponse getThing(GetThingRequest r) throws EnolaException {
        // TODO It's pretty dumb to duplicate extracting the parameter from ERI again here... :-(
        var fqn = r.getIri().substring(ProtoTypes.MESSAGE_DESCRIPTOR_ERI_PREFIX.length());
        var descriptor = descriptorProvider.findByName(fqn);

        var thing = m2t.convert(new MessageWithIRI(r.getIri(), descriptor.toProto()));
        ThingExt.setString(thing, KIRI.RDFS.LABEL, descriptor.getName());
        var things = Things.newBuilder().addThings(thing).build();

        var response = GetThingResponse.newBuilder();
        response.setThing(Any.pack(things));
        return response.build();
    }

    @Override
    public ListEntitiesResponse listEntities(ListEntitiesRequest r) throws EnolaException {
        throw new UnsupportedOperationException("Won't implement legacy method 'listEntities'");
    }
}

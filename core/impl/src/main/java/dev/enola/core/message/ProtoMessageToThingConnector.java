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

import com.google.protobuf.DescriptorProtos.DescriptorProto;

import dev.enola.common.protobuf.DescriptorProvider;
import dev.enola.core.EnolaException;
import dev.enola.core.meta.proto.Type;
import dev.enola.thing.KIRI;
import dev.enola.thing.message.MessageWithIRI;
import dev.enola.thing.message.ProtoTypes;
import dev.enola.thing.message.ThingExt;
import dev.enola.thing.proto.Things;

import java.util.Map;

public class ProtoMessageToThingConnector extends ProtoToThingConnector {
    // TODO Move to package dev.enola.thing.message ?

    public ProtoMessageToThingConnector(DescriptorProvider descriptorProvider) {
        super(descriptorProvider);
    }

    @Override
    public Type type() {
        return Type.newBuilder()
                .setEmoji("🕵🏾‍♀️")
                .setName("enola.dev/proto/message")
                .setUri(ProtoTypes.MESSAGE_DESCRIPTOR_ERI_PREFIX + "{FQN}")
                // This is "google.protobuf.DescriptorProto"
                .setProto(DescriptorProto.getDescriptor().getFullName())
                .build();
    }

    @Override
    public void augment(Things.Builder things, String iri, Map<String, String> parameters)
            throws EnolaException {
        var fqn = parameters.get("FQN");
        var descriptor = descriptorProvider.findByName(fqn);

        var newThing = m2t.convert(new MessageWithIRI(iri, descriptor.toProto()));
        ThingExt.setString(newThing, KIRI.RDFS.LABEL, descriptor.getName());
        things.addThings(newThing);
    }
}

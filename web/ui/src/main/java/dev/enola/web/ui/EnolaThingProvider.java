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
package dev.enola.web.ui;

import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.ExtensionRegistryLite;

import dev.enola.core.meta.TypeRegistryWrapper;
import dev.enola.core.proto.EnolaServiceGrpc.EnolaServiceBlockingStub;
import dev.enola.core.proto.GetFileDescriptorSetRequest;
import dev.enola.core.proto.GetThingRequest;
import dev.enola.core.view.EnolaMessages;
import dev.enola.thing.ThingProvider;
import dev.enola.thing.message.MessageToThingConverter;
import dev.enola.thing.message.MessageWithIRI;
import dev.enola.thing.proto.Thing;
import dev.enola.thing.proto.Things;

import java.io.IOException;

public class EnolaThingProvider implements ThingProvider {
    // TODO This class doesn't really belong here, but into dev.enola.core.thing?

    private final MessageToThingConverter m2t = new MessageToThingConverter();
    private final EnolaServiceBlockingStub service;
    private final TypeRegistryWrapper typeRegistryWrapper;
    private final EnolaMessages enolaMessages;

    public EnolaThingProvider(EnolaServiceBlockingStub service)
            throws DescriptorValidationException {
        this.service = service;

        var gfdsr = GetFileDescriptorSetRequest.newBuilder().build();
        var fds = service.getFileDescriptorSet(gfdsr).getProtos();
        typeRegistryWrapper = TypeRegistryWrapper.from(fds);
        var extensionRegistry = ExtensionRegistryLite.getEmptyRegistry();
        enolaMessages = new EnolaMessages(typeRegistryWrapper, extensionRegistry);
    }

    @Override
    public Thing getThing(String iri) throws IOException {
        var request = GetThingRequest.newBuilder().setIri(iri).build();
        var response = service.getThing(request);
        var any = response.getThing();

        // TODO This will need some more thought...
        if (any.getTypeUrl().endsWith("Thing")) {
            return any.unpack(Thing.class);
        } else if (any.getTypeUrl().endsWith("Things")) {
            var things = any.unpack(Things.class);
            return things.getThingsList().get(0);
        } else {
            var message = enolaMessages.toMessage(any);
            return m2t.convert(new MessageWithIRI(iri, message)).build();
        }
    }
}

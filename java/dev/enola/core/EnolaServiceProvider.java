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
package dev.enola.core;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.GenericDescriptor;
import com.google.protobuf.ExtensionRegistry;

import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.protobuf.DescriptorProvider;
import dev.enola.common.protobuf.TypeRegistryWrapper;
import dev.enola.common.protobuf.TypeRegistryWrapper.Builder;
import dev.enola.common.protobuf.ValidationException;
import dev.enola.core.message.ProtoEnumValueToThingConnector;
import dev.enola.core.message.ProtoFieldToThingConnector;
import dev.enola.core.message.ProtoMessageToThingConnector;
import dev.enola.core.thing.ThingConnector;
import dev.enola.core.view.EnolaMessages;
import dev.enola.thing.proto.Things;
import dev.enola.thing.repo.EmptyThingsRepository;
import dev.enola.thing.repo.ThingRepository;
import dev.enola.thing.repo.ThingsProvider;

public class EnolaServiceProvider {

    private final TypeRegistryWrapper typeRegistry;
    private final DescriptorProvider descriptorProvider =
            new DescriptorProvider() {

                @Override
                public Descriptor getDescriptorForTypeUrl(String messageTypeURL) {
                    return typeRegistry.getDescriptorForTypeUrl(messageTypeURL);
                }

                @Override
                public GenericDescriptor findByName(String name) {
                    return typeRegistry.findByName(name);
                }
            };

    private final EnolaServiceRegistry enolaService;
    private final EnolaMessages enolaMessages;

    public EnolaServiceProvider(ResourceProvider rp) throws ValidationException, EnolaException {
        this(new EmptyThingsRepository(), new EmptyThingsRepository(), rp);
    }

    public EnolaServiceProvider(
            ThingsProvider thingsProvider, ThingRepository thingRepository, ResourceProvider rp)
            throws ValidationException, EnolaException {
        var esb = EnolaServiceRegistry.builder();
        esb.register(thingRepository, thingsProvider);

        var trb = TypeRegistryWrapper.newBuilder();
        trb.add(Things.getDescriptor());

        // Register a bunch of hard-coded built-in Thing Connectors
        register(new ProtoMessageToThingConnector(descriptorProvider), esb, trb);
        register(new ProtoFieldToThingConnector(descriptorProvider), esb, trb);
        register(new ProtoEnumValueToThingConnector(descriptorProvider), esb, trb);

        this.typeRegistry = trb.build();
        this.enolaService = esb.build(rp);
        this.enolaMessages = new EnolaMessages(typeRegistry, ExtensionRegistry.getEmptyRegistry());
    }

    private void register(
            ThingConnector thingConnector, EnolaServiceRegistry.Builder esb, Builder trb)
            throws EnolaException {
        /* TODO !!!
               var iri = thingConnector.iri();
               var thingsRepository = new ThingsRepositoryAdapter(thingConnector);
               esb.register(thingsRepository);
        */
        trb.add(thingConnector.getDescriptors());
    }

    public EnolaService getEnolaService() {
        return enolaService;
    }

    public TypeRegistryWrapper getTypeRegistryWrapper() {
        return typeRegistry;
    }
}

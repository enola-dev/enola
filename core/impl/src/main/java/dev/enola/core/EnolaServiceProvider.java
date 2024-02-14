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
package dev.enola.core;

import com.google.common.collect.ImmutableList;

import dev.enola.common.protobuf.ValidationException;
import dev.enola.core.aspects.ErrorTestAspect;
import dev.enola.core.aspects.FilestoreRepositoryAspect;
import dev.enola.core.aspects.GrpcAspect;
import dev.enola.core.aspects.TimestampAspect;
import dev.enola.core.aspects.UriTemplateAspect;
import dev.enola.core.aspects.ValidationAspect;
import dev.enola.core.meta.EntityAspectWithRepository;
import dev.enola.core.meta.EntityKindRepository;
import dev.enola.core.meta.SchemaAspect;
import dev.enola.core.meta.TypeRegistryWrapper;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

public class EnolaServiceProvider {

    private final EnolaServiceRegistry enolaService;
    private final TypeRegistryWrapper typeRegistry;

    public EnolaServiceProvider(EntityKindRepository ekr)
            throws ValidationException, EnolaException {
        var esb = EnolaServiceRegistry.builder();
        var trb = TypeRegistryWrapper.newBuilder();
        for (var ek : ekr.list()) {
            var aspectsBuilder = ImmutableList.<EntityAspect>builder();

            // The order in which we add Aspects (AKA Connectors) here matters, a lot!
            // First come the end users custom (and possibly remove...) connectors.
            // Then come our hard-coded fix internal ones - in a particular order.

            for (var c : ek.getConnectorsList()) {
                switch (c.getTypeCase()) {
                    case ERROR:
                        aspectsBuilder.add(new ErrorTestAspect(c.getError()));
                        break;

                    case JAVA_CLASS:
                        var className = c.getJavaClass();
                        try {
                            var clazz = Class.forName(className);
                            var object = clazz.getDeclaredConstructor().newInstance();
                            EntityAspect connector = (EntityAspect) object;
                            if (connector instanceof EntityAspectWithRepository) {
                                ((EntityAspectWithRepository) connector)
                                        .setEntityKindRepository(ekr);
                            }
                            if (connector instanceof SchemaAspect) {
                                ((SchemaAspect) connector).setESP(this);
                            }
                            aspectsBuilder.add(connector);
                            break;

                        } catch (ClassNotFoundException
                                | NoSuchMethodException
                                | InstantiationException
                                | IllegalAccessException
                                | InvocationTargetException e) {
                            // TODO Full ValidationException instead of IllegalArgumentException
                            throw new IllegalArgumentException(
                                    "Java Class Connector failure for EntityKind: " + ek.getId(),
                                    e);
                        }

                        // TODO JAVA_GUICE Registry lookup?

                    case FS:
                        var fs = c.getFs();
                        aspectsBuilder.add(
                                new FilestoreRepositoryAspect(
                                        Path.of(fs.getPath()), fs.getFormat()));
                        break;

                    case GRPC:
                        aspectsBuilder.add(new GrpcAspect(c.getGrpc()));
                        break;

                    case TYPE_NOT_SET:
                        // TODO Full ValidationException instead of IllegalArgumentException
                        throw new IllegalArgumentException(
                                "Connector Type not set in EntityKind: " + ek.getId());
                }
            }

            aspectsBuilder.add(new UriTemplateAspect(ek));
            aspectsBuilder.add(new TimestampAspect());
            aspectsBuilder.add(new ValidationAspect());

            var aspects = aspectsBuilder.build();
            var s = new EntityAspectService(ek, aspects);
            esb.register(ek.getId(), s);

            for (var aspect : aspects) {
                trb.add(aspect.getDescriptors());
            }
        }
        this.typeRegistry = trb.build();
        this.enolaService = esb.build();
    }

    public EnolaService getEnolaService() {
        return enolaService;
    }

    public TypeRegistryWrapper getTypeRegistryWrapper() {
        return typeRegistry;
    }
}

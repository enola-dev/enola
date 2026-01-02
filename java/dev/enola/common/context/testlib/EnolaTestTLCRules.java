/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.context.testlib;

import com.google.common.collect.ImmutableMap;

import dev.enola.common.io.metadata.MetadataProvider;
import dev.enola.common.io.resource.ClasspathResource;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.data.iri.NamespaceConverter;
import dev.enola.data.iri.namespace.repo.NamespaceConverterWithRepository;
import dev.enola.data.iri.namespace.repo.NamespaceRepositoryEnolaDefaults;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.model.Datatypes;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.ProxyTBF;
import dev.enola.thing.java.TBF;
import dev.enola.thing.metadata.ThingMetadataProvider;
import dev.enola.thing.repo.*;

public class EnolaTestTLCRules {
    // TODO Move this to another module (and clean up the BUILD)

    private static final NamespaceConverter namespaceConverter =
            new NamespaceConverterWithRepository(NamespaceRepositoryEnolaDefaults.INSTANCE);

    private static final ThingRepositoryStore store = new ThingMemoryRepositoryRW();

    private static final ThingMetadataProvider thingMetadataProvider =
            new ThingMetadataProvider(store, namespaceConverter);

    /**
     * TestTLCRule with a {@link ClasspathResource.Provider} (which is the (TODO only?) {@link
     * ResourceProvider} that tests should typically use) and a {@link Datatypes#DTR}.
     */
    public static final TestTLCRule BASIC =
            new TestTLCRule(
                    ImmutableMap.of(
                            ResourceProvider.class,
                            new ClasspathResource.Provider(),
                            DatatypeRepository.class,
                            Datatypes.DTR,
                            NamespaceConverter.class,
                            namespaceConverter,
                            ThingRepositoryStore.class,
                            store,
                            ThingRepository.class,
                            store,
                            ThingProvider.class,
                            store,
                            AlwaysThingProvider.class,
                            new AlwaysThingProvider(store),
                            MetadataProvider.class,
                            thingMetadataProvider,
                            ThingMetadataProvider.class,
                            thingMetadataProvider,
                            TBF.class,
                            new ProxyTBF(ImmutableThing.FACTORY)));

    public static final TestTLCRule TBF =
            new TestTLCRule(ImmutableMap.of(TBF.class, new ProxyTBF(ImmutableThing.FACTORY)));
}

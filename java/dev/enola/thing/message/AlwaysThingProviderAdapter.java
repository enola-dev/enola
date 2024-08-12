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
package dev.enola.thing.message;

import dev.enola.data.ProviderFromIRI;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.thing.repo.AlwaysThingProvider;

/**
 * AlwaysThingProviderAdapter is a {@link ThingProviderAdapter} which is an {@link
 * AlwaysThingProvider}.
 */
public class AlwaysThingProviderAdapter extends AlwaysThingProvider {

    public AlwaysThingProviderAdapter(
            ProviderFromIRI<dev.enola.thing.proto.Thing> protoThingProvider,
            DatatypeRepository datatypeRepository) {
        super(new ThingProviderAdapter(protoThingProvider, datatypeRepository));
    }
}

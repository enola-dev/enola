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
package dev.enola.thing.message;

import dev.enola.common.convert.ConversionException;
import dev.enola.data.ProviderFromIRI;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.thing.Thing;
import dev.enola.thing.repo.ThingProvider;

import org.jspecify.annotations.Nullable;

import java.io.UncheckedIOException;

/**
 * ThingProviderAdapter is a {@link ThingProvider} which delegates to any {@link ProviderFromIRI} of
 * proto Thing, and then wraps the results with a {@link ThingAdapter}.
 */
public class ThingProviderAdapter implements ThingProvider {

    private final ProviderFromIRI<dev.enola.thing.proto.Thing> protoThingProvider;
    private final DatatypeRepository datatypeRepository;

    public ThingProviderAdapter(
            ProviderFromIRI<dev.enola.thing.proto.Thing> protoThingProvider,
            DatatypeRepository datatypeRepository) {
        this.protoThingProvider = protoThingProvider;
        this.datatypeRepository = datatypeRepository;
    }

    @Override
    public @Nullable Thing get(String iri) throws UncheckedIOException, ConversionException {
        var protoThing = protoThingProvider.get(iri);
        if (protoThing != null) return new ThingAdapter(protoThing, datatypeRepository);
        else return null;
    }
}

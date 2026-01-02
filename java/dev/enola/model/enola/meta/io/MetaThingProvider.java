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
package dev.enola.model.enola.meta.io;

import dev.enola.common.convert.ConversionException;
import dev.enola.thing.Thing;
import dev.enola.thing.repo.ThingRepository;

import org.jspecify.annotations.Nullable;

import java.io.UncheckedIOException;

public class MetaThingProvider implements ThingRepository {

    // TODO Implement this with the IRI Template matcher thingie... meta-specific, or generic?

    private final MetaThingByIdProvider metaThingByIdProvider;

    public MetaThingProvider(MetaThingByIdProvider metaThingByIdProvider) {
        this.metaThingByIdProvider = metaThingByIdProvider;
    }

    @Override
    public @Nullable Thing get(String iri) throws UncheckedIOException, ConversionException {
        return null;
    }

    @Override
    public Iterable<String> listIRI() {
        return null;
    }
}

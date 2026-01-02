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
package dev.enola.thing.repo;

import dev.enola.common.context.TLC;
import dev.enola.common.convert.ConversionException;
import dev.enola.thing.Thing;

import org.jspecify.annotations.Nullable;

import java.io.UncheckedIOException;

public class StackedThingProvider implements ThingProvider {

    private final ThingProvider delegate;
    private final @Nullable ThingProvider parent;

    public StackedThingProvider(ThingProvider delegate, ThingProvider parent) {
        this.delegate = delegate;
        this.parent = parent;
    }

    public StackedThingProvider(ThingProvider delegate) {
        this.delegate = delegate;
        this.parent = TLC.optional(ThingProvider.class).orElse(null);
    }

    public StackedThingProvider(Iterable<Thing> things) {
        this(new ThingMemoryRepositoryROBuilder().storeAll(things).build());
    }

    @Override
    public @Nullable Thing get(String iri) throws UncheckedIOException, ConversionException {
        var thing = delegate.get(iri);
        if (thing == null && parent != null) thing = parent.get(iri);
        return thing;
    }

    @Override
    public String toString() {
        return "StackedThingProvider{" + "delegate=" + delegate + ", parent=" + parent + '}';
    }
}

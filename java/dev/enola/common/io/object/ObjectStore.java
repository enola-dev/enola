/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.object;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import dev.enola.common.name.NamedObjectProviders;
import dev.enola.common.name.NamedObjectStore;

import java.util.Optional;

/**
 * ObjectStore is an in-memory store (and {@link ProviderFromID}) for {@link Identifiable} objects.
 *
 * <p>It supports distinct objects with the same ID but different classes, effectively "scoping"
 * them by their class.
 */
public class ObjectStore implements ProviderFromID {

    // TODO ObjectStore newImmutable(Map<String, Identifiable> map)

    public static ObjectStore newConcurrent() {
        return new ObjectStore(NamedObjectProviders.newConcurrent());
    }

    public static ObjectStore newSingleThreaded() {
        return new ObjectStore(NamedObjectProviders.newSingleThreaded());
    }

    private final NamedObjectStore namedObjectStore;

    public ObjectStore(NamedObjectStore namedObjectStore) {
        this.namedObjectStore = namedObjectStore;
    }

    @CanIgnoreReturnValue
    public ObjectStore store(Identifiable o) throws IllegalStateException {
        namedObjectStore.store(o.id(), o);
        return this;
    }

    @Override
    public <T extends Identifiable> Optional<T> opt(String id, Class<T> clazz) {
        return namedObjectStore.opt(id, clazz);
    }
}

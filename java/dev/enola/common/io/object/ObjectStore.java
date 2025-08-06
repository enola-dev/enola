/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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

import static java.util.Objects.requireNonNull;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ObjectStore is a concurrency-safe in-memory store (and {@link ProviderFromID}) for {@link
 * Identifiable} objects.
 *
 * <p>It supports distinct objects with the same ID but different classes, effectively "scoping"
 * them by their class.
 */
public class ObjectStore implements ProviderFromID {

    // TODO Offer a more efficient non concurrency-safe single thread variant!

    // Objects, keyed first by Class, then by ID.
    private final Map<Class<?>, Map<String, Identifiable>> store;

    public ObjectStore() {
        this.store = new ConcurrentHashMap<>();
    }

    /**
     * Stores an Identifiable object in the map, scoped by its class.
     *
     * @param o The Identifiable object to store.
     * @return this itself, just as a convenience for one line chaining
     * @throws IllegalStateException If an object with the same ID and class already exists.
     */
    @CanIgnoreReturnValue
    public ObjectStore store(Identifiable o) throws IllegalStateException {
        requireNonNull(o, "Object to store cannot be null.");
        requireNonNull(o.id(), "Object ID cannot be null.");

        // Get or create the inner map for this object's class
        Map<String, Identifiable> classSpecificStore =
                store.computeIfAbsent(o.getClass(), k -> new ConcurrentHashMap<>());

        var current = classSpecificStore.putIfAbsent(o.id(), o);
        if (current != null) throw new IllegalStateException("Already stores: " + current);

        return this;
    }

    /**
     * Retrieves an Identifiable object by its ID and expected class type. The lookup is scoped by
     * the provided class.
     *
     * @param id The ID of the object to retrieve.
     * @param clazz The expected class type of the object. This is crucial for type safety and
     *     scoping.
     * @param <T> The type of the Identifiable object.
     * @return The retrieved object, cast to the specified class, or null if not found or if the
     *     stored object is not assignable to the requested class.
     */
    @Override
    public <T extends Identifiable> Optional<T> opt(String id, Class<T> clazz) {
        // Get the inner map for the specific class
        Map<String, Identifiable> classSpecificStore = store.get(clazz);
        if (classSpecificStore == null) {
            // No objects of this class type have been stored yet at all
            return Optional.empty();
        }

        // No need for isInstance check here, as it's already scoped by class
        return Optional.ofNullable(clazz.cast(classSpecificStore.get(id)));
    }
}

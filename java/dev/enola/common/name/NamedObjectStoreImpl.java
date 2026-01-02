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
package dev.enola.common.name;

import static com.google.common.base.Strings.emptyToNull;

import static java.util.Objects.requireNonNull;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

class NamedObjectStoreImpl implements NamedObjectStore {

    // Objects, keyed first by Class, then by ID.
    private final Map<Class<?>, Map<String, Object>> store;

    NamedObjectStoreImpl(Map<Class<?>, Map<String, Object>> store) {
        this.store = store;
    }

    /**
     * Stores an object in the map, scoped by its class.
     *
     * @param o The object to store.
     * @return this itself, just as a convenience for one line chaining
     * @throws IllegalStateException If an object with the same ID and class already exists.
     */
    @Override
    @CanIgnoreReturnValue
    public NamedObjectStore store(String name, Object o) throws IllegalStateException {
        requireNonNull(o, "Object to store cannot be null.");
        requireNonNull(emptyToNull(name), "Object ID cannot be null or empty");

        // Get or create the inner map for this object's class
        Map<String, Object> classSpecificStore =
                store.computeIfAbsent(o.getClass(), k -> new ConcurrentHashMap<>());

        var current = classSpecificStore.putIfAbsent(name, o);
        if (current != null) throw new IllegalStateException("Already stores: " + current);

        return this;
    }

    /**
     * Retrieves an object by its ID and expected class type. The lookup is scoped by the provided
     * class.
     *
     * @param name The ID of the object to retrieve.
     * @param clazz The expected class type of the object. This is crucial for type safety and
     *     scoping.
     * @param <T> The type of the object.
     * @return The retrieved object, cast to the specified class, or null if not found or if the
     *     stored object is not assignable to the requested class.
     */
    @Override
    public <T> Optional<T> opt(String name, Class<T> clazz) {
        // Find all matching objects from any compatible class type.
        var matches =
                store.entrySet().stream()
                        // Filter for entries where the stored class is a subtype of the requested
                        // class.
                        .filter(entry -> clazz.isAssignableFrom(entry.getKey()))
                        // Get the object from the inner map using the name.
                        .map(entry -> entry.getValue().get(name))
                        // Filter out nulls in case the name doesn't exist in a compatible map.
                        .filter(Objects::nonNull)
                        .toList();

        if (matches.size() > 1) {
            // TODO Also list the classes of the matches in this exception message
            throw new IllegalArgumentException(
                    "Found "
                            + matches.size()
                            + " objects for name '"
                            + name
                            + "' matching type "
                            + clazz.getName());
        }

        if (!matches.isEmpty()) {
            return Optional.of(clazz.cast(matches.get(0)));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Iterable<String> names(Class<?> clazz) {
        Map<String, Object> classSpecificStore = store.get(clazz);
        if (classSpecificStore == null) {
            return List.of();
        }
        return classSpecificStore.keySet();
    }
}

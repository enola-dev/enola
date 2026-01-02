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
package dev.enola.thing.impl;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import dev.enola.thing.PredicatesObjects;
import dev.enola.thing.Thing;

import java.util.List;
import java.util.Map;
import java.util.Set;

// Package-local (non-public) helpers
final class ImmutableObjects {

    record Pair(ImmutableMap<String, Object> properties, ImmutableMap<String, String> datatypes) {}

    static Pair build(Map<String, Object> properties, Map<String, String> datatypes) {
        var propertiesBuilder =
                ImmutableMap.<String, Object>builderWithExpectedSize(properties.size());
        var datatypesBuilder =
                ImmutableMap.<String, String>builderWithExpectedSize(datatypes.size());

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            var predicateIRI = entry.getKey();
            var object = entry.getValue();
            if (object instanceof ImmutableCollection.Builder<?> immutableCollectionBuilder)
                object = immutableCollectionBuilder.build();
            if (object instanceof List<?> list) object = ImmutableList.copyOf(list);
            if (object instanceof Set<?> list) object = ImmutableSet.copyOf(list);
            if (object instanceof MutablePredicatesObjects<?> mutablePredicatesObjects)
                object = mutablePredicatesObjects.build();
            // Keep these ^^^ conversions in sync with:
            ImmutableObjects.check(object);

            propertiesBuilder.put(predicateIRI, object);

            var datatype = datatypes.get(predicateIRI);
            if (datatype != null) datatypesBuilder.put(predicateIRI, datatype);
        }

        return new Pair(propertiesBuilder.build(), datatypesBuilder.build());
    }

    static void check(Object object) {
        // TODO if (object == null) throw new IllegalStateException("null is not allowed here");
        if ((object instanceof Iterable<?>) && !(object instanceof ImmutableCollection<?>))
            throw new IllegalStateException("Non-ImmutableCollection: " + object);
        if (object instanceof Thing)
            throw new IllegalStateException("Things cannot contain Things: " + object);
        if (object instanceof Thing.Builder)
            throw new IllegalStateException("Things cannot contain Thing.Builder: " + object);
        if (object instanceof PredicatesObjects.Builder)
            throw new IllegalStateException(
                    "Immutable Things cannot contain PredicatesObjects.Builder: " + object);
        if (object instanceof PredicatesObjects && !(object instanceof IImmutablePredicatesObjects))
            throw new IllegalStateException(
                    "Immutable Things cannot contain Non-IImmutablePredicatesObjects: " + object);
    }

    private ImmutableObjects() {}
}

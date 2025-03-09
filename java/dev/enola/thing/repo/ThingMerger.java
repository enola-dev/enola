/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import dev.enola.thing.KIRI;
import dev.enola.thing.Thing;
import dev.enola.thing.ThingConverterInto;
import dev.enola.thing.impl.MutableThing;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

class ThingMerger {

    // TODO This currently only works for the 1st / top-level,
    //   but later ideally really needs to recurse into all contained PredicatesObjects...

    // TODO Support Java Things, like ProxyTBF (or HasSomethingTBF); it's currently "lost".

    public static Thing merge(Thing existing, Thing update) {
        if (!existing.iri().equals(update.iri())) throw new IllegalArgumentException();

        if (existing.predicateIRIs().isEmpty()) return update;
        if (update.predicateIRIs().isEmpty()) return existing;

        // TODO merged = existing.copy(); !!
        var merged = new MutableThing();
        new ThingConverterInto().convertInto(existing, merged);

        var properties = update.properties();
        properties.forEach(
                (predicate, value) -> {
                    var old = existing.get(predicate);
                    if (old == null) merged.set(predicate, value, update.datatype(predicate));
                    else if (old.equals(value)) {
                        // That's fine!
                    } else if (Objects.equals(
                            existing.datatype(predicate), update.datatype(predicate))) {
                        var newCollection =
                                existing.isOrdered(predicate) || update.isOrdered(predicate)
                                        ? ImmutableList.builder()
                                        : ImmutableSet.builder();
                        newCollection.addAll(collectivize(existing.get(predicate)));
                        newCollection.addAll(collectivize(update.get(predicate)));
                        merged.set(predicate, newCollection.build(), update.datatype(predicate));

                    } else
                        throw new IllegalStateException(
                                "Cannot merge "
                                        + predicate
                                        + " of an "
                                        + existing.iri()
                                        + " from "
                                        + existing.getString(KIRI.E.ORIGIN)
                                        + " and "
                                        + update.getString(KIRI.E.ORIGIN));
                });
        return merged.build();
    }

    private static Iterable<?> collectivize(@Nullable Object o) {
        if (o == null) return ImmutableList.of();
        if (o instanceof Iterable<?> iterable) return iterable;
        return ImmutableSet.of(o);
    }
}

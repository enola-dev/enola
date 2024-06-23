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
package dev.enola.thing.repo;

import dev.enola.thing.KIRI;
import dev.enola.thing.Thing;

class ThingMerger {
    // TODO Implement missing ThingMergerTest coverage!

    public static Thing merge(Thing existing, Thing update) {
        if (!existing.iri().equals(update.iri())) throw new IllegalArgumentException();
        var merged = existing.copy();
        var properties = update.properties();
        properties.forEach(
                (predicate, value) -> {
                    var old = existing.get(predicate);
                    if (old == null) merged.set(predicate, value, update.datatype(predicate));
                    else if (old.equals(value)) {
                        // That's fine!
                    } else if (predicate.equals(KIRI.E.ORIGIN)) {
                        // TODO Implement merging both into a List, with test coverage!
                    } else
                        throw new IllegalStateException(
                                "Cannot merge " + predicate + " of two " + existing.iri());
                });
        return merged.build();
    }
}

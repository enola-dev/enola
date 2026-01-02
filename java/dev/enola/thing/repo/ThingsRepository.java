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

import dev.enola.thing.KIRI;
import dev.enola.thing.Thing;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

public interface ThingsRepository extends ThingRepository, ThingsProvider {

    // TODO See note in ThingsProvider, and also get rid of replace this with ThingRepository

    @Override
    default Iterable<Thing> list() {
        return getThings(KIRI.E.LIST_THINGS).toList();
    }

    @Override
    default Stream<Thing> stream() {
        return getThings(KIRI.E.LIST_THINGS);
    }

    @Override
    default Iterable<String> listIRI() {
        return getThings(KIRI.E.LIST_IRIS).map(Thing::iri).toList();
    }

    @Override
    default Thing get(String iri) {
        var stream = getThings(iri);
        var iterator = stream.iterator();
        if (!iterator.hasNext())
            throw new NoSuchElementException(
                    "No Thing; caller should directly invoke getThings() instead; iri=" + iri);
        var first = iterator.next();
        if (iterator.hasNext())
            throw new NoSuchElementException(
                    "More than 1 Thing; caller should directly invoke getThings() instead; iri="
                            + iri);
        return first;
    }
}

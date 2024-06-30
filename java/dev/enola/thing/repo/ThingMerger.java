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

import static dev.enola.common.collect.Immutables.join;

import dev.enola.thing.KIRI;
import dev.enola.thing.Thing;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.impl.MutableThing;

import java.util.List;

class ThingMerger {
    // TODO Implement missing ThingMergerTest coverage!

    public static Thing merge(Thing t1, Thing t2) {
        if (!t1.iri().equals(t2.iri())) throw new IllegalArgumentException();

        var t1predis = t1.predicateIRIs();
        var t2predis = t2.predicateIRIs();
        if (t1predis.isEmpty()) return t2;
        if (t2predis.isEmpty()) return t1;

        // TODO Use copy() instead of new MutableThing + ImmutableThing.copyOf()

        var merged = new MutableThing<>(t2predis.size());
        t2.properties()
                .forEach(
                        (predicate, t2obj) -> {
                            var t1obj = t1.get(predicate);
                            var t1dt = t1.datatype(predicate);
                            var t2dt = t2.datatype(predicate);
                            var sameDt = t1dt != null && t1dt.equals(t2dt);

                            if (t1obj == null) merged.set(predicate, t2obj, t2dt);
                            else if (t1obj.equals(t2obj) && sameDt)
                                merged.set(predicate, t2obj, t2dt);
                            // skipcq: JAVA-C1003
                            else if (t1obj instanceof List t1list && t2obj instanceof List t2list) {
                                merged.set(predicate, join(t1list, t2list));
                            } else
                                throw new IllegalStateException(
                                        "Cannot merge "
                                                + predicate
                                                + " of an "
                                                + t1.iri()
                                                + " from "
                                                + t1.getString(KIRI.E.ORIGIN)
                                                + " and "
                                                + t2.getString(KIRI.E.ORIGIN));
                        });

        try {
            return ImmutableThing.copyOf(merged.build());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Cannot merge " + t1.iri(), e);
        }
    }
}

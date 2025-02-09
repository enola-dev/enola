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

import dev.enola.thing.KIRI;
import dev.enola.thing.Thing;
import dev.enola.thing.impl.OnlyIRIThing;
import dev.enola.thing.java.TBF;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Set of {@link Thing.Builder}s.
 *
 * <p>This is intended to be used "short-lived" and used only during incremental Things creation.
 *
 * <p>For memory efficiency, do NOT "keep this around".
 */
// @NotThreadSafe
public class TypedThingsBuilder<T extends Thing, B extends Thing.Builder>
        implements ThingsRepository { // TODO Move ^^^ type signatures to getBuilder

    // TODO Merge TypedThingsBuilder & ThingsBuilder!

    private final Map<String, B> map;
    private final TBF tbf;

    public TypedThingsBuilder(TBF tbf) {
        this.tbf = tbf;
        this.map = new HashMap<>();
    }

    protected TypedThingsBuilder(TypedThingsBuilder<T, B> into) {
        tbf = into.tbf;
        map = into.map;
    }

    @SuppressWarnings("unchecked")
    public B getBuilder(String iri, Class<B> builderClass, Class<T> thingClass) {
        return map.computeIfAbsent(
                iri,
                _iri -> {
                    var builder = tbf.create(builderClass, thingClass);
                    builder.iri(_iri);
                    return builder;
                });
    }

    public Iterable<B> builders() {
        return map.values();
    }

    @Override
    public Stream<Thing> getThings(String iri) {
        return switch (iri) {
            case KIRI.E.LIST_THINGS -> map.values().stream().map(Thing.Builder::build);
            case KIRI.E.LIST_IRIS -> map.keySet().stream().map(OnlyIRIThing::new);
            default -> { // NOT Stream.of(getBuilder(iri).build());
                var thing = map.get(iri);
                if (thing != null) yield Stream.of(thing.build());
                // NOT else yield Stream.empty();
                else throw new IllegalStateException();
            }
        };
    }

    @Override
    public String toString() {
        return "ThingsBuilder{" + map + '}';
    }
}

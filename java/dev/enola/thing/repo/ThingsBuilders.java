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
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.impl.OnlyIRIThing;
import dev.enola.thing.java.TBF;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Set of {@link Thing.Builder}s, which is also a {@link ThingsRepository}.
 *
 * <p>This is intended to be used "short-lived" and used only during incremental Things creation.
 *
 * <p>For memory efficiency, do NOT "keep this around".
 */
// TODO Update JavaDoc about how his would typically be used in a (TBD) "Transaction"...
// @NotThreadSafe
public class ThingsBuilders implements ThingsRepository {

    private final Map<String, Thing.Builder<Thing>> map;
    private final TBF tbf;

    public ThingsBuilders(TBF tbf) {
        this.tbf = tbf;
        this.map = new HashMap<>();
    }

    @Deprecated // TODO Remove ThingsBuilders() by replacing with ThingsBuilders(TBF tbf)
    public ThingsBuilders() {
        this(ImmutableThing.FACTORY);
    }

    @SuppressWarnings("unchecked")
    public <T extends Thing, B extends Thing.Builder<T>> B getBuilder(
            String thingIRI, Class<B> builderClass, Class<T> thingClass) {
        return (B)
                map.computeIfAbsent(
                        thingIRI,
                        _thingIRI -> {
                            var builder = tbf.create(builderClass, thingClass);
                            builder.iri(_thingIRI);
                            return (Thing.Builder<Thing>) builder;
                        });
    }

    public Thing.Builder<?> getBuilder(String thingIRI, String typeIRI) {
        return map.computeIfAbsent(
                thingIRI,
                _thingIRI -> {
                    var builder = tbf.create(typeIRI);
                    builder.iri(_thingIRI);
                    return builder;
                });
    }

    @SuppressWarnings("unchecked")
    public Thing.Builder<?> getBuilder(String iri) {
        return getBuilder(iri, Thing.Builder.class, Thing.class);
    }

    public Iterable<Thing.Builder<Thing>> builders() {
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

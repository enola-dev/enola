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
package dev.enola.web;

import com.google.common.collect.Iterables;

import dev.enola.data.Repository;
import dev.enola.thing.Thing;
import dev.enola.thing.message.ThingAdapter;
import dev.enola.thing.repo.ThingRepository;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

public class ProtoToThingRepository implements ThingRepository {

    // TODO Like EnolaThingProvider, move somewhere else

    private final Repository<dev.enola.thing.proto.Thing> protoThingRepository;

    public ProtoToThingRepository(Repository<dev.enola.thing.proto.Thing> protoThingRepository) {
        this.protoThingRepository = protoThingRepository;
    }

    @Override
    public @Nullable Thing get(String iri) {
        var protoThing = protoThingRepository.get(iri);
        if (protoThing == null) return null;
        return new ThingAdapter(protoThing);
    }

    @Override
    public Iterable<String> listIRI() {
        return protoThingRepository.listIRI();
    }

    @Override
    public Iterable<Thing> list() {
        var protoThings = protoThingRepository.list();
        var list = new ArrayList<Thing>(Iterables.size(protoThings));
        for (var protoThing : protoThings) list.add(new ThingAdapter(protoThing));
        return list;
    }
}

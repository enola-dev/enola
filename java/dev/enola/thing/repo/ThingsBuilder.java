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

import dev.enola.thing.Thing;
import dev.enola.thing.impl.ImmutableThing;

import java.util.HashMap;
import java.util.Map;

public class ThingsBuilder {

    private final Map<String, Thing.Builder<?>> map = new HashMap<>();

    public Thing.Builder<?> get(String iri) {
        return map.computeIfAbsent(iri, _iri -> ImmutableThing.builder().iri(_iri));
    }

    public Iterable<Thing.Builder<?>> builders() {
        return map.values();
    }
}

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

import dev.enola.thing.Thing;
import dev.enola.thing.impl.ImmutableThing;

/** Untyped variant of {@link TypedThingsBuilder}. */
public class ThingsBuilder extends TypedThingsBuilder {

    // TODO Merge TypedThingsBuilder & ThingsBuilder, which (now) are exactly the same?!

    public ThingsBuilder(TypedThingsBuilder into) {
        super(into);
    }

    public ThingsBuilder() {
        super(ImmutableThing.FACTORY);
    }

    @SuppressWarnings("unchecked")
    public Thing.Builder<Thing> getBuilder(String iri) {
        return getBuilder(iri, Thing.Builder.class, Thing.class);
    }
}

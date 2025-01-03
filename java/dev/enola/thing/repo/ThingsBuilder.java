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

import com.google.common.reflect.TypeToken;

import dev.enola.thing.Thing;
import dev.enola.thing.impl.ImmutableThing;

/** Untyped variant of {@link TypedThingsBuilder}. */
public class ThingsBuilder extends TypedThingsBuilder<Thing, Thing.Builder<Thing>> {

    // TODO Merge TypedThingsBuilder & ThingsBuilder!

    public ThingsBuilder(TypedThingsBuilder<Thing, Thing.Builder<Thing>> into) {
        super(into);
    }

    public ThingsBuilder() {
        super(ImmutableThing.FACTORY);
    }

    @SuppressWarnings("unchecked")
    public Thing.Builder<Thing> getBuilder(String iri) {
        // TODO Is this TypeToken required?!
        TypeToken<Thing.Builder<Thing>> genericClass = new TypeToken<>(Thing.Builder.class) {};
        Class<? super Thing.Builder<Thing>> klass = genericClass.getRawType();
        return getBuilder(iri, (Class<Thing.Builder<Thing>>) klass, Thing.class);
    }
}

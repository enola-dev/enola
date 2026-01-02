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
package dev.enola.thing;

import dev.enola.common.convert.ConverterInto;
import dev.enola.thing.Thing.Builder;

/**
 * ThingConverterInto converts a {@link Thing} into an {@link Thing.Builder}.
 *
 * <p>This is useful e.g. to convert from one kind of Thing API implementation to another.
 */
public class ThingConverterInto implements ConverterInto<Thing, Thing.Builder> {

    @Override
    @SuppressWarnings("Immutable") // TODO Remove when switching to (TBD) PredicatesObjects.Visitor
    public boolean convertInto(Thing from, Builder into) {
        into.iri(from.iri());
        for (var predicateIRI : from.predicateIRIs()) {
            var value = from.get(predicateIRI);
            var datatype = from.datatype(predicateIRI);
            // TODO if (value instanceof PredicatesObjects & Thing) into.builderSupplier().get() ...
            into.set(predicateIRI, value, datatype);
        }
        return true;
    }
}

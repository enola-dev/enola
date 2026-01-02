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
package dev.enola.model.enola.meta;

import dev.enola.thing.KIRI;
import dev.enola.thing.impl.ImmutableThing;
import dev.enola.thing.java.ProxyTBF;
import dev.enola.thing.java.TBF;

public interface Property extends Type {

    String CLASS_IRI = "https://enola.dev/meta/Property";

    default Property parent() {
        return getThing(KIRI.E.PARENT, Property.class).get();
    }

    default Datatype datatype() {
        return getThing(KIRI.E.META.DATATYPE, Datatype.class).get();
    }

    /*
        default Multiplicity multiplicity() {
            return Multiplicity.valueOf(getString(KIRI.E.META.MULTIPLICITY));
        }

        enum Multiplicity {
            Single,

            / ** Multiple, unordered * /
            Set,

            // TODO List, for Multiple, ordered?
        }
    */
    interface Builder<B extends Property> extends Property, Type.Builder<B> { // skipcq: JAVA-E0169

        @Override
        default Property.Builder<B> schema(Schema schema) {
            Type.Builder.super.schema(schema);
            return this;
        }

        @Override
        default Property.Builder<B> name(String name) {
            Type.Builder.super.name(name);
            return this;
        }

        default Property.Builder<B> parent(Property parent) {
            set(KIRI.E.PARENT, parent);
            return this;
        }

        default Property.Builder<B> datatype(Datatype datatype) {
            set(KIRI.E.META.DATATYPE, datatype);
            return this;
        }

        // TODO Property.Builder<B> multiplicity(Multiplicity multiplicity);
    }

    @SuppressWarnings("unchecked")
    static Property.Builder<Property> builder(TBF tbf) {
        return tbf.create(Property.Builder.class, Property.class);
    }

    @SuppressWarnings("unchecked")
    static Property.Builder<Property> builder() {
        return builder(new ProxyTBF(ImmutableThing.FACTORY));
    }
}

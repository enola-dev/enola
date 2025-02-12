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
package dev.enola.model.enola.meta;

public interface Property extends Type {

    String CLASS_IRI = "https://enola.dev/meta/Property";

    // TODO default KIRI.E.META.PARENT
    Property parent();

    // TODO default KIRI.E.META.DATATYPE
    Datatype datatype();

    // TODO default KIRI.E.META.MULTIPLICITY
    Multiplicity multiplicity();

    enum Multiplicity {
        Single,

        /** Multiple, unordered */
        Set,

        // TODO List, for Multiple, ordered?
    }

    interface Builder<B extends Property> extends Property, Type.Builder<B> { // skipcq: JAVA-E0169

        @Override
        Property.Builder<B> schema(Schema schema);

        @Override
        Property.Builder<B> name(String name);

        Property.Builder<B> parent(Property parent);

        Property.Builder<B> datatype(Datatype datatype);

        Property.Builder<B> multiplicity(Multiplicity multiplicity);
    }
}

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
package dev.enola.model.enola.meta.bootstrap;

import dev.enola.common.context.TLC;
import dev.enola.model.enola.meta.Datatype;
import dev.enola.model.enola.meta.Property;
import dev.enola.model.enola.meta.Schema;
import dev.enola.model.w3.rdfs.Class;
import dev.enola.thing.repo.ThingProvider;

// NB: This hand-written class may eventually get replaced by a code-generated one!
public class MutableProperty extends MutableType implements Property, Property.Builder {

    // TODO Use String IDs instead of Datatype & Property, and use TLC/TP get() in accessors?!
    private Property parent;
    private Datatype datatype;
    private Multiplicity multiplicity;

    @Override
    public Class type() {
        return TLC.get(ThingProvider.class).get(Property.CLASS_IRI, Class.class);
    }

    @Override
    public Property parent() {
        return parent;
    }

    @Override
    public Property.Builder parent(Property parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public Datatype datatype() {
        return datatype;
    }

    @Override
    public Property.Builder datatype(Datatype datatype) {
        this.datatype = datatype;
        return this;
    }

    @Override
    public Multiplicity multiplicity() {
        return multiplicity;
    }

    @Override
    public Property.Builder multiplicity(Multiplicity multiplicity) {
        this.multiplicity = multiplicity;
        return this;
    }

    @Override
    public Property.Builder schema(Schema schema) {
        super.schema(schema);
        return this;
    }

    @Override
    public Property.Builder name(String name) {
        super.name(name);
        return this;
    }

    @Override
    public Property build() {
        return this;
    }
}

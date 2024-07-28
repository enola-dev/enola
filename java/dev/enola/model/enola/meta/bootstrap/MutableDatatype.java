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
package dev.enola.model.enola.meta.bootstrap;

import dev.enola.common.context.TLC;
import dev.enola.model.enola.meta.Class;
import dev.enola.model.enola.meta.Datatype;
import dev.enola.model.enola.meta.Schema;
import dev.enola.thing.repo.ThingProvider;

import java.net.URI;

// NB: This hand-written class may eventually get replaced by a code-generated one!
public class MutableDatatype extends MutableType implements Datatype, Datatype.Builder {

    private URI xsd;
    private String java;
    private String proto;

    // TODO Use String IDs instead of Datatype, and use TLC/TP get() in accessors?!
    private Datatype parent;

    @Override
    public Class type() {
        return TLC.get(ThingProvider.class).get(Datatype.CLASS_IRI, Class.class);
    }

    @Override
    public Datatype parent() {
        return parent;
    }

    @Override
    public Datatype.Builder parent(Datatype datatype) {
        this.parent = datatype;
        return this;
    }

    @Override
    public String java() {
        return java;
    }

    @Override
    public Datatype.Builder java(String java) {
        this.java = java;
        return this;
    }

    @Override
    public String proto() {
        return proto;
    }

    @Override
    public Datatype.Builder proto(String proto) {
        this.proto = proto;
        return this;
    }

    @Override
    public URI xsd() {
        return xsd;
    }

    @Override
    public Datatype.Builder xsd(URI xsd) {
        this.xsd = xsd;
        return this;
    }

    @Override
    public Datatype.Builder schema(Schema schema) {
        super.schema(schema);
        return this;
    }

    @Override
    public Datatype.Builder name(String name) {
        super.name(name);
        return this;
    }

    @Override
    public Datatype build() {
        return this;
    }
}

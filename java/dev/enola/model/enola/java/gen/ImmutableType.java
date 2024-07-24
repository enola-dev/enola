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
package dev.enola.model.enola.java.gen;

import com.google.common.collect.ImmutableMap;

import dev.enola.model.enola.java.Package;
import dev.enola.model.enola.java.Type;
import dev.enola.thing.KIRI;
import dev.enola.thing.impl.ImmutableThing;

public class ImmutableType extends ImmutableThing implements Type {

    // TODO This, and similar, classes should (eventually) be automagically code generated...

    @Override
    public Package pkg() {
        return get("https://enola.dev/java/package", Package.class);
    }

    @Override
    public String name() {
        return get("https://schema.org/name", String.class);
    }

    @Override
    public Kind kind() {
        return get("https://enola.dev/java/type-kind", Kind.class);
    }

    @Override
    public Iterable<Type> uses() {
        return get("https://enola.dev/code/uses", Iterable.class);
        // TODO new TypeToken<Iterable<Type>>() {};
    }

    @Override
    public Iterable<Type> parents() {
        return get("https://enola.dev/code/oop/parents", Iterable.class);
        // TODO new TypeToken<Iterable<Type>>() {};
    }

    protected ImmutableType(
            String iri,
            ImmutableMap<String, Object> properties,
            ImmutableMap<String, String> datatypes) {
        super(iri, properties, datatypes);
    }

    // skipcq: JAVA-E0169
    public static class Builder<B extends ImmutableType> extends ImmutableThing.Builder<B>
            implements Type.Builder<B> {

        Builder() {
            super();
            set(KIRI.RDF.TYPE, "https://enola.dev/java/type");
        }

        @Override
        public Type.Builder<B> pkg(Package pkg) {
            set("https://enola.dev/java/package", pkg);
            return this;
        }

        @Override
        public Type.Builder<B> label(String name) {
            set(KIRI.RDFS.LABEL, name);
            return this;
        }

        @Override
        public Type.Builder<B> kind(Kind kind) {
            set("https://enola.dev/java/type-kind", kind);
            return this;
        }

        @Override
        public Type.Builder<B> addUses(Type uses) {
            // TODO add() to ImmutableList.Builder ... created in constructor?
            return this;
        }

        @Override
        public Type.Builder<B> addParents(Type parents) {
            // TODO add() to ImmutableList.Builder ... created in constructor?
            return this;
        }
    }
}

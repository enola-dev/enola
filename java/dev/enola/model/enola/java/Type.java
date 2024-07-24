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
package dev.enola.model.enola.java;

import dev.enola.model.enola.HasName;
import dev.enola.thing.Thing;
import dev.enola.thing.java.IRI;
import dev.enola.thing.java.JThing;

/**
 * ☕ <a href="https://docs.enola.dev/models/enola.dev/java/type/">Java Type</a>.
 *
 * <p>Type (Class, Interface, Enum, Record, Primitives, Array, Void) in the Java Virtual Machine
 * (JVM).
 */
@JThing("https://enola.dev/java/type")
// ? @IRI("https://enola.dev/java/type/{FQN}")
public interface Type
        extends /*Typed,*/ HasName { // NOT dev.enola.model.w3.rdfs.Class; these are the instances

    @IRI("https://enola.dev/java/package")
    Package pkg();

    @IRI("https://enola.dev/java/type-kind")
    Kind kind();

    @IRI("https://enola.dev/code/uses")
    Iterable<Type> uses();

    @IRI("https://enola.dev/code/oop/parents") // parents extends #uses
    Iterable<Type> parents();

    // TODO How to best do enums with (RDF) Things?
    enum Kind {
        Class,
        Interface,
        Enum,
        Record,
        Primitives,
        Array,
        Void
    }

    interface Builder<B extends Type> extends Thing.Builder<B> { // skipcq: JAVA-E0169

        Builder<B> label(String label);

        Builder<B> pkg(Package pkg);

        Builder<B> kind(Kind kind);

        Builder<B> addUses(Type uses);

        Builder<B> addParents(Type parents);
    }
}

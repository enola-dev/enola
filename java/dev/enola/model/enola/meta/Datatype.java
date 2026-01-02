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
import dev.enola.thing.java.TBF;

import org.jspecify.annotations.Nullable;

import java.net.URI;

public interface Datatype extends Type {

    String CLASS_IRI = "https://enola.dev/meta/Datatype";

    // Intentionally only singular instead of multiple
    default Datatype parent() {
        return getThing(KIRI.E.PARENT, Datatype.class).get();
    }

    default @Nullable String java() {
        return getString(KIRI.E.META.JAVA);
    }

    default @Nullable String proto() {
        return getString(KIRI.E.META.PROTO);
    }

    default @Nullable URI xsd() {
        return get(KIRI.E.META.XSD, URI.class);
    }

    // TODO Pattern regExp();

    interface Builder<B extends Datatype> extends Datatype, Type.Builder<B> { // skipcq: JAVA-E0169

        @Override
        default Datatype.Builder<B> schema(Schema schema) {
            Type.Builder.super.schema(schema);
            return this;
        }

        @Override
        default Datatype.Builder<B> name(String name) {
            Type.Builder.super.name(name);
            return this;
        }

        default Datatype.Builder<B> parent(Datatype datatype) {
            set(KIRI.E.PARENT, datatype);
            return this;
        }

        default Datatype.Builder<B> java(String java) {
            set(KIRI.E.META.JAVA, java);
            return this;
        }

        default Datatype.Builder<B> proto(String proto) {
            set(KIRI.E.META.PROTO, proto);
            return this;
        }

        default Datatype.Builder<B> xsd(URI xsd) {
            set(KIRI.E.META.XSD, xsd);
            return this;
        }
    }

    @SuppressWarnings("unchecked")
    static Datatype.Builder<Datatype> builder(TBF tbf) {
        return tbf.create(Datatype.Builder.class, Datatype.class);
    }
}

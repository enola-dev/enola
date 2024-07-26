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
package dev.enola.model.enola.meta;

import java.net.URI;

public interface Datatype extends Type {

    String CLASS_IRI = "https://enola.dev/meta/Datatype";

    String java();

    // TODO @IRI(KIRI.E.META.PARENT)
    // Intentionally only singular instead of multiple
    Datatype parent();

    URI xsd();

    // TODO Pattern regExp();

    interface Builder<B extends Datatype> extends Datatype, Type.Builder<B> { // skipcq: JAVA-E0169

        @Override
        Datatype.Builder<B> schema(Schema schema);

        @Override
        Datatype.Builder<B> name(String name);

        Datatype.Builder<B> parent(Datatype datatype);

        Datatype.Builder<B> java(String datatype);

        Datatype.Builder<B> xsd(URI xsd);
    }
}

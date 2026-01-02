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
package dev.enola.model;

import static com.google.common.collect.Iterables.concat;

import dev.enola.datatype.Datatype;
import dev.enola.datatype.DatatypeRepository;
import dev.enola.datatype.DatatypeRepositoryBuilder;

/**
 * All built-in Datatypes. Aggregates XSD {@link dev.enola.model.xsd.Datatypes} and Enola {@link
 * dev.enola.model.enola.Datatypes} and Schema.org {@link dev.enola.model.schemaorg.Datatypes}.
 */
// TODO Doc: <p>See https://enola.dev/datatypes --- once we put something on
// https://docs.enola.dev/models/datatypes and have a working HTTP redirector.
public final class Datatypes {
    // NB: Cannot move dev.enola.model.Datatypes to package dev.enola.datatype,
    // because this depends on x3+ dev.enola.model sub-packages. But with AutoService,
    // this should not be required anymore at all, #later.

    // Nota Bene: There's no NULL here - the absence of a value is not a Datatype!

    // TODO Compose ALL using Auto-Service

    // TODO Eventually replace this class with a declarative models/schema.org/datatypes.ttl ?

    // TODO MAP, LIST etc. from https://yaml.org/type/

    // Beware: The order here matters very much, for DatatypeRepository#match()
    public static final Iterable<Datatype<?>> ALL =
            concat(
                    dev.enola.model.xsd.Datatypes.ALL,
                    dev.enola.model.enola.Datatypes.ALL,
                    dev.enola.model.schemaorg.Datatypes.ALL);

    /**
     * {@link DatatypeRepository} of fixed known built-in datatypes.
     *
     * <p>This is primarily intended for unit tests; real applications typically use another
     * implementation, which may well permit more "dynamic" datatypes.
     */
    @SuppressWarnings("unchecked")
    public static final DatatypeRepository DTR =
            new DatatypeRepositoryBuilder().storeAll(Datatypes.ALL).build();

    private Datatypes() {}
}

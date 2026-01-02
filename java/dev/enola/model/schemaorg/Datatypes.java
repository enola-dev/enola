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
package dev.enola.model.schemaorg;

import com.google.common.collect.ImmutableList;

import dev.enola.datatype.Datatype;

import java.time.LocalDate;

/** Datatypes for <a href="https://schema.org/">Schema.org</a>. */
public final class Datatypes {

    public static final Datatype<LocalDate> DATE =
            dev.enola.model.xsd.Datatypes.DATE.child().iri("https://schema.org/Date").build();

    // Beware: The order here may matter very much, for DatatypeRepository#match()
    public static final Iterable<Datatype<?>> ALL = ImmutableList.of(DATE);

    private Datatypes() {}
}

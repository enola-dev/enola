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
package dev.enola.model.schemaorg;

import dev.enola.datatype.Datatype;
import dev.enola.datatype.ImmutableDatatype;

import java.time.LocalDate;

/** Datatypes for <a href="https://schema.org/">Schema.org</a> */
public final class Datatypes {

    // TODO How-to copy/paste even less from dev.enola.model.xsd.Datatypes.DATE?
    public static final Datatype<LocalDate> DATE =
            new ImmutableDatatype<>(
                    "https://schema.org/Date",
                    dev.enola.model.xsd.Datatypes.DATE.stringConverter(),
                    LocalDate.class,
                    dev.enola.model.xsd.Datatypes.DATE.pattern().get());

    private Datatypes() {}
}

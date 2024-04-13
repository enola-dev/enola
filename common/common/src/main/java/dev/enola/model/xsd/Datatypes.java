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
package dev.enola.model.xsd;

import dev.enola.common.convert.string.TemporalToStringBiConverters;
import dev.enola.datatype.Datatype;
import dev.enola.datatype.ImmutableDatatype;

import java.time.LocalDate;

/** Datatypes for <a href="http://www.w3.org/TR/xmlschema-2/">XML Schema (XSD)</a>. */
public final class Datatypes {
    // TODO Eventually replace this class with a declarative model/schema.org/datatypes.ttl ?
    // TODO Hack sth. which scans for an @Things and introspects constants and writes them out?!

    public static final Datatype<LocalDate> DATE =
            new ImmutableDatatype<>(
                    "http://www.w3.org/2001/XMLSchema#date",
                    TemporalToStringBiConverters.LOCAL_DATE,
                    LocalDate.class,
                    // TODO Make LocalDate RegExp more fancy...
                    // https://www.oreilly.com/library/view/regular-expressions-cookbook/9781449327453/ch04s07.html
                    "(?<year>[0-9]{4})-?(?<month>1[0-2]|0[1-9])-?(?<day>3[01]|0[1-9]|[12][0-9])");

    private Datatypes() {}
}

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

import dev.enola.common.convert.ObjectToStringBiConverters;
import dev.enola.datatype.Datatype;
import dev.enola.datatype.ImmutableDatatype;

import java.time.Instant;
import java.time.LocalDate;

/** Datatypes for <a href="http://www.w3.org/TR/xmlschema-2/">XML Schema (XSD)</a>. */
public final class Datatypes {
    // TODO Eventually replace this class with a declarative models/schema.org/datatypes.ttl ?
    // TODO Hack sth. which scans for an @Things and introspects constants and writes them out?!

    public static final Datatype<LocalDate> DATE =
            new ImmutableDatatype<>(
                    "http://www.w3.org/2001/XMLSchema#date",
                    ObjectToStringBiConverters.LOCAL_DATE,
                    LocalDate.class,
                    // TODO Make LocalDate RegExp more fancy...
                    // https://www.oreilly.com/library/view/regular-expressions-cookbook/9781449327453/ch04s07.html
                    "(?<year>[0-9]{4})-?(?<month>1[0-2]|0[1-9])-?(?<day>3[01]|0[1-9]|[12][0-9])");

    public static final Datatype<Integer> INT =
            new ImmutableDatatype<>(
                    "http://www.w3.org/2001/XMLSchema#int",
                    ObjectToStringBiConverters.INT,
                    Integer.class,
                    // TODO Test coverage for this INT RegExp...
                    "-?0*(?:214748364[0-7]|21474836[0-3]\\d|2147483[0-5]\\d{2}|214748[0-2]\\d{3}|21474[0-7]\\d{4}|2147[0-3]\\d{5}|214[0-6]\\d{6}|21[0-3]\\d{7}|20\\d{8}|1\\d{9}|[1-9]\\d{1,8}|0)");

    public static final Datatype<Instant> DATE_TIME =
            new ImmutableDatatype<>(
                    "http://www.w3.org/2001/XMLSchema#dateTime",
                    ObjectToStringBiConverters.INSTANT,
                    Instant.class,
                    // TODO Test coverage for this INT RegExp...
                    "(\\d{4})-(\\d{2})-(\\d{2})T([0-9:]+)(Z|([+-])(\\d{2})(:(\\d{2})?)?)?");

    private Datatypes() {}
}

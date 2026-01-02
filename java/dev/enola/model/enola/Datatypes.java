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
package dev.enola.model.enola;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.UnsignedLong;

import dev.enola.common.ByteSeq;
import dev.enola.common.convert.ObjectToStringBiConverters;
import dev.enola.datatype.Datatype;
import dev.enola.datatype.ImmutableDatatype;
import dev.enola.thing.KIRI;

import java.nio.file.attribute.FileTime;
import java.time.Instant;

/** Enola's built-in core datatypes. */
// TODO Doc: <p>See https://enola.dev/datatypes --- once we put something on
// https://docs.enola.dev/models/datatypes and have a working HTTP redirector.
public final class Datatypes {

    public static final Datatype<Instant> TIMESTAMP =
            dev.enola.model.xsd.Datatypes.DATE_TIME
                    .child()
                    .iri("https://enola.dev/Timestamp")
                    .build();

    public static final Datatype<UnsignedLong> UNSIGNED_LONG =
            new ImmutableDatatype<>(
                    "https://enola.dev/UnsignedLong",
                    ObjectToStringBiConverters.UNSIGNED_LONG,
                    UnsignedLong.class);

    // IRI_TEMPLATE "https://enola.dev/IRITemplate" isn't really required.

    public static final Datatype<FileTime> FILE_TIME =
            new ImmutableDatatype<>(
                    "https://enola.dev/FileTime",
                    ObjectToStringBiConverters.FILE_TIME,
                    FileTime.class);

    public static final Datatype<ByteSeq> BINARY =
            new ImmutableDatatype<>(
                    KIRI.E.BINARY,
                    ObjectToStringBiConverters.MULTIBASE,
                    ByteSeq.class,
                    // NB: Multibase Pattern is also in binary.ttl
                    "[0179fFvVtTbBcChkKRzZmMuUpQ/🚀][^\\s]*");

    // Beware: The order here matters very much, for DatatypeRepository#match()
    public static final Iterable<Datatype<?>> ALL =
            ImmutableList.of(UNSIGNED_LONG, TIMESTAMP, FILE_TIME);

    private Datatypes() {}
}

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
package dev.enola.datatype;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.UnsignedLong;

import dev.enola.common.convert.ObjectToStringBiConverters;

import java.net.URI;
import java.nio.file.attribute.FileTime;

/** Enola's built-in core datatypes. */
// TODO Doc: <p>See https://enola.dev/datatypes --- once we put something on
// https://docs.enola.dev/models/datatypes and have a working HTTP redirector.
public final class Datatypes {

    // TODO Rethink and clear-up the current mix between XSD and Enola IRIs...

    // TODO Move dev.enola.datatype.Datatypes to dev.enola.model.xsd.Datatypes!

    // Nota Bene: There's no NULL here - the absence of a value is not a Datatype!

    // TODO MAP, LIST etc. from https://yaml.org/type/

    public static final Datatype<String> STRING =
            new ImmutableDatatype<>(
                    "http://www.w3.org/2001/XMLSchema#string",
                    ObjectToStringBiConverters.STRING,
                    String.class,
                    "(?s).*");

    public static final Datatype<Boolean> BOOLEAN =
            new ImmutableDatatype<>(
                    "http://www.w3.org/2001/XMLSchema#boolean",
                    ObjectToStringBiConverters.BOOLEAN,
                    Boolean.class,
                    // Subset of https://yaml.org/type/bool.html
                    "true|True|TRUE|false|False|FALSE");

    private static final String IRI_PATTERN =
            "(<([a-zA-Z][a-zA-Z+-\\.]*:\\S+)>)|([a-zA-Z][a-zA-Z+-\\.]*:\\S+)";
    public static final Datatype<URI> IRI =
            new ImmutableDatatype<>(
                    "http://www.w3.org/2001/XMLSchema#anyURI",
                    ObjectToStringBiConverters.URI,
                    URI.class, // TODO Make this URI.class!
                    IRI_PATTERN);

    public static final Datatype<UnsignedLong> UNSIGNED_LONG =
            new ImmutableDatatype<>(
                    "https://enola.dev/UnsignedLong",
                    ObjectToStringBiConverters.UNSIGNED_LONG,
                    UnsignedLong.class);

    public static final Datatype<FileTime> FILE_TIME =
            new ImmutableDatatype<>(
                    "https://enola.dev/FileTime",
                    ObjectToStringBiConverters.FILE_TIME,
                    FileTime.class);

    // TODO BINARY ... multibase ... with https://github.com/multiformats/java-multibase, or
    // https://github.com/filip26/copper-multibase, for https://github.com/multiformats/multibase.
    // Replace use of Base64.getEncoder().encodeToString() in MessageToThingConverter#b64()

    // Beware: The order here matters very much, for DatatypeRepository#match()
    public static final Iterable<Datatype<?>> ALL =
            ImmutableList.of(BOOLEAN, IRI, STRING, UNSIGNED_LONG, FILE_TIME);

    public static final DatatypeRepository DTR =
            new DatatypeRepositoryBuilder().store(Datatypes.ALL).build();

    private Datatypes() {}
}

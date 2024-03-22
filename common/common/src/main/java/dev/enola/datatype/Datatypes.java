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

import dev.enola.common.convert.PrimitiveObjectToStringBiConverters;

/** Enola's built-in core datatypes. */
// TODO Doc: <p>See https://enola.dev/datatypes --- once we put something on
// https://docs.enola.dev/models/datatypes and have a working HTTP redirector.
public final class Datatypes {

    // Nota Bene: There's no NULL here - the absence of a value is not a Datatype!

    // TODO MAP, LIST etc. from https://yaml.org/type/

    public static final Datatype<String> STRING =
            new ImmutableDatatype<>(
                    "http://www.w3.org/2001/XMLSchema#string",
                    PrimitiveObjectToStringBiConverters.STRING,
                    String.class,
                    "(?s).*");

    public static final Datatype<Boolean> BOOLEAN =
            new ImmutableDatatype<>(
                    "http://www.w3.org/2001/XMLSchema#boolean",
                    PrimitiveObjectToStringBiConverters.BOOLEAN,
                    Boolean.class,
                    // Subset of https://yaml.org/type/bool.html
                    "true|True|TRUE|false|False|FALSE");

    private static final String IRI_PATTERN = "[a-zA-Z][a-zA-Z+-\\.]*:\\S+";
    public static final Datatype<String> IRI =
            new ImmutableDatatype<>(
                    "http://www.w3.org/2001/XMLSchema#anyURI",
                    PrimitiveObjectToStringBiConverters.STRING,
                    String.class,
                    IRI_PATTERN);

    // TODO public static final Datatype NUMBER = new NumberDatatype();

    // TODO TIMESTAMP

    // TODO BINARY ... multibase ... with https://github.com/multiformats/java-multibase, or
    // https://github.com/filip26/copper-multibase, for https://github.com/multiformats/multibase.
    // Replace use of Base64.getEncoder().encodeToString() in MessageToThingConverter#b64()

    // Beware: The order here matters very much, for DatatypeRepository#match()
    public static final Iterable<Datatype<?>> ALL = ImmutableList.of(BOOLEAN, IRI, STRING);

    private Datatypes() {}
}

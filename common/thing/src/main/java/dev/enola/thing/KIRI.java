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
package dev.enola.thing;

import dev.enola.thing.proto.Value.Literal;

/**
 * Java constants for some "well-known" IRIs used in Enola's code. This is NOT a list of all
 * possible such IRIs, because that's by definition an open set. This is merely for convenient use
 * in code.
 */
public final class KIRI {

    /**
     * XML Schema's built-in datatypes. These are used as Things' Literal's Datatypes in {@link
     * Literal#getDatatype()}. See <a href="https://www.w3.org/TR/rdf11-concepts/#xsd-datatypes">RDF
     * 1.1 Concepts XSD datatypes</a>, based (of course) on the <a
     * href="https://www.w3.org/TR/xmlschema11-2/">XML Schema 1.1 datatypes</a>.
     */
    public static final class XSD {
        private static final String NS = "http://www.w3.org/2001/XMLSchema#";

        public static final String BOOL = NS + "boolean";

        public static final String DOUBLE = NS + "double";
        public static final String FLOAT = NS + "float";

        public static final String INT32 = NS + "int";
        public static final String INT64 = NS + "long";
        public static final String UINT64 = NS + "unsignedLong";
        public static final String UINT32 = NS + "unsignedInt";

        public static final String BIN64 = NS + "base64Binary";

        public static final String TS = NS + "dateTime";

        private XSD() {}
    }

    private KIRI() {}
}

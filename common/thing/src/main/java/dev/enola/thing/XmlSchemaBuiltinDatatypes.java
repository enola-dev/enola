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

/**
 * Constants for XML Schema's built-in datatypes. See e.g. <a
 * href="https://www.w3.org/TR/rdf11-concepts/#xsd-datatypes">https://www.w3.org/TR/rdf11-concepts/#xsd-datatypes</a>.
 */
public final class XmlSchemaBuiltinDatatypes {

    private static final String NS = "http://www.w3.org/2001/XMLSchema#";

    public static final String INT = NS + "int";
    public static final String UINT32 = NS + "unsignedInt";

    public static final String TS = NS + "dateTime";

    private XmlSchemaBuiltinDatatypes() {}
}

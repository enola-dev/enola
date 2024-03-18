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
package dev.enola.rdf;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.thing.XmlSchemaBuiltinDatatypes;

import org.eclipse.rdf4j.model.vocabulary.XSD;
import org.junit.Test;

/** Test datatype IRIs. */
public class DatatypesTest {

    @Test
    public void XSD() {
        assertThat(XmlSchemaBuiltinDatatypes.BIN64).isEqualTo(XSD.BASE64BINARY.stringValue());
        assertThat(XmlSchemaBuiltinDatatypes.BOOL).isEqualTo(XSD.BOOLEAN.stringValue());
        assertThat(XmlSchemaBuiltinDatatypes.DOUBLE).isEqualTo(XSD.DOUBLE.stringValue());
        assertThat(XmlSchemaBuiltinDatatypes.FLOAT).isEqualTo(XSD.FLOAT.stringValue());
        assertThat(XmlSchemaBuiltinDatatypes.INT32).isEqualTo(XSD.INT.stringValue());
        assertThat(XmlSchemaBuiltinDatatypes.INT64).isEqualTo(XSD.LONG.stringValue());
        assertThat(XmlSchemaBuiltinDatatypes.UINT32).isEqualTo(XSD.UNSIGNED_INT.stringValue());
        assertThat(XmlSchemaBuiltinDatatypes.UINT64).isEqualTo(XSD.UNSIGNED_LONG.stringValue());
        assertThat(XmlSchemaBuiltinDatatypes.TS).isEqualTo(XSD.DATETIME.stringValue());
    }
}

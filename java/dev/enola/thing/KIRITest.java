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
package dev.enola.thing;

import static com.google.common.truth.Truth.assertThat;

import org.eclipse.rdf4j.model.base.CoreDatatype.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.XSD;
import org.junit.Test;

/** Tests IRIs defined in {@link KIRI}, by comparing them against their RDF4j equivalents. */
public class KIRITest {

    @Test
    public void XSD() {
        assertThat(KIRI.XSD.IRI).isEqualTo(XSD.ANYURI.stringValue());
        assertThat(KIRI.XSD.STRING).isEqualTo(XSD.STRING.stringValue());
        assertThat(KIRI.XSD.BIN64).isEqualTo(XSD.BASE64BINARY.stringValue());
        assertThat(KIRI.XSD.BOOL).isEqualTo(XSD.BOOLEAN.stringValue());
        assertThat(KIRI.XSD.DOUBLE).isEqualTo(XSD.DOUBLE.stringValue());
        assertThat(KIRI.XSD.FLOAT).isEqualTo(XSD.FLOAT.stringValue());
        assertThat(KIRI.XSD.INT32).isEqualTo(XSD.INT.stringValue());
        assertThat(KIRI.XSD.INT64).isEqualTo(XSD.LONG.stringValue());
        assertThat(KIRI.XSD.UINT32).isEqualTo(XSD.UNSIGNED_INT.stringValue());
        assertThat(KIRI.XSD.UINT64).isEqualTo(XSD.UNSIGNED_LONG.stringValue());
        assertThat(KIRI.XSD.TS).isEqualTo(XSD.DATETIME.stringValue());
    }

    @Test
    public void RDF() {
        assertThat(KIRI.RDF.HTML).isEqualTo(RDF.HTML.getIri().stringValue());
    }

    @Test
    public void RDFS() {
        assertThat(KIRI.RDFS.CLASS).isEqualTo(RDFS.CLASS.stringValue());
        assertThat(KIRI.RDFS.LABEL).isEqualTo(RDFS.LABEL.stringValue());
    }
}

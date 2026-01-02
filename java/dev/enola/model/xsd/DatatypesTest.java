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
package dev.enola.model.xsd;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.common.convert.ConversionException;
import dev.enola.datatype.Datatype;
import dev.enola.datatype.ImmutableDatatype;

import org.junit.Test;

import java.net.URI;

public class DatatypesTest {

    public @Test void testImplementationWithOnlyIRI() throws ConversionException {
        checkInvariants(new ImmutableDatatype<>("http://test/"));
    }

    public @Test void string() throws ConversionException {
        var datatype = Datatypes.STRING;
        checkInvariants(datatype);
        assertThat(datatype.pattern()).isPresent();
        assertThat(datatype.javaType()).hasValue(String.class);
        assertThat(datatype.stringConverter().convertFrom("hello")).isEqualTo("hello");
    }

    public @Test void bool() throws ConversionException {
        var datatype = Datatypes.BOOLEAN;
        checkInvariants(datatype);
        assertThat(datatype.pattern()).isPresent();
        assertThat(datatype.javaType()).hasValue(Boolean.class);
        assertThat(datatype.stringConverter().convertFrom("TrUE")).isEqualTo(true);
    }

    public @Test void iri() throws ConversionException {
        var datatype = Datatypes.IRI;
        checkInvariants(datatype);
        assertThat(datatype.pattern()).isPresent();
        assertThat(datatype.javaType()).hasValue(URI.class);
        assertThat(datatype.stringConverter().convertFrom("https://enola.dev"))
                .isEqualTo(URI.create("https://enola.dev"));
    }

    void checkInvariants(Datatype<?> datatype) {
        assertThat(datatype.iri()).isNotEmpty();
        assertThat(datatype.pattern()).isNotNull();
        assertThat(datatype.stringConverter()).isNotNull();
        assertThat(datatype.javaType()).isNotNull();
    }
}

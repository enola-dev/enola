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

import static com.google.common.truth.Truth.assertThat;

import com.google.common.primitives.UnsignedLong;

import dev.enola.common.convert.ConversionException;
import dev.enola.datatype.Datatype;

import org.junit.Test;

import java.io.IOException;

public class DatatypesTest {

    public @Test void unsignedLong() throws ConversionException, IOException {
        var datatype = Datatypes.UNSIGNED_LONG;
        checkInvariants(datatype);
        assertThat(datatype.javaType()).hasValue(UnsignedLong.class);
        assertThat(datatype.stringConverter().convertTo(UnsignedLong.MAX_VALUE))
                .isEqualTo("18446744073709551615");
        assertThat(datatype.stringConverter().convertToType(UnsignedLong.MAX_VALUE, String.class))
                .hasValue("18446744073709551615");
    }

    void checkInvariants(Datatype<?> datatype) {
        assertThat(datatype.iri()).isNotEmpty();
        assertThat(datatype.pattern()).isNotNull();
        assertThat(datatype.stringConverter()).isNotNull();
        assertThat(datatype.javaType()).isNotNull();
    }
}

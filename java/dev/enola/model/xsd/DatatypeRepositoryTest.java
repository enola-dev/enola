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

import dev.enola.datatype.DatatypeRepositoryBuilder;

import org.junit.Test;

public class DatatypeRepositoryTest {

    @Test
    public void match() {
        var r = new DatatypeRepositoryBuilder().storeAll(Datatypes.ALL).build();
        assertThat(r.match("hello, world")).hasValue(Datatypes.STRING);
        assertThat(r.match("hello,\n\tworld")).hasValue(Datatypes.STRING);

        assertThat(r.match("true")).hasValue(Datatypes.BOOLEAN);
        assertThat(r.match("FALSE")).hasValue(Datatypes.BOOLEAN);

        assertThat(r.match("xsd:int")).hasValue(Datatypes.IRI);
        assertThat(r.match("https://enola.dev/emoji")).hasValue(Datatypes.IRI);
        // assertThat(r.match("<https://enola.dev/emoji>")).hasValue(Datatypes.IRI);
        // assertThat(r.match("[xsd:int]")).hasValue(Datatypes.IRI);

        // TODO assertThat(r.match("123")).hasValue(Datatypes.NUMBER);
    }
}

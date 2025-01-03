/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024-2025 The Enola <https://enola.dev> Authors
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
package dev.enola.common.io.iri.namespace;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class NamespaceConverterTest {

    @Test
    public void schemaOrgName() {
        var standard = NamespaceRepositoryEnolaDefaults.INSTANCE;
        var convert = new NamespaceConverterWithRepository(standard);
        assertThat(convert.toIRI("schema:name")).isEqualTo("https://schema.org/name");
        assertThat(convert.toCURIE("https://schema.org/name")).isEqualTo("schema:name");
    }

    @Test
    public void empty() {
        var empty = new NamespaceRepositoryBuilder().store("", "https://schema.org/").build();
        var convert = new NamespaceConverterWithRepository(empty);
        assertThat(convert.toIRI(":name")).isEqualTo("https://schema.org/name");
        assertThat(convert.toCURIE("https://schema.org/name")).isEqualTo(":name");
    }

    @Test
    public void unknown() {
        var emptyRepository = new NamespaceRepositoryBuilder().build();
        var convert = new NamespaceConverterWithRepository(emptyRepository);
        assertThat(convert.toIRI(":name")).isEqualTo(":name");
        assertThat(convert.toCURIE("https://schema.org/name")).isEqualTo("https://schema.org/name");
    }
}

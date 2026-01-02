/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025-2026 The Enola <https://enola.dev> Authors
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
package dev.enola.data.iri;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.util.HashMap;

public class IRITest {

    @Test
    public void curie() {
        var iri = IRI.from("https://enola.dev/tika/", "author");
        assertThat(iri.toString()).isEqualTo("https://enola.dev/tika/author");
    }

    @Test
    public void equalsOfSameFrom() {
        var iri1 = IRI.from("https://enola.dev/tika/", "author");
        var iri2 = IRI.from("https://enola.dev/tika/", "author");
        assertThat(iri1).isEqualTo(iri2);
    }

    @Test
    public void equalsOfDifferentFrom() {
        var iri1 = IRI.from("https://schema.org/name");
        var iri2 = IRI.from("https://schema.org/", "name");
        assertThat(iri1).isEqualTo(iri2);
    }

    @Test
    public void mapRemove1() {
        var map = new HashMap<IRI, String>();
        IRI iri = IRI.from("https://enola.dev/tika/", "author");
        map.put(iri, "hi");
        assertThat(map.remove(iri)).isEqualTo("hi");
        assertThat(map).isEmpty();
    }

    @Test
    public void mapRemove2() {
        var map = new HashMap<IRI, String>();
        IRI iri1 = IRI.from("https://enola.dev/tika/", "author");
        map.put(iri1, "hi");

        IRI iri2 = IRI.from("https://enola.dev/tika/", "author");
        assertThat(map.remove(iri2)).isEqualTo("hi");
        assertThat(map).isEmpty();
    }
}

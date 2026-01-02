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
package dev.enola.data.id;

import static com.google.common.truth.Truth.assertThat;

import dev.enola.data.iri.IRI;

import org.junit.Test;

public class UUID_IRI_Test {

    @Test
    public void uuid() {
        var uuidIRI = new UUID_IRI(); // TODO IRI.newUUID();
        assertThat(uuidIRI.toString()).startsWith("urn:uuid:");
        assertThat(uuidIRI.toString().length()).isEqualTo(45);

        var uuidIRI2 = IRI.from(uuidIRI.toString());
        assertThat(uuidIRI).isEqualTo(uuidIRI2);
    }
}

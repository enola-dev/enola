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
package dev.enola.common.io.resource;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class MultibaseResourceTest {

    @Test
    public void hex() throws IOException {
        assertThat(new MultibaseResource(URI.create("multibase:f0a3f")).byteSource().read())
                .isEqualTo(new byte[] {0x0a, 0x3f});
    }

    @Test(expected = IllegalArgumentException.class)
    public void spaceIsInvalid() {
        new DataResource(URI.create("multibase:f0 a3f"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void empty() {
        new MultibaseResource(URI.create(""));
    }
}

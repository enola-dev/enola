/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2025 The Enola <https://enola.dev> Authors
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

import com.google.common.net.MediaType;

import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class TestResourceTest {

    ResourceProviders rp = new ResourceProviders(new TestResource.Provider());

    @Test
    public void testMemory() throws IOException {
        // Direct
        try (var r = TestResource.create(MediaType.PLAIN_TEXT_UTF_8)) {
            checkMemory(r);
        }

        // Indirect via factory and with fancy URI with ?mediaType= parameter
        try (var r1 = TestResource.create(MediaType.JSON_UTF_8)) {
            var r2 = rp.getResource(URI.create(r1.uri().toString()));
            assertThat(r2.mediaType()).isEqualTo(MediaType.JSON_UTF_8);
            checkMemory(r2);
        }
    }

    private void checkMemory(Resource r) throws IOException {
        r.charSink().write("hi");
        var uri = r.uri();
        var text = rp.getResource(uri).charSource().read();
        assertThat(text).isEqualTo("hi");
    }
}

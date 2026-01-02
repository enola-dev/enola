/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2026 The Enola <https://enola.dev> Authors
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
import java.io.InputStream;

public class NullResourceTest {
    @Test
    public <is> void testNullResource() throws IOException {
        var e = NullResource.INSTANCE;
        assertThat(e.byteSource().isEmpty()).isFalse();
        assertThat(e.mediaType()).isNotNull();
        assertThat(e.uri()).isNotNull();

        try (InputStream is = e.byteSource().openStream()) {
            assertThat(is.read()).isEqualTo(0);
        }

        e.byteSink().write(new byte[3]);
    }
}

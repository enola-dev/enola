/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2024 The Enola <https://enola.dev> Authors
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

import static com.google.common.net.MediaType.*;
import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;

import static org.junit.Assert.assertThrows;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class MemoryResourceTest {

    public @Rule SingletonRule r = $(MediaTypeProviders.set());

    private static final byte[] BYTES = new byte[] {1, 2, 3};
    private static final String TEXT = "hello, world";

    @Test
    public void testBinaryMemoryResource() throws IOException {
        MemoryResource resource = new MemoryResource(OCTET_STREAM);
        resource.byteSink().write(BYTES);
        assertThat(resource.byteSource().read()).isEqualTo(BYTES);

        assertThrows(IllegalStateException.class, () -> resource.charSink());
        assertThrows(IllegalStateException.class, () -> resource.charSource());
    }

    @Test
    public void testTextMemoryResource() throws IOException {
        MemoryResource resource = new MemoryResource(PLAIN_TEXT_UTF_8);
        resource.charSink().write(TEXT);
        assertThat(resource.charSource().read()).isEqualTo(TEXT);
    }

    @Test
    public void testMediaTypePrecedence() throws IOException {
        // This does not work for PLAIN_TEXT_UTF_8, because that's "special"
        // (It's one of a few MediaTypes which MediaTypeDetector always overrides)
        MemoryResource resource = new MemoryResource(URI.create("test.html"), GZIP);
        assertThat(resource.mediaType()).isEqualTo(GZIP);
    }
}

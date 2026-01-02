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
package dev.enola.common.io.resource.stream;

import static com.google.common.truth.Truth.assertThat;

import static dev.enola.common.context.testlib.SingletonRule.$;
import static dev.enola.common.io.resource.FileDescriptorResource.STDOUT_URI;

import dev.enola.common.context.testlib.SingletonRule;
import dev.enola.common.io.mediatype.MediaTypeProviders;
import dev.enola.common.io.resource.ResourceProvider;
import dev.enola.common.io.resource.ResourceProviders;

import org.junit.Rule;
import org.junit.Test;

import java.net.URI;

public class WritableResourcesProviderTest {

    public @Rule SingletonRule r = $(MediaTypeProviders.set(new MediaTypeProviders()));

    private final ResourceProvider rp = new ResourceProviders();
    private final WritableResourcesProvider wrp = new WritableResourcesProvider(rp);

    @Test
    public void stdout() {
        assertThat(wrp.getWritableResource(STDOUT_URI, URI.create("file:/hello.txt")).uri())
                .isEqualTo(STDOUT_URI);
    }

    @Test
    public void directory() {
        assertThat(
                        wrp.getWritableResource(
                                        URI.create("file:/tmp/test/"),
                                        URI.create("file:/etc/hello.txt"))
                                .uri())
                .isEqualTo(URI.create("file:/tmp/test/etc/hello.txt"));
    }

    @Test
    public void file() {
        assertThat(
                        wrp.getWritableResource(
                                        URI.create("file:/tmp/hello.txt"),
                                        URI.create("file:/etc/hello.txt"))
                                .uri())
                .isEqualTo(URI.create("file:/tmp/hello.txt"));
    }
}

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

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class StringResourceTest {
    @Test
    public void testStringResource() throws IOException, URISyntaxException {
        var r1 = StringResource.of("hello, world");
        assertThat(r1.charSource().read()).isEqualTo("hello, world");

        var r2 = StringResource.of("# Models\n");
        assertThat(r2.charSource().read()).isEqualTo("# Models\n");

        // NB: new StringResource("") is not supported, because
        // URI.create("string:") causes an java.net.URISyntaxException.
        var r3 = StringResource.of("");
        assertThat(r3.charSource().read()).isEmpty();
    }
}

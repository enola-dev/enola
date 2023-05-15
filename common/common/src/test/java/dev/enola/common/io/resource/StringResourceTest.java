/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023 The Enola <https://enola.dev> Authors
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
        var r1 = new StringResource("hello, world");
        assertThat(r1.charSource().read()).isEqualTo("hello, world");

        var r2 = new StringResource("# Models\n");
        assertThat(r2.charSource().read()).isEqualTo("# Models\n");

        assertThat(new StringResource("").byteSource().size()).isEqualTo(0);
        assertThat(new StringResource("").charSource().length()).isEqualTo(0);
    }
}

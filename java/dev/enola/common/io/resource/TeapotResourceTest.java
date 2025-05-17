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

import org.junit.Test;

import java.io.IOException;
import java.net.URI;

public class TeapotResourceTest {

    @Test
    public void read() throws IOException {
        check("coffee:/");
        check("kafo://demo.enola.dev/pot-7?#syrup-type=Vanilla");
        check("Kaffee://demo.dév.dev/pot-7?#syrup-type=Vanilla");
    }

    private void check(String uri) throws IOException {
        var pot = new TeapotResource.Provider();
        var coffee = pot.getResource(URI.create(uri)).charSource().read();
        assertThat(coffee).isEqualTo("I'm a teapot");
    }

    @Test
    public void write() throws IOException {
        var pot = new TeapotResource.Provider();
        pot.getResource(URI.create("coffee:/")).charSink().write("ignore");
    }
}

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2024 The Enola <https://enola.dev> Authors
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

public class OkHttpResourceTest {

    // TODO Use https://square.github.io/okhttp/#mockwebserver

    @Test
    public void google() throws IOException {
        // TODO Remove ?charset=UTF-8 again later
        var r = new OkHttpResource("http://www.google.com?charset=UTF-8");
        assertThat(r.charSource().read()).startsWith("<!doctype html>");
    }
}

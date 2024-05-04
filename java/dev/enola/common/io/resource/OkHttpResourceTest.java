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

import com.google.common.net.MediaType;

import org.junit.Test;

import java.io.IOException;
import java.io.UncheckedIOException;

public class OkHttpResourceTest {

    // TODO Use https://square.github.io/okhttp/#mockwebserver

    @Test
    public void google() throws IOException {
        var r = new OkHttpResource("http://www.google.com");
        assertThat(r.charSource().read()).ignoringCase().contains("<!doctype html>");
        assertThat(r.mediaType()).isEqualTo(MediaType.HTML_UTF_8);
    }

    @Test(expected = IllegalArgumentException.class)
    public void google404() throws IOException {
        new OkHttpResource("http://www.google.com/bad").charSource().read();
    }

    @Test(expected = UncheckedIOException.class)
    public void connectTimeout() throws IOException {
        // NB: 203.0.113.1 is a non-routable IPv4 address; the cause includes Timeout
        new OkHttpResource("http://203.0.113.1").charSource().read();
    }
}

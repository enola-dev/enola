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
package dev.enola.web.sun.test;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.util.concurrent.Futures.immediateFuture;

import com.google.common.net.MediaType;

import dev.enola.common.io.resource.ResourceProviders;
import dev.enola.common.io.resource.StringResource;
import dev.enola.web.sun.SunServer;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;

public class SunServerTest {

    public static void main(String[] args) throws IOException {
        System.out.println(start().getInetAddress());
    }

    static SunServer start() throws IOException {
        var addr = new InetSocketAddress(0);
        var server = new SunServer(addr);

        var hello = new StringResource("hello, world", MediaType.PLAIN_TEXT_UTF_8);
        server.register("/hello", uri -> immediateFuture(hello));
        server.start();
        return server;
    }

    @Test
    public void testHello() throws IOException {
        var server = start();

        var rp = new ResourceProviders();
        // NOK with IPv6 :( var uri = URI.create("http://" + server.getInetAddress() + "/hello");
        var uri = URI.create("http://localhost:" + server.getInetAddress().getPort() + "/hello");
        var response = rp.getResource(uri);
        assertThat(response.mediaType()).isEqualTo(MediaType.PLAIN_TEXT_UTF_8);
        assertThat(response.charSource().read()).isEqualTo("hello, world");

        server.stop();
    }
}

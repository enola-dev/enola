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
import dev.enola.web.StaticWebHandler;
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

        server.register("/abc/xyz/", new StaticWebHandler("/abc/xyz/", "static"));

        server.start();
        return server;
    }

    @Test
    public void testHello() throws IOException {
        try (var server = start()) {
            var prefix = "http://localhost:" + server.getInetAddress().getPort();
            var rp = new ResourceProviders();

            // IPv6 NOK :( var uri = URI.create("http://" + server.getInetAddress() + "/hello");
            var uri1 = URI.create(prefix + "/hello");
            var response1 = rp.getResource(uri1);
            assertThat(response1.mediaType()).isEqualTo(MediaType.PLAIN_TEXT_UTF_8);
            assertThat(response1.charSource().read()).isEqualTo("hello, world");

            var uri2 = URI.create(prefix + "/abc/xyz/hello.txt");
            var response2 = rp.getResource(uri2);
            assertThat(response2.mediaType()).isEqualTo(MediaType.PLAIN_TEXT_UTF_8);
            assertThat(response2.charSource().read()).isEqualTo("hi, you\n");
        }
    }
}

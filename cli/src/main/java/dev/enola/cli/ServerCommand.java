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
package dev.enola.cli;

import static com.google.common.util.concurrent.Futures.immediateFuture;

import com.google.common.net.MediaType;

import dev.enola.common.io.resource.StringResource;
import dev.enola.web.sun.SunServer;

import picocli.CommandLine;

import java.net.InetSocketAddress;

@CommandLine.Command(name = "server", description = "Start HTTP Server")
public class ServerCommand implements CheckedRunnable {

    @CommandLine.Option(
            names = {"--httpPort"},
            required = true,
            description = "HTTP Port")
    int port;

    @Override
    public void run() throws Exception {
        var server = new SunServer(new InetSocketAddress(port));
        var hello = new StringResource("hello, world", MediaType.PLAIN_TEXT_UTF_8);
        server.register("/hello", uri -> immediateFuture(hello));
        server.start();
        System.out.println("Open http://localhost:9999/hello ...");
        Thread.currentThread().join();
    }
}

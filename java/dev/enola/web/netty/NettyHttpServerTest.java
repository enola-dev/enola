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
package dev.enola.web.netty;

import dev.enola.web.WebHandlers;
import dev.enola.web.WebServer;
import dev.enola.web.testlib.WebServerTestAbstract;

import org.junit.Test;

import java.io.IOException;

public class NettyHttpServerTest extends WebServerTestAbstract {

    @Override
    protected WebServer create(WebHandlers handlers) throws IOException {
        return new NettyHttpServer(0, handlers);
    }

    @Test
    public void testServer() throws IOException, InterruptedException {
        super.testServer();
    }
}
